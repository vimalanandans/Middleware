package com.bezirk.componentManager;

import com.bezirk.comms.Comms;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.datastorage.ProxyPersistence;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.Device;
import com.bezirk.device.JavaDevice;
import com.bezirk.networking.JavaNetworkManager;
import com.bezirk.networking.NetworkManager;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.proxy.MessageHandler;
import com.bezirk.proxy.ProxyServer;
import com.bezirk.pubsubbroker.PubSubBroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * This class manages Bezirk middleware component injection & lifecycle.
 * All top level components like PubSubBroker, Comms, ProxyServer, Data-storage etc are injected with their dependencies.
 * Circular dependency needs to be at its minimum to prevent injection problems. Going forward dependency injection using a DI framework like guice or dagger can be introduced.
 * To manage circular dependencies in the current code structure, init/setters might be needed.
 */
public class ComponentManager {

    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private Comms comms;
    private PubSubBroker pubSubBroker;
    private RegistryStorage registryStorage;
    private ProxyServer proxyServer;
    private MessageHandler messageHandler;
    private Device device;
    private NetworkManager networkManager;
    private LifecycleManager lifecycleManager;
    private static final String DB_VERSION = "0.0.4";
    private static final String DB_FILE_LOCATION = ".";

    public ComponentManager(ProxyServer proxyServer, MessageHandler messageHandler) {
        this.proxyServer = proxyServer;
        this.messageHandler = messageHandler;
        create();
    }

    public void create() {
        this.lifecycleManager = new LifecycleManager();
        this.lifecycleManager.addObserver(new LifeCycleObserver()); //sample observer, does nothing
        // other observers are added here
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);

        networkManager = new JavaNetworkManager();

        try {
            this.registryStorage = new RegistryStorage(new DatabaseConnectionForJava(DB_FILE_LOCATION), DB_VERSION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.device = new JavaDevice();
        this.comms = new ZyreCommsManager(null, null, networkManager);
        this.pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms, messageHandler, null, null);

        this.proxyServer.setPubSubBrokerService(pubSubBroker);

        this.lifecycleManager.setState(LifecycleManager.LifecycleState.CREATED);
    }

    public void start() {
        this.lifecycleManager.setState(LifecycleManager.LifecycleState.STARTED);
        comms.startComms(); //this should be called by comms directly when observing for lifecycle events
    }

    public void stop() {
        this.lifecycleManager.setState(LifecycleManager.LifecycleState.DESTROYED);
        comms.closeComms(); //this should be called by comms directly when observing for lifecycle events
    }

    //TODO: Remove this dependency for proxy client by providing a persistance implementation
    public ProxyPersistence getBezirkProxyPersistence() {
        return registryStorage;
    }

}
