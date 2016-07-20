package com.bezirk.pubsubbroker.discovery;

import com.bezirk.comms.Comms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubBrokerControlReceiver;
import com.bezirk.sphere.api.SphereServiceAccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


/**
 * This Class computes a DiscoveryResponse for a DiscoveryRequest
 * It handles Discovery from Spheres and Services
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */

public class DiscoveryRequestHandler {
    private final static Logger logger = LoggerFactory.getLogger(DiscoveryRequestHandler.class);

    private final PubSubBrokerControlReceiver sadlCtrl;
    private final DiscoveryResponse response;
    //private final MessageQueue ctrlSenderQueue;
    private final Comms comms;
    private final DiscoveryRequest discReq;
    SphereServiceAccess serviceAccess ;
    /**
     * @param req incoming DiscoveryRequest
     */
    public DiscoveryRequestHandler(PubSubBrokerControlReceiver sadlCtrl, DiscoveryRequest req,  Comms comms, SphereServiceAccess serviceAccess) {
        this.sadlCtrl = sadlCtrl;
        this.comms = comms;
        this.discReq = req;
        this.serviceAccess = serviceAccess;

        response = new DiscoveryResponse(discReq.getSender(), discReq.getSphereId(), discReq.getUniqueKey(), discReq.getDiscoveryId());

    }

    public void getDiscoveryResponse() {
        final Boolean success = handleSphereDiscovery(discReq);

        if (success) {
            populateReceiverQueue(response);
            logger.debug("DiscoveryResponse created successfully");
        } else {
            logger.debug("Nothing could be discovered");
        }
    }


    private Boolean handleSphereDiscovery(DiscoveryRequest discoveryRequest) {

        serviceAccess.processSphereDiscoveryRequest(discoveryRequest);

        return true;
    }


    private void populateReceiverQueue(DiscoveryResponse response) {
        ControlLedger responseMsg = new ControlLedger();
        responseMsg.setMessage(response);
        responseMsg.setSerializedMessage(responseMsg.getMessage().serialize());
        responseMsg.setSphereId(response.getSphereId());
        //ctrlSenderQueue.addToQueue(responseMsg);
        comms.sendMessage(responseMsg);
    }
}
