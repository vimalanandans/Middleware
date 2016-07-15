package com.bezirk.pubsubbroker.discovery;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.SphereServiceAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates to the Zirk / sadl discovery related classes
 */
public class DiscoveryManager implements CtrlMsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryManager.class);

    private PubSubBroker sadlManager;

    private Comms comms;

    SphereServiceAccess sphereServiceAccess;



    private Thread discThread;

    public DiscoveryManager(PubSubBroker sadlManager, Comms comms, SphereServiceAccess sphereServiceAccess) {
        this.sadlManager = sadlManager;
        this.comms = comms;
        this.sphereServiceAccess = sphereServiceAccess;
    }

    public boolean initDiscovery() {

        DiscoveryProcessor.setDiscovery(new Discovery());
        discThread = new Thread(new DiscoveryProcessor());

        /*SphereDiscoveryProcessor.setDiscovery(new SphereDiscovery(sphereDiscHandler));
        sphereDiscThread = new Thread(new SphereDiscoveryProcessor(sphereDiscHandler, this));*/

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryRequest, this);

        comms.registerControlMessageReceiver(ControlMessage.Discriminator.DiscoveryResponse, this);

        if (discThread != null)
            discThread.start();

        return true;
    }

    public boolean stopDiscovery() {
        //Interrupt Discovery Cleaner
        if (discThread != null)
            discThread.interrupt();
        return true;
    }

    @Override
    public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
        switch (id) {
            case DiscoveryRequest:
                final DiscoveryRequest req = ControlMessage.deserialize(serializedMsg, DiscoveryRequest.class);
                new DiscoveryRequestHandler(sadlManager, req, comms, sphereServiceAccess).getDiscoveryResponse();

                break;
            case DiscoveryResponse:
                //if tcMessage.message==null fromJson, else get tcMessage.message and typecast as response
                final DiscoveryResponse response = ControlMessage.deserialize(serializedMsg, DiscoveryResponse.class);
                if (DiscoveryProcessor.getDiscovery().addResponse(response)) {
                    logger.debug("Discovery Response added successfully");
                } else
                    logger.debug("Problem w adding response/invoking zirk listener");
                break;
            default:
                logger.error("Unknown control message > " + id);
                return false;
        }
        return true;
    }
}
