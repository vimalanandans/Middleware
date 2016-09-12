package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.core.proxy.Config;

public class StartServiceAction extends ServiceAction {

    private final Config config;
    private final String identity;

    public StartServiceAction(final Config config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.config = config;
        this.identity = null;
    }

    public StartServiceAction(final Config config, final String identity) {
        if (config == null || identity == null) {
            throw new IllegalArgumentException("Configuration and Identity cannot be null");
        }
        this.config = config;
        this.identity = identity;
    }

    public Config getConfig() {
        return config;
    }

    public String getIdentity() {
        return identity;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_START_BEZIRK;
    }
}
