package com.bezirk.middleware.java.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;

public abstract class BezirkMiddleware {

    /**
     * Start the bezirk service. This allows Zirk(s) to be registered with the bezirk service and allows them to communicate with other Zirk(s).
     * <p>
     * Service is started using default configurations {@link Config} unless configurations are supplied explicitly using {@link #initialize(Config)}. Bezirk service runs as a background Android service unless explicitly stopped by the application using {@link #stop()} or by Android OS.
     * </p>
     *
     * @see #initialize(Config)
     * @see #stop()
     */
    public static synchronized void initialize() {
        initialize(null);
    }

    /**
     * Start the bezirk service. This allows Zirk(s) to be registered with the bezirk service and allows them to communicate with other Zirk(s).
     * <p>
     * Service is started using the supplied <code>Config</code>. Bezirk service runs as a background Android service unless explicitly stopped by the application using {@link #stop()} or by Android OS.
     * </p>
     *
     * @param config custom configurations to be used by bezirk service
     *               <ul>
     *               <li>If <code>config</code> is <code>null</code>, a default configuration is used</li>
     *               <li>If <code>config</code> is not <code>null</code>, Bezirk service is created for the current application, even if an existing bezirk service is running in the device.</li>
     *               </ul>
     * @see #stop()
     */
    public static synchronized void initialize(final Config config) {
        //TODO
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
     */
    public static Bezirk registerZirk(@NotNull String zirkName) {
        synchronized (BezirkMiddleware.class) {
            ProxyClient proxyClient = new ProxyClient(null);
            return proxyClient.registerZirk(zirkName) ? proxyClient : null;
        }
    }

    /**
     * Register a Zirk with the Bezirk middleware. This makes the Zirk available to the user in
     * Bezirk configuration interfaces, thus allowing her to place it in a sphere to interact with
     * other Zirks. This method returns an instance of the Bezirk API for the newly registered
     * Zirk.
     *
     * @param zirkName  the name of the Zirk being registered, as defined by the Zirk
     *                  developer/vendor
     * @param groupName group name to avoid message collision
     * @return an instance of the Bezirk API for the newly registered Zirk, or <code>null</code> if
     * a Zirk with the name <code>zirkName</code> is already registered.
     */
    @Deprecated
    public static Bezirk registerZirk(String zirkName, String groupName) {
        synchronized (BezirkMiddleware.class) {
            ProxyClient proxyClient = new ProxyClient(groupName);
            return proxyClient.registerZirk(zirkName) ? proxyClient : null;
        }
    }

    /**
     * Stop the bezirk service.
     * <p>
     * Once bezirk is stopped, Zirk(s) cannot register with the bezirk service and all communications between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        //TODO
    }

}
