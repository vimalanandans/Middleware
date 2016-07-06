package com.bezirk.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory creates and returns the comms based on features
 */
public class CommsFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommsFactory.class);

    CommsFeature activeComms = CommsFeature.COMMS_BEZIRK;

    BezirkComms getComms() {

        if (!CommsFeature.COMMS_BEZIRK.isActive() && !CommsFeature.COMMS_ZYRE.isActive()) {
            logger.error("both comms are not active. Selecting only the default comms");
            // logger
        }

        // we can't keep both comms active. not yet.
        if (CommsFeature.COMMS_BEZIRK.isActive() && CommsFeature.COMMS_ZYRE.isActive()) {
            logger.error("both comms active. Selecting only the default comms");
            // logger
        } else if (CommsFeature.COMMS_ZYRE.isActive()) {

            activeComms = CommsFeature.COMMS_ZYRE;

            logger.debug("zyre comms is selected");
        }
        return null;
    }

    /**
     * Selects the active comms
     */
    public CommsFeature getActiveComms() {
        return activeComms;
    }

}
