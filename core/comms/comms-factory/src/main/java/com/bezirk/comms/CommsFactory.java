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

    CommsFeature activeComms = CommsFeature.COMMS_BEZIRK;

    public BezirkComms getComms() {

        BezirkComms bezirkComms = null;

        getActiveComms();

        log.info("selected comms -> " + activeComms.name());

        switch (activeComms) {
            default:
            case COMMS_BEZIRK:
                bezirkComms = new BezirkCommsManager();
                log.debug("udp comms is created. ");
                break;
            case COMMS_ZYRE_JNI:
                log.error("comms are injected by each platform, common won't do anything");
                break;
            case COMMS_JYRE:
                //bezirkComms =  new JyreCommsProcessor();
                log.error("Jyre comms is not created. ");
                break;
            case COMMS_ZYRE:
                // todo
                break;
        }

        return bezirkComms;
    }

    /**
     * returns the comms configurations as commsFeature
     */
    public CommsFeature getActiveComms() {

        // if multiples are true, then first one is considered for selection
        if (CommsFeature.COMMS_BEZIRK.isActive()) {
            activeComms = CommsFeature.COMMS_BEZIRK;
        } else if (CommsFeature.COMMS_ZYRE.isActive()) {
            activeComms = CommsFeature.COMMS_ZYRE;
        } else if (CommsFeature.COMMS_ZYRE_JNI.isActive()) {
            activeComms = CommsFeature.COMMS_ZYRE_JNI;
        } else if (CommsFeature.COMMS_JYRE.isActive()) {
            activeComms = CommsFeature.COMMS_JYRE;
        } else { // default
            activeComms = CommsFeature.COMMS_BEZIRK;
        }

        return activeComms;
    }

}
