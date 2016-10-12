package com.bezirk.middleware.android;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.bezirk.middleware.core.actions.Action;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntentSender {
    private static final Logger logger = LoggerFactory.getLogger(IntentSender.class);
    private static final String COMPONENT_NAME = "com.bezirk.middleware.android.ui";
    private static final String SERVICE_PKG_NAME = "com.bezirk.middleware.android.ComponentManager";
    private final Context context;
    private final ComponentName componentName;

    public IntentSender(@NotNull final Context context) {
        this.context = context;
        this.componentName = new ComponentName(BezirkMiddleware.isLocalBezirkService() ?
                context.getPackageName() : COMPONENT_NAME, SERVICE_PKG_NAME);
    }

    static boolean isBezirkAvailableOnDevice(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(COMPONENT_NAME, PackageManager.GET_ACTIVITIES);
            installed = true;
            logger.debug("Bezirk App available on the device, reusing existing bezirk service.");
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
            logger.debug("Bezirk App not installed on the device.", e);
        }
        return installed;
    }

    public boolean sendBezirkIntent(@NotNull Action action) {
        if (componentName == null) {
            throw new IllegalStateException("ComponentName for sending intents not initialized");
        }

        final Intent intent = new Intent();
        intent.setComponent(componentName);
        final String actionName = action.getAction().getName();
        intent.setAction(actionName);
        intent.putExtra(actionName, action);

        if (context.startService(intent) == null) {
            logger.error("Failed to send intent for action: {}. Intents can be send only if bezirk " +
                    "service is started", actionName);
            return false;
        }
        return true;
    }
}
