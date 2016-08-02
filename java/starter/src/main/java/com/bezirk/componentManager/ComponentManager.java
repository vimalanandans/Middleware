package com.bezirk.componentManager;

import com.bezirk.comms.ZyreCommsManager;

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
    private LifecycleManager lifecycleManager;
    private final ZyreCommsManager comms;

    public ComponentManager(ZyreCommsManager comms) {
        this.comms = comms;
        create();
    }

    public void create() {
        lifecycleManager = new LifecycleManager();
        lifecycleManager.addObserver(new LifeCycleObserver()); //sample observer, does nothing
        // other observers are added here
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);

        lifecycleManager.addObserver(comms);

        lifecycleManager.setState(LifecycleManager.LifecycleState.CREATED);
    }

    public void start() {
        this.lifecycleManager.setState(LifecycleManager.LifecycleState.STARTED);
        //comms.startComms(); //this should be called by comms directly when observing for lifecycle events
    }

    public void stop() {
        this.lifecycleManager.setState(LifecycleManager.LifecycleState.DESTROYED);
        //comms.closeComms(); //this should be called by comms directly when observing for lifecycle events
    }
}
