package com.bosch.upa.uhu.rest;

import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.GenerateMsgId;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * this class will translate all the request to Bezirk EventLedger
 * @author PIK6KOR
 *
 */
public class BezirkRequestTranslator {
	
	
	/**
	 * this method will translate the request object to event ledger object. 
	 * This object will be passed to Comms, which will be communicated to other devices
	 * @return
	 */
	public EventLedger translateRequestToEventLedger(BezirkHttpRequest requestObject){
		EventLedger eventLedger = new EventLedger();
		
		//set sender service end point.
		UhuServiceId uhuServiceId = new UhuServiceId(requestObject.getEventServiceId(), requestObject.getUniqueEventId());
		UhuServiceEndPoint mySEP = UhuNetworkUtilities.getServiceEndPoint(uhuServiceId);
		
		
		//prepare the event based on the type, update the message with Origin and MessageId
		
		if(true){
			final StringBuilder uniqueMsgId = new StringBuilder(GenerateMsgId.generateEvtId(mySEP));
			
			// construct multicast header
			MulticastHeader multicastHeader = new MulticastHeader();
			
			// TODO ?? multicastHeader.setAddress(address);
			multicastHeader.setSenderSEP(mySEP);
			multicastHeader.setSphereName(requestObject.getEventSphere());
			multicastHeader.setTopic(requestObject.getEventTopic());
			multicastHeader.setUniqueMsgId(uniqueMsgId.toString());
			
			//set constructed header...
			eventLedger.setHeader(multicastHeader);
			eventLedger.setIsMulticast(true);
			eventLedger.setSerializedHeader(multicastHeader.serialize());
			eventLedger.setIsLocal(true);
			
		}
		else{
			
			//As discussed there is no Unicast!!!!! Client currently does not require a response from a single device
			
			//construct unicast header..
			/*UnicastHeader unicastHeader = new UnicastHeader();
			unicastHeader.setRecipient(null);
			unicastHeader.setSenderSEP(mySEP);
			unicastHeader.setSphereName("SphereName");
			unicastHeader.setTopic("HOW TO KNOW THE EVENT TOPIC");
			
			//Important to identify every request event
			unicastHeader.setUniqueMsgId(IMPORTANT);
			
			//set constructed header..
			eventLedger.setHeader(unicastHeader);
			eventLedger.setIsMulticast(false);*/
		}
		
		eventLedger.setSerializedMessage(requestObject.getEventMsg());
		
		return eventLedger;
	}
	
	
	/**
	 * This method will translate the Ledger response object from Comms to Client expected json response string
	 * @param eventLedger
	 * @return
	 */
	public String translateEventToClientResponse(EventLedger responseEventLedger){
		String clientResponse = null;
		if(responseEventLedger != null){
			clientResponse = responseEventLedger.getSerializedMessage();
		}
		return clientResponse;
	}
	
	
	
	
	

}
