package com.bezirk.middleware.android;

import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;
import com.bezirk.middleware.core.proxy.Config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private boolean started = false;
    private final IntentSender intentSender;

    public ServiceManager(@NotNull final IntentSender intentSender) {
        this.intentSender = intentSender;
    }

    void start(@NotNull final Config config) {
        if (intentSender.sendBezirkIntent(new StartServiceAction(config))) {
            logger.info("Bezirk is starting");
            started = true;
        } else {
            logger.error("Unable to start Bezirk Service");
        }
    }

    void start(@NotNull final Config config, @NotNull final String identity) {
        if (intentSender.sendBezirkIntent(new StartServiceAction(config, identity))) {
            logger.info("Bezirk is starting");
            started = true;
        } else {
            logger.error("Unable to start Bezirk Service");
        }
    }

    void stop() {
        if (BezirkMiddleware.isLocalBezirkService()) {
            if (started) {
                started = !intentSender.sendBezirkIntent(new StopServiceAction()); //if stop action was successfully sent, started is set to false.
            } else {
                logger.warn("Bezirk Service is not running.");
            }
        } else {
            logger.debug("Not stopping bezirk. Bezirk not running within the same app.");
        }
    }

    boolean isStarted() {
        return started;
    }


}
