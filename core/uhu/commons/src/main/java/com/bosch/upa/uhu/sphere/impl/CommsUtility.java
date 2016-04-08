/**
 * 
 */
package com.bosch.upa.uhu.sphere.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;

/**
 * @author rishabh
 *
 */
public final class CommsUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommsUtility.class);
    private IUhuComms uhuComms;

    public CommsUtility(IUhuComms uhuComms) {
        this.uhuComms = uhuComms;
    }

    public boolean sendMessage(ControlMessage controlMessage) {
        final ControlLedger transControlMessage = new ControlLedger();
        transControlMessage.setMessage(controlMessage);
        transControlMessage.setSphereId(controlMessage.getSphereId());
        transControlMessage.setSerializedMessage(transControlMessage.getMessage().serialize());

        // send the message
        if (uhuComms != null) {
            LOGGER.debug("Sending message: " + controlMessage.getDiscriminator());
            return uhuComms.sendMessage(transControlMessage);
        }
        LOGGER.error("Failed to send message: " + controlMessage.getDiscriminator());
        return false;
    }
}
