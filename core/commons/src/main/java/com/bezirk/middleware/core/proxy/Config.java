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

        public ConfigBuilder() {
        }

        public Config create() {
            return new Config(groupName, appName, logLevel, packageLogLevelMap, commsEnabled, keepServiceAlive);
        }

        public ConfigBuilder setAppName(@NotNull final String appName) {
            this.appName = appName;
            return this;
        }

        public ConfigBuilder setGroupName(@NotNull final String groupName) {
            this.groupName = groupName;
            return this;
        }

        public ConfigBuilder setLogLevel(@NotNull final Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

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
         */
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
