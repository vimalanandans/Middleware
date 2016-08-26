package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory creates and returns the comms based on features
 */
public class CommsFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommsFactory.class);

    com.bezirk.middleware.core.comms.CommsFeature activeComms = com.bezirk.middleware.core.comms.CommsFeature.COMMS_BEZIRK;

    public com.bezirk.middleware.core.comms.Comms getComms() {

        com.bezirk.middleware.core.comms.Comms comms = null;

        getActiveComms();

        logger.info("selected comms -> " + activeComms.name());

        switch (activeComms) {
            default:
            case COMMS_BEZIRK:
                //comms = new BezirkCommsManager();
                logger.debug("udp comms is not supported. ");
                break;
            case COMMS_ZYRE_JNI:
                logger.error("comms are injected by each platform, common won't do anything");
                break;
            case COMMS_JYRE:
                //comms =  new JyreCommsProcessor();
                logger.error("Jyre comms is not created. ");
                break;
            case COMMS_ZYRE:
                // todo
                break;
        }

        return comms;
    }

    /**
     * returns the comms configurations as commsFeature
     */
    public com.bezirk.middleware.core.comms.CommsFeature getActiveComms() {

        // if multiples are true, then first one is considered for selection
        if (com.bezirk.middleware.core.comms.CommsFeature.COMMS_BEZIRK.isActive()) {
            activeComms = com.bezirk.middleware.core.comms.CommsFeature.COMMS_BEZIRK;
        } else if (com.bezirk.middleware.core.comms.CommsFeature.COMMS_ZYRE.isActive()) {
            activeComms = com.bezirk.middleware.core.comms.CommsFeature.COMMS_ZYRE;
        } else if (com.bezirk.middleware.core.comms.CommsFeature.COMMS_ZYRE_JNI.isActive()) {
            activeComms = com.bezirk.middleware.core.comms.CommsFeature.COMMS_ZYRE_JNI;
        } else if (com.bezirk.middleware.core.comms.CommsFeature.COMMS_JYRE.isActive()) {
            activeComms = com.bezirk.middleware.core.comms.CommsFeature.COMMS_JYRE;
        } else { // default
            activeComms = CommsFeature.COMMS_BEZIRK;
        }

        return activeComms;
    }

}
