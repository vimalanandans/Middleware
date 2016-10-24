/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import com.bezirk.middleware.core.datastorage.DataStorageException;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.core.identity.BezirkIdentityManager;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.core.proxy.MessageHandler;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.remotelogging.RemoteLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ComponentManager extends Service implements LifeCycleCallbacks {
    private static final Logger logger = LoggerFactory.getLogger(ComponentManager.class);
    private static final String ALIAS_KEY = "aliasName";
    private static final String DB_VERSION = "0.0.4";
    private static final int FOREGROUND_ID = 1336;
    private SharedPreferences preferences;
    private ActionProcessor actionProcessor;
    private BezirkIdentityManager identityManager;
    private AndroidProxyServer proxyServer;
    private JmqCommsManager comms;
    private AndroidNetworkManager networkManager;
    private RegistryStorage registryStorage;
    private LifeCycleObservable lifecycleObservable;
    private Config config;
    private final RemoteLog remoteLog = null;
    private LifeCycleObservable.State currentState;
    private String identityString;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            //Check if bezirk is starting for the first time, if yes process the start bezirk action
            if (currentState == null) {
                BezirkAction intentAction = BezirkAction.getActionFromString(intent.getAction());
                if (intentAction == BezirkAction.ACTION_START_BEZIRK) {
                    StartServiceAction startServiceAction =
                            (StartServiceAction) intent.getSerializableExtra(BezirkAction.ACTION_START_BEZIRK.getName());
                    start(startServiceAction);
                } else {
                    logger.debug("Bezirk Action received {}. Bezirk is not running. {} required to start bezirk.",
                            intentAction, BezirkAction.ACTION_START_BEZIRK);
                }
            } else {
                actionProcessor.processBezirkAction(intent, proxyServer, this);
            }
        } else {
            logger.debug("Intent received by Bezirk Service is null");
        }
        return START_STICKY;
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("IBinder is not supported");
    }

    @Override
    public void start(StartServiceAction startServiceAction) {
        //start bezirk is called for the first time
        if (currentState == null) {
            config = startServiceAction.getConfig();
            create();
        }

        startForeground(FOREGROUND_ID, buildForegroundNotification(config.getAppName(),
                config.getAppName() + " ON", R.drawable.bezirk_notification_icon));

        logger.debug("LifeCycleCallbacks:start");
        lifecycleObservable.transition(LifeCycleObservable.Transition.START);
        currentState = lifecycleObservable.getState();
    }

    @Override
    public void stop(StopServiceAction stopServiceAction) {
        logger.debug("LifeCycleCallbacks:stop");
        //As jmq-comms currently stops its components in the main thread, this causes NetworkOnMainThread Exception.
        //As a work around, the transition is made in a new thread.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                lifecycleObservable.transition(LifeCycleObservable.Transition.STOP);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            logger.error("Stop action interrupted", e);
        }
        currentState = lifecycleObservable.getState();
        stopSelf();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (config != null && !config.keepServiceAlive()) {
            stop(null);
        }
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
        notification.setContentTitle(appName)
                .setContentText(status)
                .setSmallIcon(icon)
                .setTicker(appName
                );

        return notification.build();
    }

    private final void create() {
        logger.debug("Creating Bezirk Service");

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

        // initialize action processor to manage intents fired to Bezirk
        actionProcessor = new ActionProcessor();

        if (config.isCommsEnabled()) {
            // initialize network manager for handling wifi-management and getting network addressing
            // information
            networkManager = new AndroidNetworkManager(preferences, this);

            // initialize comms for communicating between devices over the wifi-network using jmq
            comms = new JmqCommsManager(networkManager, config.getGroupName(), null);

            // add components as observers of bezirk lifecycle events.
            lifecycleObservable.addObserver(comms);
            lifecycleObservable.addObserver(networkManager);
        }
        // initialize message handler for sending events back to zirks
        final MessageHandler messageHandler = new ZirkMessageHandler(this);

        //initialize remoteLogging for logging the messages
        // remoteLog = new RemoteLoggingManager(comms, networkManager, null);

        initializeIdentityManager();

        // initialize pub-sub Broker for filtering of events based on subscriptions and spheres
        // (if present) & dispatching messages to other zirks within the same device or another
        // device
        final PubSubBroker pubSubBroker = new PubSubBroker(registryStorage, device, networkManager, comms,
                messageHandler, identityManager, null, null, remoteLog);

        //initialize proxyServer responsible for managing incoming events from zirks
        proxyServer = new AndroidProxyServer(identityManager);
        lifecycleObservable.addObserver(proxyServer);

        // TODO initialize in constructor instead.
        proxyServer.setPubSubBrokerService(pubSubBroker);
    }

    //initialize the identity manager
    void initializeIdentityManager() {
        identityManager = new BezirkIdentityManager();
        final String aliasString = preferences.getString(ALIAS_KEY, null);
        if (logger.isDebugEnabled()) logger.debug("aliasString is {}", aliasString);

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
