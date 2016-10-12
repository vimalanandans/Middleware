package com.bezirk.middleware.java.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;

public abstract class BezirkMiddleware {

    /**
     * Initializes and starts the bezirk service.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} unless
     * configurations are supplied explicitly using {@link #initialize(Config)}.
     * </p>
     *
     * @see #initialize(Config)
     * @see #stop()
     */
    public static void initialize() {
        initialize(null);
    }

    /**
     * Initializes and starts the Bezirk service.
     * <p>
     * Service is started using the supplied <code>config</code>. Once started, Zirk(s) can be
     * registered using {@link BezirkMiddleware#registerZirk(String)}.
     * </p>
     *
     * @param config custom configurations to be used by Bezirk service
     *               <ul>
     *               <li>If <code>null</code>, a default configuration is used</li>
     *               <li>If not <code>null</code>, Bezirk service is created for the current application,
     *               even if an existing Bezirk service(inside the Bezirk Application) is running in the device.</li>
     *               </ul>
     * @see #stop()
     */
    public static void initialize(final Config config) {
        synchronized (BezirkMiddleware.class) {
            ProxyClient.start((config == null) ? new Config() : config);
        }
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
     */
    public static Bezirk registerZirk(@NotNull String zirkName) {
        if (!ProxyClient.isStarted()) {
            throw new IllegalStateException("Bezirk Service is not running. Start the Bezirk service " +
                    "using BezirkMiddleware.initialize() or BezirkMiddleware.initialize(Config)");
        }
        synchronized (BezirkMiddleware.class) {
            ProxyClient proxyClient = new ProxyClient();
            return proxyClient.registerZirk(zirkName) ? proxyClient : null;
        }
    }

    /**
     * Stop the Bezirk service.
     * <p>
     * Once Bezirk is stopped, Zirk(s) cannot register with the Bezirk service and all communications
     * between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        ProxyClient.stop();
    }

}
