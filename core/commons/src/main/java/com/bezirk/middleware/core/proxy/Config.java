package com.bezirk.middleware.core.proxy;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Config implements Serializable {
    private static final long serialVersionUID = 2330364021536661076L;
    private static final String DEFAULT_GROUP_NAME = "BEZIRK_GROUP";
    private static final String DEFAULT_APP_NAME = "Bezirk";
    private static final Level DEFAULT_LOG_LEVEL = Level.ERROR;
    private static final boolean COMMS_ENABLED = true;

    public enum Level {TRACE, DEBUG, INFO, WARN, ERROR, OFF}

    private final String groupName;
    private final String appName;
    private final Level logLevel;
    private final Map<String, Level> packageLogLevelMap;
    private final boolean commsEnabled;

    public Config() {
        this(DEFAULT_GROUP_NAME, DEFAULT_APP_NAME, DEFAULT_LOG_LEVEL, null, COMMS_ENABLED);
    }

    public Config(@NotNull final String groupName, @NotNull final String appName, @NotNull final Level logLevel, final Map<String, Level> packageLogLevelMap, @NotNull final boolean commsEnabled) {
        this.groupName = groupName;
        this.appName = appName;
        this.logLevel = logLevel;
        this.packageLogLevelMap = packageLogLevelMap;
        this.commsEnabled = commsEnabled;
    }

    public static class ConfigBuilder {
        private String groupName = DEFAULT_GROUP_NAME;
        private String appName = DEFAULT_APP_NAME;
        private Level logLevel = DEFAULT_LOG_LEVEL;
        private Map<String, Level> packageLogLevelMap;
        private boolean commsEnabled = COMMS_ENABLED;

        public ConfigBuilder() {
        }

        public Config create() {
            return new Config(groupName, appName, logLevel, packageLogLevelMap, commsEnabled);
        }

        public ConfigBuilder setAppName(@NotNull String appName) {
            this.appName = appName;
            return this;
        }

        public ConfigBuilder setGroupName(@NotNull String groupName) {
            this.groupName = groupName;
            return this;
        }

        public ConfigBuilder setLogLevel(@NotNull Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public ConfigBuilder setPackageLogLevel(@NotNull String packageName, @NotNull Level logLevel) {
            if (packageLogLevelMap == null) {
                packageLogLevelMap = new HashMap<>();
            }
            if (packageLogLevelMap.containsKey(packageName)) {
                throw new IllegalArgumentException("package name '" + packageName + "' already present with logLevel " + packageLogLevelMap.get(packageName));
            } else {
                packageLogLevelMap.put(packageName, logLevel);
            }

            return this;
        }

        /**
         * Enable/Disable communications and networking capabilities of Bezirk.
         *
         * @param state if <code>false</code>, disables communications and networking capabilities of bezirk<br>
         */
        public ConfigBuilder setComms(@NotNull boolean state) {
            commsEnabled = state;
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
}
