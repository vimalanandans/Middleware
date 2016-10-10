package com.bezirk.middleware.java;

import com.bezirk.middleware.core.comms.JmqCommsManager;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.datastorage.DataStorageException;
import com.bezirk.middleware.core.datastorage.ProxyPersistence;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.identity.BezirkIdentityManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.proxy.ProxyServer;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.remotelogging.RemoteLog;
import com.bezirk.middleware.core.remotelogging.RemoteLoggingManager;
import com.bezirk.middleware.core.streaming.StreamManager;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.java.device.JavaDevice;
import com.bezirk.middleware.java.logging.LoggingManager;
import com.bezirk.middleware.java.networking.JavaNetworkManager;
import com.bezirk.middleware.java.persistence.DatabaseConnectionForJava;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

/**
 * This class manages Bezirk middleware component injection & lifecycle.
 * All top level components like PubSubBroker, Comms, ProxyServer, Data-storage, etc are injected with their dependencies.
 * Circular dependency needs to be at its minimum to prevent injection problems. Going forward dependency
 * injection using a DI framework like guice or dagger can be introduced.
 * To manage circular dependencies in the current code structure, init/setters might be needed.
 */
public class ComponentManager {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);

    private static final String ALIAS_KEY = "aliasName";

    private JmqCommsManager comms;
    private RegistryStorage registryStorage;
    private ProxyServer proxyServer;
    private final MessageHandler messageHandler;
    private final Config config;
    private NetworkManager networkManager;
    private RemoteLog remoteLog;
    private BezirkIdentityManager identityManager;
    private LifeCycleObservable lifecyleObservable;
    private Streaming streaming;

    private static final String DB_VERSION = "0.0.4";
    private static final String DB_FILE_LOCATION = ".";

    public ComponentManager(@NotNull final MessageHandler messageHandler, @NotNull final Config config) {
        this.messageHandler = messageHandler;
        this.config = config;
        create();
    }

    public final void create() {
        //initialize lifecycle manager(Observable) for components(observers) to observe bezirk lifecycle events
        lifecyleObservable = new LifeCycleObservable();

        LoggingManager loggingManager = new LoggingManager(config);
        loggingManager.configure();

        //initialize data-storage for storing detailed component information like maps, objects
        try {
            this.registryStorage = new RegistryStorage(new DatabaseConnectionForJava(DB_FILE_LOCATION), DB_VERSION);
        } catch (DataStorageException e) {
            logger.error("Failed to initialize registry storage", e);
        }

        //java device for getting information like deviceId, deviceName, etc
        final Device device = new JavaDevice();

        if(config.isCommsEnabled()) {
            logger.debug("Comms is enabled");
            //initialize network manager for handling network management and getting network addressing information
            networkManager = new JavaNetworkManager();

            // the Jmq comms
            comms = new JmqCommsManager(networkManager, config.getGroupName(), null, null);

            //streaming manager
            streaming = new StreamManager(comms, /*downloadPath,*/ networkManager);

            //initialize remoteLogging for logging the messages
            remoteLog = new RemoteLoggingManager(comms, networkManager, null);

            // add components as observers of bezirk lifecycle events.
            lifecyleObservable.addObserver(comms);

            if (Configuration.isRemoteLoggingEnabled()) {
                remoteLog.enableLogging(true, false, true, null);
            }
        }else{
            logger.debug("Comms is disabled");
        }
        //initialize the identity manager
        initializeIdentityManager();

        //initialize pub-sub Broker for filtering of events based on subscriptions and spheres(if present)
        //Dispatching messages to other zirks within the same device or another device
        final PubSubBroker pubSubBroker = new PubSubBroker(registryStorage, device, networkManager,
                comms, messageHandler, identityManager, null, null, streaming, remoteLog);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new ProxyServer(identityManager);

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);

    }

    void initializeIdentityManager() {

        final Preferences preferences = Preferences.userNodeForPackage(BezirkIdentityManager.class);
        identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.get(ALIAS_KEY, null);


        if (aliasString == null) {
            final Alias identity;
            identity = identityManager.createIdentity(System.getProperty("user.name"));
            identityManager.setIdentity(identity);

            preferences.put(ALIAS_KEY, identityManager.getAliasString());
        } else {
            logger.debug("Reusing identity {}", aliasString);
            identityManager.createAndSetIdentity(aliasString);
        }
    }

    public void start() {
        lifecyleObservable.transition(LifeCycleObservable.Transition.START);
    }

    public void stop() {
        lifecyleObservable.transition(LifeCycleObservable.Transition.STOP);
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    //TODO: Remove this dependency for proxy client by providing a persistance implementation
    public ProxyPersistence getBezirkProxyPersistence() {
        return registryStorage;
    }

}
