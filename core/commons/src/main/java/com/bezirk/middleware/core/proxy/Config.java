package com.bezirk.middleware.core.proxy;

import java.io.Serializable;

public class Config implements Serializable {
    private static final String DEFAULT_GROUP_NAME = "BEZIRK_GROUP";
    private static final boolean DEFAULT_LOGGING_ENABLED = false;
    private static final String DEFAULT_APP_NAME = "Bezirk";

    private String groupName = DEFAULT_GROUP_NAME;
    private boolean loggingEnabled = DEFAULT_LOGGING_ENABLED;
    private String appName = DEFAULT_APP_NAME;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}
