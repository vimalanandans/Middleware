package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.core.proxy.Config;

public class StartServiceAction extends ServiceAction {

    private static final long serialVersionUID = -729245176942825895L;
    private final Config config;

    public StartServiceAction(final Config config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_START_BEZIRK;
    }
}
