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
import android.content.ServiceConnection;
import android.os.IBinder;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API to register Zirks, fetch the Bezirk API, and manage the lifecycle of the middleware on Android.
 */
public final class BezirkMiddleware {
    private static final Logger logger = LoggerFactory.getLogger(BezirkMiddleware.class);
    private static Context context;
    private static boolean serviceBound;
    private static ComponentManager.ProxyBinder proxyBinder;
    private static ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            synchronized (BezirkMiddleware.class) {
                proxyBinder = (ComponentManager.ProxyBinder) service;
                serviceBound = true;
                logger.trace("Bezirk Service connected");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            synchronized (BezirkMiddleware.class) {
                serviceBound = false;
                logger.trace("Bezirk Service disconnected");
            }
        }
    };

    private BezirkMiddleware() {
    }

    /**
     * Initializes and starts the bezirk
     * <a target="_blank" href="https://developer.android.com/reference/android/app/Service.html">Service</a>.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} unless
     * configurations are supplied explicitly using {@link #initialize(Context, Config)}. Bezirk
     * service runs as a background Android service unless explicitly stopped by the application
     * using {@link #stop()} or when the application is closed by the user.
     * </p>
     *
     * @see #initialize(Context, Config)
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context) {
        initialize(context, (Config) null);
    }

    /**
     * Initializes and starts the bezirk
     * <a target="_blank" href="https://developer.android.com/reference/android/app/Service.html">Service</a>.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} with
     * {@link Config#groupName} set to #channelId. Bezirk service runs
     * as a background Android service unless explicitly stopped by the application
     * using {@link #stop()} or when the application is closed by the user.
     * </p>
     *
     * @param channelId
     * @see #initialize(Context, Config)
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context, @NotNull final String channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId cannot be null");
        }
        initialize(context, new Config.ConfigBuilder().setGroupName(channelId).create());
    }

    /**
     * Initializes and starts the Bezirk {@link android.app.Service}.
     * <p>
     * Service is started using the supplied <code>config</code>. Once started, Zirk(s) can be
     * registered using {@link BezirkMiddleware#registerZirk(String)}. Bezirk service runs as a
     * background Android service unless explicitly stopped by the application using {@link #stop()}
     * or when the application is closed by the user.
     * </p>
     *
     * @param config custom configurations to be used when initializing the Bezirk service
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context, final Config config) {
        if (serviceBound) {
            logger.debug("Bezirk service already initialized");
            return;
        }
        BezirkMiddleware.context = context;
        context.startService(new Intent(context, ComponentManager.class));
        final Intent intent = new Intent(context, ComponentManager.class);
        intent.putExtra(Config.class.getSimpleName(), (config == null) ? new Config() : config);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available to the user in
     * Bezirk configuration interfaces. This method returns an instance of the Bezirk API for the
     * newly registered Zirk.
     *
     * @param zirkName the name of the Zirk being registered, as defined by the Zirk
     *                 developer/vendor
     * @return an instance of the Bezirk API for the newly registered Zirk, or <code>null</code> if
     * a Zirk with the name <code>zirkName</code> is already registered.
     * @see #initialize(Context)
     */
    public static synchronized Bezirk registerZirk(@NotNull final String zirkName) {
        if (!serviceBound) {
            throw new IllegalStateException("Bezirk Service is not running. Start the Bezirk " +
                    "service using BezirkMiddleware.initialize(Context) or " +
                    "BezirkMiddleware.initialize(Context, Config)");
        }
        final ZirkId zirkId = ProxyClient.registerZirk(context, zirkName, proxyBinder.getProxyServer());
        return zirkId == null ? null : new ProxyClient(zirkId);
    }

    /**
     * Stops the Bezirk {@link android.app.Service}.
     * <p>
     * Once Bezirk is stopped, Zirk(s) cannot register with the Bezirk service and all
     * communications between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        if (serviceBound) {
            logger.debug("unbinding and stopping Bezirk Service");
            context.unbindService(serviceConnection);
            context.stopService(new Intent(context, ComponentManager.class));
            serviceBound = false;
        }
    }

    /**
     * Returns true if the {@link BezirkMiddleware} is initialized.
     *
     * @return true if the {@link BezirkMiddleware} is initialized.
     */
    public static synchronized boolean isInitialized() {
        return serviceBound;
    }

}
