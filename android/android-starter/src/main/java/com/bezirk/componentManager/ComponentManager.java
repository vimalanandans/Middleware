package com.bezirk.componentManager;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.bezirk.comms.Comms;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.AndroidDevice;
import com.bezirk.device.Device;
import com.bezirk.networking.AndroidNetworkManager;
import com.bezirk.persistence.DatabaseConnectionForAndroid;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.proxy.android.AndroidProxyServer;
import com.bezirk.proxy.android.ProxyClientMessageHandler;
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
    private Comms comms;
    private AndroidNetworkManager networkManager;
    private RegistryStorage registryStorage;
    private MessageHandler messageHandler;
    private LifecycleManager lifecycleManager;
    private Device device;
    private static final String DB_VERSION = "0.0.4";

    public ComponentManager() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logger.info("Creating the Service");
        //get handle for preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //manage intents fired to Bezirk
        actionProcessor = new ActionProcessor();

        //component lifecycle mgmt
        lifecycleManager = new LifecycleManager();

        //networking
        networkManager = new AndroidNetworkManager(preferences, this);
        lifecycleManager.addObserver(networkManager);

        //proxy client
        messageHandler = new ProxyClientMessageHandler(this);

        //data storage
        try {
            registryStorage = new RegistryStorage(new DatabaseConnectionForAndroid(this), DB_VERSION);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }

        //android device
        device = new AndroidDevice();

        //comms
        comms = new ZyreCommsManager(networkManager, null, null);

        //pubsub Broker
        PubSubBroker pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, null, null);

        // TODO fix construction of proxy server
        proxyServer = new AndroidProxyServer();
        proxyServer.setPubSubBrokerService(pubSubBroker);
        proxyServer.setMessageHandler(messageHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            actionProcessor.processBezirkAction(intent, proxyServer, new LifeCycleCallbacks() {
                @Override
                public void start() {
                    logger.info("LifeCycleCallbacks:start");
                    comms.startComms();
                }

                @Override
                public void stop() {
                    logger.info("LifeCycleCallbacks:stop");
                    comms.stopComms();
                }

                @Override
                public void destroy() {
                    logger.info("LifeCycleCallbacks:destroy");
                    comms.closeComms();
                }

                @Override
                public void clearDB() {
                    logger.info("LifeCycleCallbacks:clearDB");
                }

                @Override
                public void reboot() {
                    logger.info("LifeCycleCallbacks:reboot");
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

        void clearDB();

        void reboot();
    }
}
