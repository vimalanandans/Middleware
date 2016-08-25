/**
 *
 */
package com.bezirk.middleware.core.sphere.impl;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.control.messages.ControlMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommsUtility {
    private static final Logger logger = LoggerFactory.getLogger(CommsUtility.class);

    private final Comms comms;

    public CommsUtility(Comms comms) {
        this.comms = comms;
    }

    public boolean sendMessage(ControlMessage controlMessage) {

        // send the message
        if (comms != null) {
            logger.debug("Sending message: " + controlMessage.getDiscriminator());
            return comms.sendControlMessage(controlMessage);
        }
        logger.error("Failed to send message: " + controlMessage.getDiscriminator());
        return false;
    }
}