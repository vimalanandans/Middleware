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
import com.bezirk.proxy.ProxyServer;
import com.bezirk.pubsubbroker.PubSubBroker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;

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
    private Device device;
    private NetworkManager networkManager;
    private LifecycleManager lifecycleManager;

    public ComponentManager(ProxyServer proxyServer) {
        this.proxyServer = proxyServer;
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
            this.registryStorage = new RegistryStorage(new DatabaseConnectionForJava("."), "0.0.4");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.device = new JavaDevice();

        this.pubSubBroker = new PubSubBroker(registryStorage, device, networkManager);
        this.proxyServer.setPubSubBrokerService(pubSubBroker);
        this.comms = new ZyreCommsManager(null, networkManager.getInetAddress(), pubSubBroker, null, null, null, networkManager);

        this.lifecycleManager.setState(LifecycleManager.LifecycleState.CREATED);
    }

    public void start() {
        comms.startComms();
    }

    public void stop() {
        comms.closeComms();
    }

    //TODO: Remove this dependency for proxy client by providing a persistance implementation
    public ProxyPersistence getBezirkProxyPersistence() {
        return registryStorage;
    }

}
