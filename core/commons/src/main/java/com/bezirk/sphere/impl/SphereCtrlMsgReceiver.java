package com.bezirk.sphere.impl;

import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.discovery.SphereDiscoveryProcessor;
import com.bezirk.sphere.api.IUhuSphereMessages;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(SphereCtrlMsgReceiver.class);

    IUhuSphereMessages uhuSphereMessages = null;

    SphereCtrlMsgReceiver(IUhuSphereMessages uhuSphereMessages) {
        this.uhuSphereMessages = uhuSphereMessages;
    }

    @Override
    /** from comms layer control message handler for sphere */
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {

        switch (id) {
            case SphereDiscoveryResponse:
                LOGGER.debug("Processing sphere Discovery Response");
                final SphereDiscoveryResponse discoveryResponse = ControlMessage.deserialize(serializedMsg,
                        SphereDiscoveryResponse.class);

                // Fixme: avoid global access
                SphereDiscoveryProcessor.getDiscovery().addResponse(discoveryResponse);
                break;
            case CatchRequest:
                final CatchRequest catchRequest = ControlMessage.deserialize(serializedMsg, CatchRequest.class);
                LOGGER.debug("Catch Request " + catchRequest.getSphereId());
                uhuSphereMessages.processCatchRequestExt(catchRequest);
                break;
            case CatchResponse:
                final CatchResponse catchResponse = ControlMessage.deserialize(serializedMsg, CatchResponse.class);

                LOGGER.debug("Catch Response " + catchResponse.getSphereId());
                uhuSphereMessages.processCatchResponse(catchResponse);
                break;
            case ShareRequest:
                ShareRequest shareRequest = ControlMessage.deserialize(serializedMsg, ShareRequest.class);
                LOGGER.debug("Share Request " + shareRequest.getSphereId());
                uhuSphereMessages.processShareRequest(shareRequest);
                break;
            case ShareResponse:
                ShareResponse shareResponse = ControlMessage.deserialize(serializedMsg, ShareResponse.class);
                LOGGER.debug("Share Response " + shareResponse.getSphereId());
                uhuSphereMessages.processShareResponse(shareResponse);
                break;
            default: // unregistered message
                LOGGER.error("unregistrered message. dispatcher map of is corrupted ? ");
                return false;
        }
        return true;
    }

    /**
     * register the control message id with message Dispatcher
     */
    public boolean initControlMessageListener(IUhuComms comms) {
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
            LOGGER.error("invalid comms");
        }
        return false;
    }

}
