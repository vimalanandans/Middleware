package com.bezirk.middleware.core.sphere.impl;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.sphere.api.SphereMessages;
import com.bezirk.middleware.core.sphere.messages.CatchRequest;
import com.bezirk.middleware.core.sphere.messages.CatchResponse;
import com.bezirk.middleware.core.sphere.messages.ShareRequest;
import com.bezirk.middleware.core.sphere.messages.ShareResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SphereCtrlMsgReceiver implements CtrlMsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(SphereCtrlMsgReceiver.class);

    private SphereMessages sphereMessages = null;

    SphereCtrlMsgReceiver(SphereMessages sphereMessages) {
        this.sphereMessages = sphereMessages;
    }

    @Override
    /** from comms layer control message handler for sphere */
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {

        switch (id) {
            case CatchRequest:
                final CatchRequest catchRequest = ControlMessage.deserialize(serializedMsg, CatchRequest.class);
                logger.debug("Catch Request " + catchRequest.getSphereId());
                sphereMessages.processCatchRequestExt(catchRequest);
                break;
            case CatchResponse:
                final CatchResponse catchResponse = ControlMessage.deserialize(serializedMsg, CatchResponse.class);

                logger.debug("Catch Response " + catchResponse.getSphereId());
                sphereMessages.processCatchResponse(catchResponse);
                break;
            case ShareRequest:
                ShareRequest shareRequest = ControlMessage.deserialize(serializedMsg, ShareRequest.class);
                logger.debug("Share Request " + shareRequest.getSphereId());
                sphereMessages.processShareRequest(shareRequest);
                break;
            case ShareResponse:
                ShareResponse shareResponse = ControlMessage.deserialize(serializedMsg, ShareResponse.class);
                logger.debug("Share Response " + shareResponse.getSphereId());
                sphereMessages.processShareResponse(shareResponse);
                break;
            default: // unregistered message
                logger.error("unregistered message. dispatcher map of is corrupted ? ");
                return false;
        }
        return true;
    }

    /**
     * register the control message id with message Dispatcher
     */
    public boolean initControlMessageListener(Comms comms) {
        if (comms != null) {
            /**
             * register all the control messages, in which sphere is interested
             */
            comms.registerControlMessageReceiver(ControlMessage.Discriminator.CatchRequest, this);
            comms.registerControlMessageReceiver(ControlMessage.Discriminator.CatchResponse, this);
            comms.registerControlMessageReceiver(ControlMessage.Discriminator.ShareRequest, this);
            comms.registerControlMessageReceiver(ControlMessage.Discriminator.ShareResponse, this);
            return true;
        } else {
            logger.error("invalid comms");
        }
        return false;
    }

}
