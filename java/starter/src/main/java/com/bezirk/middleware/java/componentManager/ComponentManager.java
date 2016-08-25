package com.bezirk.middleware.java.componentManager;

import com.bezirk.middleware.java.comms.ZyreCommsManager;
import com.bezirk.middleware.core.datastorage.ProxyPersistence;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.java.device.JavaDevice;
import com.bezirk.middleware.core.identity.BezirkIdentityManager;
import com.bezirk.middleware.identity.Alias;
import com.bezirk.middleware.java.networking.JavaNetworkManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.java.persistence.DatabaseConnectionForJava;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.proxy.ProxyServer;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.streaming.StreamManager;
import com.bezirk.middleware.core.streaming.Streaming;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.Preferences;

import ch.qos.logback.classic.Level;

/**
 * This class manages Bezirk middleware component injection & lifecycle.
 * All top level components like PubSubBroker, Comms, ProxyServer, Data-storage etc are injected with their dependencies.
 * Circular dependency needs to be at its minimum to prevent injection problems. Going forward dependency injection using a DI framework like guice or dagger can be introduced.
 * To manage circular dependencies in the current code structure, init/setters might be needed.
 */
public class ComponentManager {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);

    private static final String ALIAS_KEY = "aliasName";

    private ZyreCommsManager comms;
    //private JmqCommsManager comms;
    private PubSubBroker pubSubBroker;
    private RegistryStorage registryStorage;
    private ProxyServer proxyServer;
    private MessageHandler messageHandler;
    private Device device;
    private NetworkManager networkManager;
    private com.bezirk.middleware.core.componentManager.LifecycleManager lifecycleManager;
    private static final String DB_VERSION = "0.0.4";
    private static final String DB_FILE_LOCATION = ".";
    //download path
    private final static String downloadPath = "";

    public ComponentManager(MessageHandler messageHandler, String messageGroupName) {
        this.messageHandler = messageHandler;
        create(messageGroupName);
    }

    public void create(String messageGroupName) {
        //initialize lifecycle manager(Observable) for components(observers) to observe bezirk lifecycle events
        lifecycleManager = new com.bezirk.middleware.core.componentManager.LifecycleManager();

        if (Configuration.isLoggingEnabled()) {
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(Level.DEBUG);
        }

        //initialize network manager for handling network management and getting network addressing information
        networkManager = new JavaNetworkManager();

        //initialize data-storage for storing detailed component information like maps, objects
        try {
            this.registryStorage = new RegistryStorage(new DatabaseConnectionForJava(DB_FILE_LOCATION), DB_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //java device for getting information like deviceId, deviceName, etc
        device = new JavaDevice();

        //initialize comms for communicating between devices over the wifi-network using zyre.
        comms = new ZyreCommsManager(networkManager, messageGroupName, null,null );
        // to test the Jmq comms
        //comms = new JmqCommsManager(networkManager, messageGroupName, null,null );

        //streaming manager
        Streaming streaming  = new StreamManager(comms, /*downloadPath,*/ networkManager);

        //initialize pub-sub Broker for filtering of events based on subscriptions and spheres(if present) & dispatching messages to other zirks within the same device or another device
        pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, null, null,streaming);

        //initialize the identity manager
        final Preferences preferences = Preferences.userNodeForPackage(BezirkIdentityManager.class);
        final BezirkIdentityManager identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.get(ALIAS_KEY, null);
        final Gson gson = new Gson();
        final Alias identity;

        if (aliasString == null) {
            identity = identityManager.createIdentity(System.getProperty("user.name"));
            identityManager.setIdentity(identity);

            preferences.put(ALIAS_KEY, gson.toJson(identity));
        } else {
            if (logger.isDebugEnabled()) logger.debug("Reusing identity {}", aliasString);
            identity = gson.fromJson(aliasString, Alias.class);
        }

        identityManager.setIdentity(identity);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new ProxyServer(identityManager);

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);

        // add components as observers of bezirk lifecycle events.
        lifecycleManager.addObserver(comms);

        // this state is set only when the bezirk service is created the first time
        lifecycleManager.setState(com.bezirk.middleware.core.componentManager.LifecycleManager.LifecycleState.CREATED);
    }

    public void start() {
        this.lifecycleManager.setState(com.bezirk.middleware.core.componentManager.LifecycleManager.LifecycleState.STARTED);
    }

    public void stop() {
        this.lifecycleManager.setState(com.bezirk.middleware.core.componentManager.LifecycleManager.LifecycleState.DESTROYED);
    }

    public ProxyServer getProxyServer() {
        return proxyServer;
    }

    //TODO: Remove this dependency for proxy client by providing a persistance implementation
    public ProxyPersistence getBezirkProxyPersistence() {
        return registryStorage;
    }

}
