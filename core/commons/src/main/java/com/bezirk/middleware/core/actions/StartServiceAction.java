package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.core.proxy.Config;

public class StartServiceAction extends ServiceAction {

    private final Config config;

    public StartServiceAction(Config config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public com.bezirk.middleware.core.actions.BezirkAction getAction() {
        return com.bezirk.middleware.core.actions.BezirkAction.ACTION_START_BEZIRK;
    }
}
