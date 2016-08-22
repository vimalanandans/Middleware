package com.bezirk.middleware.proxy.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.bezirk.actions.Action;
import com.bezirk.actions.StartServiceAction;
import com.bezirk.actions.StopServiceAction;
import com.bezirk.proxy.Config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private static final String COMPONENT_NAME = "com.bezirk.controlui";
    private static final String SERVICE_PKG_NAME = "com.bezirk.componentManager.ComponentManager";
    private static boolean localBezirkService = true; //true if bezirk service is running locally inside the same application, false otherwise
    private static ComponentName componentName;
    private static boolean started = false;
    private static Context context;


    /**
     * Initializes the bezirk service based on the configuration passed
     * <ul>
     * <li>If <code>config</code> is <code>null</code>, Checks if bezirk app is available on the device. If available, uses the existing bezirk app, else starts the bezirk service for the current application.</li>
     * <li>If <code>config</code> is not <code>null</code>, Bezirk service is created for the current application, even if an existing bezirk service is running in the device.</li>
     * </ul>
     */
    static void initialize(@NotNull Context context, Config config) {
        ServiceManager.context = context;
        if (config == null) {
            config = new Config();
            if (isBezirkAvailableOnDevice()) {
                localBezirkService = false;
                logger.debug("Bezirk App available on the device, reusing existing bezirk service.");
            } else {
                logger.debug("Bezirk App not installed on the device.");
            }
        } else {
            logger.debug("Custom configuration passed when initializing bezirk, creating custom bezirk service");
        }
        componentName = new ComponentName((localBezirkService) ? context.getPackageName() : COMPONENT_NAME, SERVICE_PKG_NAME);

        if (sendBezirkIntent(new StartServiceAction(config))) {
            logger.info("Bezirk is starting");
            started = true;
        } else {
            logger.error("Unable to start Bezirk Service");
        }
    }


    private static boolean isBezirkAvailableOnDevice() {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(COMPONENT_NAME, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    static void stop() {
        if (localBezirkService) {
            if (started) {
                started = !sendBezirkIntent(new StopServiceAction()); //if stop action was successfully sent, started is set to false.
            } else {
                logger.warn("Bezirk Service is not running.");
            }
        } else {
            logger.debug("Not stopping bezirk. Bezirk not running within the same app.");
        }
    }

    static boolean isStarted() {
        return started;
    }

    static boolean sendBezirkIntent(@NotNull Action action) {
        if (componentName == null) {
            throw new IllegalStateException("ComponentName for sending intents not initialized");
        }

        final Intent intent = new Intent();
        intent.setComponent(componentName);
        final String actionName = action.getAction().getName();
        intent.setAction(actionName);
        intent.putExtra(actionName, action);

        if (context.startService(intent) == null) {
            logger.error("Failed to send intent for action: " + actionName + ". Intents can be send only if bezirk service is started");
            return false;
        }
        return true;
    }

}
