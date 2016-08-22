package com.bezirk.middleware.proxy.android;

import android.content.Context;

import com.bezirk.middleware.Bezirk;
import com.bezirk.proxy.Config;
import com.bezirk.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;

public abstract class BezirkMiddleware {

    private static Config config;
    private static Context context;

    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available to the user in
     * Bezirk configuration interfaces, thus allowing her to place it in a sphere to interact with
     * other Zirks. This method returns an instance of the Bezirk API for the newly registered
     * Zirk.
     *
     * @param zirkName the name of the Zirk being registered, as defined by the Zirk
     *                 developer/vendor
     * @return an instance of the Bezirk API for the newly registered Zirk, or <code>null</code> if
     * a Zirk with the name <code>zirkName</code> is already registered.
     * @see #initialize(Context)
     */
    public static synchronized Bezirk registerZirk(final String zirkName) {
        if (!ServiceManager.isStarted()) {
            throw new IllegalStateException("Bezirk Service is not running. Start the bezirk service using BezirkMiddleware.initialize(Context)");
        }
        ZirkId zirkId = ProxyClient.registerZirk(context, zirkName);
        return zirkId == null ? null : new ProxyClient(zirkId);
    }

    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available to the user in
     * Bezirk configuration interfaces, thus allowing her to place it in a sphere to interact with
     * other Zirks. This method returns an instance of the Bezirk API for the newly registered
     * Zirk.
     *
     * @param zirkName the name of the Zirk being registered, as defined by the Zirk
     *                 developer/vendor
     * @return an instance of the Bezirk API for the newly registered Zirk, or <code>null</code> if
     * a Zirk with the name <code>zirkName</code> is already registered.
     * @see #initialize(Context)
     * @deprecated registration of Zirk(s) now requires {@link BezirkMiddleware} to be initialized using {@link BezirkMiddleware#initialize(Context)}. Once initialized use {@link #registerZirk(String)} to register a Zirk with the middleware.
     */
    @Deprecated
    public static synchronized Bezirk registerZirk(final Context context, final String zirkName) {
        initialize(context);
        return registerZirk(zirkName);
    }

    /**
     * Provide custom configuration for your application before starting the bezirk service {@link #initialize(Context)}.
     *
     * @param config custom configurations to be used by bezirk service
     */
    public static synchronized void setConfig(final Config config) {
        BezirkMiddleware.config = config;
    }

    /**
     * Start the bezirk service. This allows Zirk(s) to be registered with the bezirk service and allows them to communicate with other Zirk(s).
     * <p>
     * Service is started using default configurations {@link Config} unless configurations are supplied using {@link #setConfig(Config)}. Bezirk service runs a background Android service unless explicitly stopped by the application using {@link #stop()} or by Android OS.
     * </p>
     *
     * @see #setConfig(Config)
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context) {
        BezirkMiddleware.context = context;
        //ServiceManager.start(context, (config != null) ? config : new Config());
        ServiceManager.initialize(context, config);
    }

    /**
     * Stop the bezirk service.
     * <p>
     * Once the bezirk is stopped, Zirk(s) cannot register with the bezirk service and all communications between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        ServiceManager.stop();
        context = null;
    }

}
