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
package com.bezirk.middleware.java.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;

/**
 * API to register Zirks, fetch the Bezirk API, and manage the lifecycle of the middleware on Java SE.
 */
public final class BezirkMiddleware {

    private BezirkMiddleware() {
    }

    /**
     * Initializes and starts the bezirk service.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} unless
     * configurations are supplied explicitly using {@link #initialize(Config)}.
     * </p>
     *
     * @see #initialize(Config)
     * @see #initialize(String)
     * @see #stop()
     */
    public static void initialize() {
        initialize((Config) null);
    }

    /**
     * Initializes and starts the bezirk service.
     * <p>
     * Once started, Zirk(s) can be registered using {@link BezirkMiddleware#registerZirk(String)}.
     * {@link BezirkMiddleware} is started using default configurations {@link Config} with
     * {@link Config#groupName} set to the passed <code>channelId</code>.
     * </p>
     *
     * @see #initialize()
     * @see #initialize(Config)
     * @see #stop()
     */
    public static void initialize(@NotNull final String channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("channelId cannot be null");
        }
        initialize(new Config.ConfigBuilder().setGroupName(channelId).create());
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
     * @see #initialize()
     * @see #initialize(String)
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
     * Once Bezirk is stopped, Zirk(s) cannot register with the Bezirk middleware and all communications
     * between registered Zirks would stop.
     * </p>
     */
    public static synchronized void stop() {
        ProxyClient.stop();
    }

}
