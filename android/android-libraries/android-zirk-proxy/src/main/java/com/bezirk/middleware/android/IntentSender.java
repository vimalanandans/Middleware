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
