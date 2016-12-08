/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.bezirk.middleware.android.device.AndroidDevice;
import com.bezirk.middleware.android.logging.LoggingManager;
import com.bezirk.middleware.android.persistence.DatabaseConnectionForAndroid;
import com.bezirk.middleware.android.proxy.android.ZirkMessageHandler;
import com.bezirk.middleware.core.comms.JmqCommsManager;
import com.bezirk.middleware.core.componentManager.LifeCycleObservable;
import com.bezirk.middleware.core.datastorage.DataStorageException;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.identity.BezirkIdentityManager;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.proxy.ProxyServer;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.remotelogging.RemoteLog;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentManager extends Service {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private static final String ALIAS_KEY = "aliasName";
    private static final String DB_VERSION = "0.0.4";
    private SharedPreferences preferences;
    private BezirkIdentityManager identityManager;
    private ProxyServer proxyServer;
    private JmqCommsManager comms;
    private RegistryStorage registryStorage;
    private LifeCycleObservable lifecycleObservable;
    private Config config;
    private RemoteLog remoteLog;

    public ComponentManager() {
        super();
    }

    public class ProxyBinder extends Binder {
        ProxyServer getProxyServer() {
            return proxyServer;
        }
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        logger.trace("onBind()");
        config = (Config) intent.getSerializableExtra(Config.class.getSimpleName());
        create();
        executeTransitionInThread(LifeCycleObservable.Transition.START);
        return new ProxyBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        logger.trace("onUnbind()");
        stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        logger.trace("onTaskRemoved()");
        stop();
    }

    private void stop() {
        logger.trace("stop()");
        executeTransitionInThread(LifeCycleObservable.Transition.STOP);
        stopSelf();
    }

    private void create() {
        logger.trace("Creating Bezirk Service");

        // initialize lifecycle manager(Observable) for components(observers) to observe bezirk lifecycle events
        lifecycleObservable = new LifeCycleObservable();

        final LoggingManager loggingManager = new LoggingManager(config);
        loggingManager.configure();

        //initialize data-storage for storing detailed component information like maps, objects
        try {
            registryStorage = new RegistryStorage(new DatabaseConnectionForAndroid(this), DB_VERSION);
        } catch (DataStorageException e) {
            logger.error("Failed to initialize registry storage", e);
        }

        //android device for getting information like deviceId, deviceName, etc
        final Device device = new AndroidDevice();

        // initialize android shared preferences for storing user preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (config.isCommsEnabled()) {
            // initialize comms for communicating between devices over the wifi-network using jmq
            comms = new JmqCommsManager(config.getGroupName(), null);

            // add components as observers of bezirk lifecycle events.
            lifecycleObservable.addObserver(comms);
        }
        // initialize message handler for sending events back to zirks
        final MessageHandler messageHandler = new ZirkMessageHandler(this);

        //initialize remoteLogging for logging the messages
        // remoteLog = new RemoteLoggingManager(comms, null);

        initializeIdentityManager();

        // initialize pub-sub Broker for filtering of events based on subscriptions and spheres
        // (if present) & dispatching messages to other zirks within the same device or another
        // device
        final PubSubBroker pubSubBroker = new PubSubBroker(registryStorage, device, comms,
                messageHandler, identityManager, null, null, remoteLog);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new ProxyServer(identityManager);

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);
    }

    //initialize the identity manager
    private void initializeIdentityManager() {
        identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.getString(ALIAS_KEY, null);
        if (logger.isDebugEnabled()) {
            logger.debug("aliasString is {}", aliasString);
        }

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

    /**
     * As jmq-comms currently stops its components in the main thread,
     * this causes NetworkOnMainThread Exception in Android.
     * As a work around, the transition is made in a new thread.
     */
    private void executeTransitionInThread(@NotNull final LifeCycleObservable.Transition transition) {
        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                lifecycleObservable.transition(transition);
            }
        });
        t.setName("lifecycle-transition");
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            logger.error("transition {} interrupted", transition, e);
            Thread.currentThread().interrupt();
        }
    }
}
