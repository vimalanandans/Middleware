package com.bezirk.componentManager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.bezirk.R;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.AndroidDevice;
import com.bezirk.device.Device;
import com.bezirk.networking.AndroidNetworkManager;
import com.bezirk.persistence.DatabaseConnectionForAndroid;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.proxy.android.AndroidProxyServer;
import com.bezirk.proxy.android.ZirkMessageHandler;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.starter.helper.ActionProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentManager extends Service {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private SharedPreferences preferences;
    //private final Context context;
    private ActionProcessor actionProcessor;
    private AndroidProxyServer proxyServer;
    private ZyreCommsManager comms;
    private AndroidNetworkManager networkManager;
    private RegistryStorage registryStorage;
    private MessageHandler messageHandler;
    private LifecycleManager lifecycleManager;
    private Device device;
    private PubSubBroker pubSubBroker;
    private static final String DB_VERSION = "0.0.4";
    private LifecycleManager.LifecycleState currentState;

    int FOREGROUND_ID = 1336;
//                    service.startForeground(FOREGROUND_ID,
//                            service.buildForegroundNotification("Bezirk ON"));

    public ComponentManager() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger.debug("Creating Bezirk Service");

        //initialize lifecycle manager(Observable) for components(observers) to observe bezirk lifecycle events
        lifecycleManager = new LifecycleManager();

        //initialize android shared preferences for storing user preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //initialize action processor to manage intents fired to Bezirk
        actionProcessor = new ActionProcessor();

        //initialize network manager for handling wifi-management and getting network addressing information
        networkManager = new AndroidNetworkManager(preferences, this);

        //initialize message handler for sending events back to zirks
        messageHandler = new ZirkMessageHandler(this);

        //initialize data-storage for storing detailed component information like maps, objects
        try {
            registryStorage = new RegistryStorage(new DatabaseConnectionForAndroid(this), DB_VERSION);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

        //android device for getting information like deviceId, deviceName, etc
        device = new AndroidDevice();

        //initialize comms for communicating between devices over the wifi-network using zyre.
        comms = new ZyreCommsManager(networkManager, null, null);


        //initialize pub-sub Broker for filtering of events based on subscriptions and spheres(if present) & dispatching messages to other zirks within the same device or another device
        pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, null, null);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new AndroidProxyServer();

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);

        // add components as observers of bezirk lifecycle events.
        lifecycleManager.addObserver(comms);
        lifecycleManager.addObserver(networkManager);

        // this state is set only when the bezirk service is created the first time
        lifecycleManager.setState(LifecycleManager.LifecycleState.CREATED);
        currentState = LifecycleManager.LifecycleState.CREATED;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            actionProcessor.processBezirkAction(intent, proxyServer, new LifeCycleCallbacks() {
                @Override
                public void start() {
                    logger.debug("LifeCycleCallbacks:start");
                    lifecycleManager.setState(LifecycleManager.LifecycleState.STARTED);
                    //comms.startComms();
                    startForeground(FOREGROUND_ID, buildForegroundNotification("Bezirk ON"));
                }

                @Override
                public void stop() {
                    logger.debug("LifeCycleCallbacks:stop");
                    lifecycleManager.setState(LifecycleManager.LifecycleState.STOPPED);
                    stopSelf();
                }

                @Override
                public void destroy() {
                    logger.debug("LifeCycleCallbacks:destroy");
                    lifecycleManager.setState(LifecycleManager.LifecycleState.DESTROYED);
                    //comms.closeComms();
                }
            });
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface LifeCycleCallbacks {
        void start();

        void stop();

        void destroy();
    }

    public Notification buildForegroundNotification(String filename) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        Intent notificationIntent;
        PackageManager manager = getPackageManager();

        notificationIntent = manager.getLaunchIntentForPackage(getApplicationContext().getPackageName());

        if (notificationIntent == null) {
            notificationIntent = new Intent(Intent.ACTION_MAIN);
        }

        notificationIntent.setAction(Intent.ACTION_MAIN);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.setOngoing(true);

        notification.setContentIntent(pendingIntent);
        BitmapFactory.decodeResource(getResources(), R.drawable.upa_notification_s);
        notification.setContentTitle(getString(R.string.app_name))
                .setContentText(filename)
                /** Changed notification icon to white color. */

                //.setLargeIcon(bm)
                .setSmallIcon(R.drawable.upa_notification_s)
                .setTicker(getString(R.string.app_name)
                );

        return notification.build();
    }
}