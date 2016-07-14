package com.bezirk.sphere.impl;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.sphere.discovery.SphereDiscoveryProcessor;
import com.bezirk.sphere.api.SphereMessages;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Vimal on 5/19/2015. control message handler for sphere
 */
public class SphereCtrlMsgReceiver implements CtrlMsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(SphereCtrlMsgReceiver.class);

    SphereMessages sphereMessages = null;

    SphereCtrlMsgReceiver(SphereMessages sphereMessages) {
        this.sphereMessages = sphereMessages;
    }

    @Override
    /** from comms layer control message handler for sphere */
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {

        switch (id) {
            case SphereDiscoveryResponse:
                logger.debug("Processing sphere Discovery Response");
                final SphereDiscoveryResponse discoveryResponse = ControlMessage.deserialize(serializedMsg,
                        SphereDiscoveryResponse.class);

                // Fixme: avoid global access
                SphereDiscoveryProcessor.getDiscovery().addResponse(discoveryResponse);
                break;
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
            comms.registerControlMessageReceiver(ControlMessage.Discriminator.SphereDiscoveryResponse, this);
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
