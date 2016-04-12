package com.bezirk.comms.udp.listeners;

import java.net.DatagramPacket;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.checksum.DuplicateMessageManager;
import com.bezirk.commons.UhuVersion;
import com.bezirk.comms.ICommsNotification;
import com.bezirk.control.messages.ControlLedger;

/**
 * This class is used a utility function for common methods used by the Control Listeners
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * @modifed Vijet Badigannavar Added a parameter to the constructMsg() and modified to give the callback 
 * if version mismatch occures
 */
public final class ControlListenerUtility {
    private static final Logger log = LoggerFactory.getLogger(ControlListenerUtility.class);

	private final static byte SEP = new String(",").getBytes()[0];
	
	
	  
	private ControlListenerUtility(){
		// This a utility class
	}
	
	public static boolean constructMsg(ControlLedger receivedMessage, DatagramPacket receivePacket,ICommsNotification notification) throws Exception{
		//Reconstruct the message SphereName, EncryptedBytes
		boolean isMsgDuplicate = false;
		byte[] received = new byte[receivePacket.getLength()];
		byte[] reconSphere = null;
		String sphereName = "";	
		System.arraycopy(receivePacket.getData(),receivePacket.getOffset(),received,0,receivePacket.getLength());			
		// Message format is bytes --> CS,SphereName,EncryptedMessage 
		int countSeperator = -1;
		int firstSEP = 0;
		int secSEP = 0;
		for(int i=0; i < received.length ; i++){
			if(received[i] == SEP){
				countSeperator++;
				switch (countSeperator) {
				case 0:
					String tempString = new String(Arrays.copyOfRange(received, 0, i));
					if(!tempString.toString().equals(UhuVersion.UHU_VERSION)){
						log.error("UPGRADE UHU. UHU VERSION MISMATCH. device version > "+UhuVersion.UHU_VERSION + " Received msg version "+tempString.toString());
						if(null != notification){
							notification.versionMismatch(tempString.toString());
						}
						return false;
					}
					firstSEP = i;
					break;
				case 1:
					byte[] receivedChecksum = Arrays.copyOfRange(received, firstSEP+1, i);
					secSEP = i;
					//do the handling
					isMsgDuplicate = DuplicateMessageManager.checkDuplicateEvent(receivedChecksum);
					receivedMessage.setChecksum(receivedChecksum);
					break;
				case 2:
					reconSphere = Arrays.copyOfRange(received, secSEP+1, i);
					sphereName = new String(reconSphere,0,reconSphere.length);
					receivedMessage.setSphereId(sphereName);
					byte[] encPayload = Arrays.copyOfRange(received, i+1, receivePacket.getLength());
					receivedMessage.setEncryptedMessage(encPayload);
					break;
				
				}
			
				if(isMsgDuplicate){
					//log.info("Duplicate Msg Received DROPPING PACKET; CHECKSUM = " + CheckSumUtil.bytesToHex(receivedMessage.getChecksum()) );
					//log.info("Duplicate Msg Received DROPPING PACKET");
					return false;
				}
			}					
		}
		return true;
	}
}
