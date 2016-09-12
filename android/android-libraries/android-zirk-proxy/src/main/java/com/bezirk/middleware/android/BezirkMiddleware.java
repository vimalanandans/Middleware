package com.bezirk.middleware.android;

import android.content.Context;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BezirkMiddleware {
    private static final Logger logger = LoggerFactory.getLogger(BezirkMiddleware.class);
    private static Context context;
    private static boolean localBezirkService;
    private static IntentSender intentSender;
    private static ServiceManager serviceManager;

    public static synchronized void initializeWithIdentity(@NotNull final Context context, final Config config, final String identity) {
        BezirkMiddleware.context = context;

        if (identity == null || identity.isEmpty()) {
            throw new IllegalArgumentException("identity cannot be null or empty");
        }

        if (config == null) {
            localBezirkService = (IntentSender.isBezirkAvailableOnDevice(context)) ? false : true;
        } else {
            logger.debug("Custom configuration passed when initializing bezirk, creating custom bezirk service. Is bezirk service local: " + localBezirkService);
        }

        intentSender = new IntentSender(context);
        serviceManager = new ServiceManager(intentSender);
        serviceManager.start((config == null) ? new Config() : config, identity);
    }

    /**
     * Initializes and starts the bezirk {@link android.app.Service}.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} unless configurations are supplied explicitly using {@link #initialize(Context, Config)}. Bezirk service runs as a background Android service unless explicitly stopped by the application using {@link #stop()} or by Android OS.
     * </p>
     *
     * @see #initialize(Context, Config)
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context) {
        initialize(context, null);
    }

    /**
     * Initializes and starts the bezirk {@link android.app.Service}.
     * <p>
     * Service is started using the supplied <code>config</code>. Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}. Bezirk service runs as a background Android service unless explicitly stopped by the application using {@link #stop()} or by Android OS.
     * </p>
     *
     * @param config custom configurations to be used by bezirk service
     *               <ul>
     *               <li>If <code>null</code>, a default configuration is used</li>
     *               <li>If not <code>null</code>, Bezirk service is created for the current application, even if an existing bezirk service(inside the Bezirk Application) is running in the device.</li>
     *               </ul>
     * @see #stop()
     */
    public static synchronized void initialize(@NotNull final Context context, final Config config) {
        BezirkMiddleware.context = context;

        if (config == null) {
            localBezirkService = (IntentSender.isBezirkAvailableOnDevice(context)) ? false : true;
        } else {
            logger.debug("Custom configuration passed when initializing bezirk, creating custom bezirk service. Is bezirk service local: " + localBezirkService);
        }

        intentSender = new IntentSender(context);
        serviceManager = new ServiceManager(intentSender);
        serviceManager.start((config == null) ? new Config() : config);
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
     */
    public static synchronized Bezirk registerZirk(@NotNull final String zirkName) {
        if (serviceManager == null || !serviceManager.isStarted()) {
            throw new IllegalStateException("Bezirk Service is not running. Start the bezirk service using BezirkMiddleware.start(Context)");
        }
        ZirkId zirkId = ProxyClient.registerZirk(context, zirkName, intentSender);
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
     * Stops the bezirk {@link android.app.Service}.
     * <p>
     * Once bezirk is stopped, Zirk(s) cannot register with the bezirk service and all communications between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        serviceManager.stop();
        context = null;
    }

    static boolean isLocalBezirkService() {
        return localBezirkService;
    }

}
