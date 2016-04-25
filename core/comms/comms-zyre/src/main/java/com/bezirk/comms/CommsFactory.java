package com.bezirk.comms;

import com.bezirk.features.CommsFeature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by vnd2kor on 8/18/2015.
 * The factory creates and returns the comms based on features
 */
public class CommsFactory {

    private static final Logger log = LoggerFactory.getLogger(CommsFactory.class);

    CommsFeature activeComms = CommsFeature.COMMS_UHU;

    IUhuComms getComms() {

        if (!CommsFeature.COMMS_UHU.isActive() && !CommsFeature.COMMS_ZYRE.isActive()) {
            log.error("both comms are not active. Selecting only the default comms");
            // logger
        }

        // we can't keep both comms active. not yet.
        if (CommsFeature.COMMS_UHU.isActive() && CommsFeature.COMMS_ZYRE.isActive()) {
            log.error("both comms active. Selecting only the default comms");
            // logger
        } else if (CommsFeature.COMMS_ZYRE.isActive()) {

            activeComms = CommsFeature.COMMS_ZYRE;

            log.debug("zyre comms is selected");
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
