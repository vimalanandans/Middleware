package com.bezirk.actions;

import com.bezirk.proxy.Config;

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
    public BezirkAction getAction() {
        return BezirkAction.ACTION_START_BEZIRK;
    }
}
