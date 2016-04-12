package com.bezirk.discovery;

import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.comms.IUhuComms;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sadl.ISadlControlReceiver;


/**
 * This Class computes a DiscoveryResponse for a DiscoveryRequest
 * It handles Discovery from Spheres and Services
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 *
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
	public DiscoveryRequestHandler(ISadlControlReceiver sadlCtrl, DiscoveryRequest req, IUhuComms uhuComms){
		this.sadlCtrl = sadlCtrl;
        this.uhuComms = uhuComms;
        this.discReq= req;
        response = new DiscoveryResponse(discReq.getSender(), discReq.getSphereId(), discReq.getUniqueKey(), discReq.getDiscoveryId());
     	
	}


	public void getDiscoveryResponse() {
		
        Boolean success = false;
     	success = (null == discReq.getProtocol() )? handleSphereDiscovery(discReq) : handleServiceDiscovery(discReq);
		if(success){
			populateReceiverQueue(response);
			log.debug("DiscoveryResponse created successfully");
		}
		else{
			log.debug("Nothing could be discovered");
		}
     	
	}




	
	private Boolean handleSphereDiscovery(DiscoveryRequest discoveryRequest){
		
		UhuCompManager.getSphereForSadl().processSphereDiscoveryRequest(discoveryRequest);
		
		return true;
	}
	
	private Boolean handleServiceDiscovery(DiscoveryRequest req){
		Set<UhuDiscoveredService> dServiceList = this.sadlCtrl.discoverServices(req.getProtocol(), req.getLocation());
		if(null==dServiceList || dServiceList.isEmpty()){
			return false;
		}
		Iterator<UhuDiscoveredService> dServices = dServiceList.iterator();
		while(dServices.hasNext()){
			UhuDiscoveredService dService = dServices.next();
			UhuServiceId sid = ((UhuServiceEndPoint)dService.getServiceEndPoint()).getUhuServiceId();
			
			if(UhuCompManager.getSphereForSadl().isServiceInSphere(sid, req.getSphereId())){
				//Set the Service Name
				dService.name = UhuCompManager.getSphereForSadl().getServiceName(sid);
				//Populate response service list
				response.getServiceList().add(dService);
			}			
		}
		if(null == response.getServiceList() || response.getServiceList().isEmpty()){
			return false;
		}
		
		return true;
	}

	
	private void populateReceiverQueue(DiscoveryResponse response){
		 ControlLedger responseMsg = new ControlLedger();
         responseMsg.setMessage(response);
         responseMsg.setSerializedMessage(responseMsg.getMessage().serialize());
         responseMsg.setSphereId(response.getSphereId());
         //ctrlSenderQueue.addToQueue(responseMsg);
		uhuComms.sendMessage(responseMsg);
	}

}
