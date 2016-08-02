/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rishabh
 */
public final class CommsUtility {
    private static final Logger logger = LoggerFactory.getLogger(CommsUtility.class);

    private Comms comms;

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
