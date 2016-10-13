/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory creates and returns the comms based on features
 */
public class CommsFactory {
    private static final Logger logger = LoggerFactory.getLogger(CommsFactory.class);

    private CommsFeature activeComms = CommsFeature.COMMS_BEZIRK;

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
