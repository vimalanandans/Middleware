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

    /**
     * Start the bezirk service based on the configuration passed
     * <ul>
     * <li>If <code>config</code> is <code>null</code>, initialize a default configuration</li>
     * <li>If <code>config</code> is not <code>null</code>, Bezirk service is created for the current application, even if an existing bezirk service is running in the device.</li>
     * </ul>
     */
    void start(final Config config) {
        if (intentSender.sendBezirkIntent(new StartServiceAction(config))) {
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
