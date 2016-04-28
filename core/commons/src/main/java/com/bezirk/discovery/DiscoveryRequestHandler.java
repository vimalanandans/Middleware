package com.bezirk.discovery;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sadl.ISadlControlReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;


/**
 * This Class computes a DiscoveryResponse for a DiscoveryRequest
 * It handles Discovery from Spheres and Services
 *
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 */

public class DiscoveryRequestHandler {
    private final static Logger log = LoggerFactory.getLogger(DiscoveryRequestHandler.class);

    private final ISadlControlReceiver sadlCtrl;
    private final DiscoveryResponse response;
    //private final MessageQueue ctrlSenderQueue;
    private final IUhuComms uhuComms;
    private final DiscoveryRequest discReq;

    /**
     * @param req incoming DiscoveryRequest
     */
    public DiscoveryRequestHandler(ISadlControlReceiver sadlCtrl, DiscoveryRequest req, IUhuComms uhuComms) {
        this.sadlCtrl = sadlCtrl;
        this.uhuComms = uhuComms;
        this.discReq = req;
        response = new DiscoveryResponse(discReq.getSender(), discReq.getSphereId(), discReq.getUniqueKey(), discReq.getDiscoveryId());

    }

    public void getDiscoveryResponse() {
        final Boolean success = (null == discReq.getProtocol()) ? handleSphereDiscovery(discReq) :
                handleZirkDiscovery(discReq);

        if (success) {
            populateReceiverQueue(response);
            log.debug("DiscoveryResponse created successfully");
        } else {
            log.debug("Nothing could be discovered");
        }
    }


    private Boolean handleSphereDiscovery(DiscoveryRequest discoveryRequest) {

        BezirkCompManager.getSphereForSadl().processSphereDiscoveryRequest(discoveryRequest);

        return true;
    }

    private Boolean handleZirkDiscovery(DiscoveryRequest req) {
        Set<BezirkDiscoveredZirk> dZirkList = this.sadlCtrl.discoverZirks(req.getProtocol(), req.getLocation());
        if (null == dZirkList || dZirkList.isEmpty()) {
            return false;
        }
        Iterator<BezirkDiscoveredZirk> dZirks = dZirkList.iterator();
        while (dZirks.hasNext()) {
            BezirkDiscoveredZirk dZirk = dZirks.next();
            BezirkZirkId sid = ((BezirkZirkEndPoint) dZirk.getZirkEndPoint()).getBezirkZirkId();

            if (BezirkCompManager.getSphereForSadl().isZirkInSphere(sid, req.getSphereId())) {
                //Set the Zirk Name
                dZirk.name = BezirkCompManager.getSphereForSadl().getZirkName(sid);
                //Populate response zirk list
                response.getZirkList().add(dZirk);
            }
        }
        if (null == response.getZirkList() || response.getZirkList().isEmpty()) {
            return false;
        }

        return true;
    }


    private void populateReceiverQueue(DiscoveryResponse response) {
        ControlLedger responseMsg = new ControlLedger();
        responseMsg.setMessage(response);
        responseMsg.setSerializedMessage(responseMsg.getMessage().serialize());
        responseMsg.setSphereId(response.getSphereId());
        //ctrlSenderQueue.addToQueue(responseMsg);
        uhuComms.sendMessage(responseMsg);
    }

}
