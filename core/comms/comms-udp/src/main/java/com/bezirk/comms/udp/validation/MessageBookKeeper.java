package com.bezirk.comms.udp.validation;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.bezirk.control.messages.ControlLedger;

/**
 * This class is used for Book keeping sent messages
 * and on reception
 * This class is used to filter duplicate messages and also remove the message from the senderQ,
 * when we get the first response
 * @author Mansimar Aneja
 *
 */
/**
 * This module is no longer needed. because no one calls addmessage
 * and only the checking the record present in the message validator
 * FIXME: remove this module
 * - Vimal
 * */
public class MessageBookKeeper {
	//private static final Logger log = LoggerFactory.getLogger(MessageBookKeeper.class);
	//uniqueKey:ControlRecord
	private LinkedHashMap<String,ControlRecord> controlMap = new LinkedHashMap<String,ControlRecord>();
	private final static long FRESHNESS_VAL = 10000;
	private final static int MAX_SIZE = 50;

	private class ControlRecord {
		public ControlLedger message;
		private HashSet<String> responders = new HashSet<String>();
		public long orgTime;

		public ControlRecord(ControlLedger msg){
			this.message = msg;
			this.orgTime = new Date().getTime();
		}	

		public void updateResponder(String responder){
			this.responders.add(responder);
		}

		public void cleanMsg(){
			message = null;
		}
		
		public HashSet<String> getResponders(){
			return responders;
		}
	}

	/**
	 * On decryption, the MessageValidator workers should call this method
	 * @param msg 
	 * @return true if the message is to be processed
	 */
	public Boolean processMsg(ControlLedger msg){
		String uniqueKey = msg.getMessage().getUniqueKey();
		Boolean isMsg2BProcessed = false;
		if(controlMap.containsKey(uniqueKey)){ 
			//this is the case where a response is received for a request 
			isMsg2BProcessed = updateRecord(uniqueKey, msg);
		}
		else{
			//key is not present - just return true
			//this is the case where a request is received
			isMsg2BProcessed = true;
		}

		return isMsg2BProcessed;
	}

	/**
	 * This is called after a message is sent for book-Keeping    
	 * @param tcMsg
	 */
	public synchronized void addMessage(ControlLedger tcMsg){
		String uniqueKey = tcMsg.getMessage().getUniqueKey();
		ControlRecord newRecord = new ControlRecord(tcMsg);
		//if map is full : pop the head
		if(controlMap.keySet().size() == MAX_SIZE){ 
			// pop the head
			controlMap.remove(controlMap.keySet().iterator().next());
		}
		
		//update map with new record
		this.controlMap.put(uniqueKey, newRecord);
	}

	private Boolean updateRecord(String uniqueKey, ControlLedger msg){
		String sender = msg.getMessage().getSender().device;
		ControlRecord record = controlMap.get(uniqueKey);
		//If record is stale update with new record
		long curTime = new Date().getTime();
		if(curTime - record.orgTime > FRESHNESS_VAL){ //stale record
			ControlRecord newRecord = new ControlRecord(msg);
			controlMap.put(uniqueKey, newRecord);
		}
		ControlLedger message = record.message;
		if(message != null){ //this means you received your first response
			//Remove message from queue
			// commented the below for migrating uhuComms manager
			// since no longer this module is used - Vimal
			//MessageQueueManager.getControlSenderQueue().removeFromQueue(message);
            // not used anymore

			//Remove message from record
			record.cleanMsg();
		}
		//Check if the sender has already responded
		if(record.getResponders().contains(sender)){
			//message is a duplicate
			return false;
		}
		else{ //update the responders list
			record.updateResponder(sender);
			return true;
		}		
	}

}
