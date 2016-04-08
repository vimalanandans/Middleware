package com.bosch.upa.uhu.comms.udp.listeners;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.checksum.DuplicateMessageManager;
import com.bosch.upa.uhu.commons.UhuVersion;
import com.bosch.upa.uhu.comms.ICommsNotification;
import com.bosch.upa.uhu.control.messages.EventLedger;

/**
 * This class contains static methods used by the Event listeners
 * @author Mansimar Aneja
 * @modifed Vijet Badigannavar Added a parameter to the constructMsg() and modified to give the callback 
 * if version mismatch occures
 */
public final class EventListenerUtility {
	private static final Logger log = LoggerFactory.getLogger(EventListenerUtility.class);
	private final static byte SEP = new String(",").getBytes()[0];
        
	private EventListenerUtility(){
		//This is a utitlity class
	}
	
	public static Boolean constructMsg(EventLedger receivedMessage, DatagramPacket receivePacket,ICommsNotification errCallaback){
		//Set the message is not local field
		receivedMessage.setIsLocal(false);
		
		byte[] packetData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		byte[] reconHeader = null;
		String headerPart = "";	
		//Separate header from payload
		int lastSeenSep = 0;
		int headerCount = -2;
		Integer headerLength = 0;				
		receivedMessage.setIsMulticast(true);
		Boolean isHeaderSep = false;
		//FIX
		byte[] receivedChecksum = null;
		boolean isMsgDuplicate = false;
		for(int i=0; i < packetData.length ; i++){
			if(packetData[i] == SEP){					
				reconHeader = Arrays.copyOfRange(packetData, (lastSeenSep==0)?lastSeenSep:lastSeenSep+1, i);
				lastSeenSep = i;
				headerCount++;
				headerPart = new String(reconHeader,0,reconHeader.length);
				switch (headerCount){
				case -1:
					if(!headerPart.equals(UhuVersion.UHU_VERSION)){
						log.error("UPGRADE UHU. UHU VERSION MISMATCH. device version > "+UhuVersion.UHU_VERSION + " Recieved msg version "+headerPart);
						if(null != errCallaback){
							errCallaback.versionMismatch(headerPart);
						}
						return false;
					}
					break;
				case 0:
					receivedChecksum = Arrays.copyOfRange(reconHeader, 0, reconHeader.length);
					isMsgDuplicate = DuplicateMessageManager.checkDuplicateEvent(receivedChecksum);
					receivedMessage.setChecksum(receivedChecksum);
					break;
				case 1: 
					receivedMessage.getHeader().setSphereName(headerPart);
					break;
				case 2:
					headerLength = Integer.parseInt(headerPart);
					isHeaderSep = separateHeaderAndPayload(receivedMessage, lastSeenSep+1, headerLength, packetData);
					break;					
				}	
				if(isMsgDuplicate ){
					//log.info("Duplicate Msg Received DROPPING PACKET; CHECKSUM = " + CheckSumUtil.bytesToHex(receivedMessage.getChecksum()) );
					//log.info("Duplicate Msg Received DROPPING PACKET");
					return false;
				}
				if(headerCount==2 && isHeaderSep){
					return true;
				}				
			}	
		}
		log.info( " Failed: Failed to sep Header ");
		return false;			
	}

	private static Boolean separateHeaderAndPayload(EventLedger receivedMessage, int headerStartIndex, int headerLength, byte[] packetData){
		byte[] header = Arrays.copyOfRange(packetData, headerStartIndex, headerLength+headerStartIndex);
		receivedMessage.setEncryptedHeader(header);
		int preHeaderLen = headerStartIndex;
		byte[] payload = Arrays.copyOfRange(packetData, preHeaderLen+headerLength, packetData.length);
		receivedMessage.setEncryptedMessage(payload);
		if(receivedMessage.getEncryptedHeader().length == 0)
			return false;
		return true;
	}

}
