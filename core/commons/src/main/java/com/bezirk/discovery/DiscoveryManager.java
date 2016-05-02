package com.bezirk.discovery;

import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.sadl.BezirkSadlManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates to the Zirk / sadl discovery related classes
 */
public class DiscoveryManager implements CtrlMsgReceiver {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryManager.class);

    private BezirkSadlManager sadlManager;

    private BezirkComms comms;

    private Thread discThread;

    public DiscoveryManager(BezirkSadlManager sadlManager, BezirkComms comms) {
        this.sadlManager = sadlManager;
        this.comms = comms;
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
                new DiscoveryRequestHandler(sadlManager, req, comms).getDiscoveryResponse();

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
