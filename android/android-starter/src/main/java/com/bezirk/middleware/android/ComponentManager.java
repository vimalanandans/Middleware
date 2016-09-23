package com.bezirk.middleware.android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.bezirk.middleware.android.device.AndroidDevice;
import com.bezirk.middleware.android.logging.LoggingManager;
import com.bezirk.middleware.android.networking.AndroidNetworkManager;
import com.bezirk.middleware.android.persistence.DatabaseConnectionForAndroid;
import com.bezirk.middleware.android.proxy.android.AndroidProxyServer;
import com.bezirk.middleware.android.proxy.android.ZirkMessageHandler;
import com.bezirk.middleware.core.actions.BezirkAction;
import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;
import com.bezirk.middleware.core.comms.JmqCommsManager;
import com.bezirk.middleware.core.componentManager.LifeCycleCallbacks;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.identity.BezirkIdentityManager;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.streaming.StreamManager;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.identity.Alias;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.bezirk.middleware.android.comms.ZyreCommsManager;

public final class ComponentManager extends Service implements LifeCycleCallbacks {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private static final String ALIAS_KEY = "aliasName";


    private SharedPreferences preferences;
    //private final Context context;
    private ActionProcessor actionProcessor;
    private BezirkIdentityManager identityManager;
    private AndroidProxyServer proxyServer;
    //private ZyreCommsManager comms;
    private JmqCommsManager comms;
    private AndroidNetworkManager networkManager;
    private RegistryStorage registryStorage;
    private MessageHandler messageHandler;
    private LifeCycleObservable lifecyleObservable;
    private Config config;
    private LoggingManager loggingManager;
    private Device device;
    private PubSubBroker pubSubBroker;
    private RemoteLog remoteLog = null;
    private static final String DB_VERSION = "0.0.4";
    private LifeCycleObservable.State currentState;
    private String identityString;

    int FOREGROUND_ID = 1336;
//                    service.startForeground(FOREGROUND_ID,
//                            service.buildForegroundNotification("Bezirk ON"));

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            //Check if bezirk is starting for the first time, if yes process the start bezirk action
            if (currentState == null) {
                BezirkAction intentAction = BezirkAction.getActionFromString(intent.getAction());
                if (intentAction == BezirkAction.ACTION_START_BEZIRK) {
                    StartServiceAction startServiceAction = (StartServiceAction) intent.getSerializableExtra(BezirkAction.ACTION_START_BEZIRK.getName());
                    start(startServiceAction);
                } else {
                    logger.debug("Bezirk Action received " + intentAction + ". Bezirk is not running. " + BezirkAction.ACTION_START_BEZIRK + " required to start bezirk.");
                }
            } else {
                actionProcessor.processBezirkAction(intent, proxyServer, this);
            }
        } else {
            logger.debug("Intent received by Bezirk Service is null");
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("ibinder is not supperted");
        //return null;
    }

    @Override
    public void start(StartServiceAction startServiceAction) {
        //start bezirk is called for the first time
        if (currentState == null) {
            //create bezirk
            if (startServiceAction.getIdentity() != null) {
                this.identityString = startServiceAction.getIdentity();
                logger.debug("Received identityString in component manager: " + identityString);
            }
            config = startServiceAction.getConfig();
            create();
        }

        startForeground(FOREGROUND_ID, buildForegroundNotification(config.getAppName(), config.getAppName() + " ON", R.drawable.bezirk_notification_icon));

        logger.debug("LifeCycleCallbacks:start");
        lifecyleObservable.transition(LifeCycleObservable.Transition.START);
        currentState = lifecyleObservable.getState();
    }

    @Override
    public void stop(StopServiceAction stopServiceAction) {
        logger.debug("LifeCycleCallbacks:stop");
        lifecyleObservable.transition(LifeCycleObservable.Transition.STOP);
        currentState = lifecyleObservable.getState();
        stopSelf();
    }

    public final Notification buildForegroundNotification(String appName, String status, int icon) {
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
        //BitmapFactory.decodeResource(getResources(), R.drawable.upa_notification_s);
        notification.setContentTitle(appName)
                .setContentText(status)
                /** Changed notification icon to white color. */

                //.setLargeIcon(bm)
                .setSmallIcon(icon)
                .setTicker(appName
                );

        return notification.build();
    }

    private final void create() {
        logger.debug("Creating Bezirk Service");

        loggingManager = new LoggingManager(config);
        loggingManager.configure();

        //initialize lifecycle manager(Observable) for components(observers) to observe bezirk lifecycle events
        //lifecyleObservable = new LifecycleManager();
        lifecyleObservable = new LifeCycleObservable();

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
            logger.debug(e1.getMessage(), e1);
        }

        //android device for getting information like deviceId, deviceName, etc
        device = new AndroidDevice();

        //initialize comms for communicating between devices over the wifi-network using zyre.
        //comms = new ZyreCommsManager(networkManager, null, null, null);
        // testing the comms comms-jmq
        comms = new JmqCommsManager(networkManager, null, null, null);

        //initialize remoteLogging for logging the messages
        // remoteLog = new RemoteLoggingManager(comms, networkManager, null);

        initializeIdentityManager();

        //streaming manager
        Streaming streaming = new StreamManager(comms, networkManager);
        //initialize pub-sub Broker for filtering of events based on subscriptions and spheres(if present) & dispatching messages to other zirks within the same device or another device
        pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, identityManager, null, null, streaming, remoteLog);


        //initialize pub-sub Broker for filtering of events based on subscriptions and spheres(if present) & dispatching messages to other zirks within the same device or another device
        //pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, null, null, streaming);


        //TODO cleanup identity manager initialization
        //initialize the identity manager
        identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.getString(ALIAS_KEY, null);
        logger.debug("aliasString is " + aliasString);
        logger.debug("identityString received is " + identityString);
        final Gson gson = new Gson();
        final Alias identity;

        if (identityString != null) {
            logger.debug("Using received identity" + identityString);
            identity = gson.fromJson(aliasString, Alias.class);
            logger.trace("Setting Bezirk identity in preferences");

            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putString(ALIAS_KEY, gson.toJson(identity));
            preferencesEditor.commit();
        } else {
            if (aliasString == null) {
                identity = identityManager.createIdentity("BezirkUser");
                identityManager.setIdentity(identity);

                logger.trace("Created new Bezirk identity");

                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putString(ALIAS_KEY, gson.toJson(identity));
                preferencesEditor.commit();
            } else {
                logger.debug("Reusing identity" + aliasString);
                identity = gson.fromJson(aliasString, Alias.class);
            }
        }

        identityManager.setIdentity(identity);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new AndroidProxyServer(identityManager);

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);

        // add components as observers of bezirk lifecycle events.
        lifecyleObservable.addObserver(comms);
        lifecyleObservable.addObserver(networkManager);

        // this state is set only when the bezirk service is created the first time
        //TODO add create implementations for modules
        //lifecyleObservable.setState(LifecycleManager.LifecycleState.CREATED);
        //currentState = LifecycleManager.LifecycleState.CREATED;
    }

    void initializeIdentityManager() {
        //initialize the identity manager
        identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.getString(ALIAS_KEY, null);
        logger.debug("aliasString is " + aliasString);


        if (aliasString == null) {
            identityManager.createAndSetIdentity(aliasString);

            logger.trace("Created new Bezirk identity");

            SharedPreferences.Editor preferencesEditor = preferences.edit();
            preferencesEditor.putString(ALIAS_KEY, identityManager.getAliasString());
            preferencesEditor.commit();
        } else {
            logger.debug("Reusing identity" + aliasString);

            identityManager.createAndSetIdentity(aliasString);
        }

    }

}
