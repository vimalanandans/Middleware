package com.bosch.upa.uhu.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.Ledger;
import com.bosch.upa.uhu.control.messages.UnicastHeader;

public class BezirkRestCallBackImpl  implements BezirkRestCallBack{
	//logger
	private static final Logger LOGGER = LoggerFactory.getLogger(BezirkRestCallBackImpl.class);
	
	//translator utilities class
	private final BezirkRequestTranslator translator = new BezirkRequestTranslator();
		
	private BezirkRestCommsManager bezirkCommsManager;
	
	public BezirkRestCallBackImpl() {
		this.bezirkCommsManager = BezirkRestCommsManager.getInstance();
	}
	
	/**
	 * appends the response to the map.
	 */
	@Override
	public void callBackForResponse(Ledger event) {
		//Response will be the EventLedger
		EventLedger eventLedger  = (EventLedger) event;
		
		//translate Ledger to Response object which will be sent to service
		String responseString  = translator.translateEventToClientResponse(eventLedger);
		
		//update the response in the map, it will be picked and returned to the client during the GET call.
		
		//extract the uniqueID and update it to the map
		//TODO :: Parse header either to UnicastHeader or MulticastHeader, and extract it.
		UnicastHeader header = (UnicastHeader)eventLedger.getHeader();
		Integer key = extractUniqueKey(header.getRecipient().serviceId.getUhuEventId());
		
		LOGGER.debug("Response for Key ::"+key+" is :: "+responseString);
		bezirkCommsManager.appendResponseToMap(key, responseString);
	}
	
	/**
	 * extracts the uniqueid recived from the service ID
	 * @param serviceID
	 * @return
	 */
	private Integer extractUniqueKey(String serviceID){
		
		String key = null;
		int startIndex = serviceID.indexOf("$");
		if(startIndex >-1){
			key = serviceID.substring(startIndex+1, serviceID.length());
		}
		
		LOGGER.debug("Extracted the key from service id : "+key);
		return Integer.valueOf(key);
		
	}

}
