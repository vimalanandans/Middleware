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
package com.bezirk.middleware.core.proxy;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to supply configuration(s) when initializing the <code>BezirkMiddleware</code>.
 * <br>
 * <pre>
 * import com.bezirk.middleware.core.proxy.Config;
 * import com.bezirk.middleware.java.proxy.BezirkMiddleware;
 *
 * // ...
 *          Config.ConfigBuilder configBuilder = new Config.ConfigBuilder();
 *
 *          //set loglevel to debug for the bezirk middleware.
 *          configBuilder.setLogLevel(Config.Level.DEBUG);
 *
 *          BezirkMiddleware.initialize(configBuilder.create());
 *
 * // ...
 *
 * </pre>
 *
 * The sample uses the <code>BezirkMiddleware</code> Java SE lifecycle API.
 * There is also an android version for the lifecycle API.
 */
public class Config implements Serializable {
    private static final long serialVersionUID = 2330364021536661076L;
    private static final String DEFAULT_GROUP_NAME = "BEZIRK_GROUP";
    private static final Level DEFAULT_LOG_LEVEL = Level.ERROR;
    private static final boolean COMMS_ENABLED = true;

    //currently used only for android version of the middleware
    private static final String DEFAULT_APP_NAME = "Bezirk";
    private static final boolean SERVICE_ALIVE = false;

    public enum Level {TRACE, DEBUG, INFO, WARN, ERROR, OFF}

    private final String groupName;
    private final String appName;
    private final Level logLevel;
    private final Map<String, Level> packageLogLevelMap;
    private final boolean commsEnabled;
    private final boolean serviceAlive;

    /**
     * Constructs a Config object with default configurations. The default configurations have the following settings:
     * <ul>
     * <li>groupName/channelId for communication is {@link #DEFAULT_GROUP_NAME}</li>
     * <li>appName used in android notifications is {@link #DEFAULT_APP_NAME}</li>
     * <li>log level for the bezirk middleware is {@link #DEFAULT_LOG_LEVEL}</li>
     * <li>Inter-device communication enabled {@link #COMMS_ENABLED}</li>
     * <li>Bezirk android service continues to run after application shutdown {@link #SERVICE_ALIVE}</li>
     * </ul>
     */
    public Config() {
        this(DEFAULT_GROUP_NAME, DEFAULT_APP_NAME, DEFAULT_LOG_LEVEL, null, COMMS_ENABLED,
                SERVICE_ALIVE);
    }

    public Config(@NotNull final String groupName, @NotNull final String appName,
                  @NotNull final Level logLevel, final Map<String, Level> packageLogLevelMap,
                  final boolean commsEnabled, final boolean serviceAlive) {
        this.groupName = groupName;
        this.appName = appName;
        this.logLevel = logLevel;
        this.packageLogLevelMap = packageLogLevelMap;
        this.commsEnabled = commsEnabled;
        this.serviceAlive = serviceAlive;
    }

    public static class ConfigBuilder {
        private String groupName = DEFAULT_GROUP_NAME;
        private String appName = DEFAULT_APP_NAME;
        private Level logLevel = DEFAULT_LOG_LEVEL;
        private Map<String, Level> packageLogLevelMap;
        private boolean commsEnabled = COMMS_ENABLED;
        private boolean keepServiceAlive = SERVICE_ALIVE;

        /**
         * Creates a ConfigBuilder instance that can be used to build Config with various configuration
         * settings. ConfigBuilder follows the builder pattern, and it is typically used by first
         * invoking various configuration methods to set desired options, and finally calling
         * {@link #create()}.
         */
        public ConfigBuilder() {
        }

        /**
         * Creates a {@link Config} instance based on the current configuration.
         *
         * @return an instance of Config configured with the options currently set in this builder
         */
        public Config create() {
            return new Config(groupName, appName, logLevel, packageLogLevelMap, commsEnabled, keepServiceAlive);
        }


        /**
         * Set the appName which is shown in the notification created by the bezirk android service.
         *
         * @deprecated As of version 3.0.2, this method is no longer used.
         */
        @Deprecated
        public ConfigBuilder setAppName(@NotNull final String appName) {
            this.appName = appName;
            return this;
        }

        /**
         * Set Group-name/channelId used for communication between a multiple instances of bezirk.
         */
        public ConfigBuilder setGroupName(@NotNull final String groupName) {
            this.groupName = groupName;
            return this;
        }

        /**
         * Set the overall log level for the bezirk
         */
        public ConfigBuilder setLogLevel(@NotNull final Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * Set loglevel for a particular package in the bezirk middleware.
         *
         * @param packageName package name for setting the log level, ex. <code>com.bezirk.middleware.core.comms</code>
         */
        public ConfigBuilder setPackageLogLevel(@NotNull final String packageName, @NotNull final Level logLevel) {
            if (packageLogLevelMap == null) {
                packageLogLevelMap = new HashMap<>();
            }
            if (packageLogLevelMap.containsKey(packageName)) {
                throw new IllegalArgumentException("package name '" + packageName +
                        "' already present with logLevel " + packageLogLevelMap.get(packageName));
            } else {
                packageLogLevelMap.put(packageName, logLevel);
            }

            return this;
        }

        /**
         * Enable/Disable communications and networking capabilities of Bezirk. True by default.
         *
         * @param state if <code>false</code>, disables communications and networking capabilities of bezirk<br>
         */
        public ConfigBuilder setComms(final boolean state) {
            commsEnabled = state;
            return this;
        }

        /**
         * This configuration works only in android, has no affect in java version of the middleware. False by default.
         *
         * @param alive if <code>true</code>, keeps the android service running even when the application
         *              is shutdown. In such cases, shutdown is handled explicitly by the application
         *              using <code>BezirkMiddleware.stop()</code>
         * @deprecated As of version 3.0.2, this method is no longer used.
         */
        @Deprecated
        public ConfigBuilder setServiceAlive(final boolean alive) {
            keepServiceAlive = alive;
            return this;
        }

    }

    public String getGroupName() {
        return groupName;
    }

    public String getAppName() {
        return appName;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public Map<String, Level> getPackageLogLevelMap() {
        return packageLogLevelMap;
    }

    public boolean isCommsEnabled() {
        return commsEnabled;
    }

    public boolean keepServiceAlive() {
        return serviceAlive;
    }
}
