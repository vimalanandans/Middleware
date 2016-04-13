package com.bezirk.comms.udp.sender;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.checksum.UhuCheckSum;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.commons.UhuVersion;
import com.bezirk.comms.UhuComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.remotelogging.messages.UhuLoggingMessage;
import com.bezirk.remotelogging.queues.LoggingQueueManager;
import com.bezirk.remotelogging.spherefilter.FilterLogMessages;
import com.bezirk.remotelogging.status.LoggingStatus;
import com.bezirk.remotelogging.util.Util;

/**
 * @author Mansimar Aneja (mansimar.aneja@us.bosch.com)
 * The UhuCommsSend represents the comms layer in the sender side. It supports both unicast and multicast
 * In the current implementation, unicast and multicast are based on UDP
 * Both events and ctrl messages use this to send messages
 */

public class UhuCommsSend {
    private static final Logger log = LoggerFactory.getLogger(UhuCommsSend.class);
    private final static Date currentDate = new Date();
    private final static String SEP = ",";
    
    /**
     * The SenderThread invokes this method after the Sphere Layer returns true.
     * Note: The UhuCommsSend sends a unicast if the recipient address has been set and multicast if the recipient address is not set
     * @param tcMessage the Message to be sent
     * @return true if the message is sent successfully
     * @see com.bezirk.control.messages.EventControlMessage
     * @see com.bezirk.comms.udp.threads.ReceiverThread
     */
    public static Boolean send(EventLedger tcMessage){
        boolean isSent = false;
        //Update the packaged message
         // updateMessage(tcMessage);
        // Update the Message
        tcMessage.setLastSent(currentDate.getTime());
		//Update number of sends
		tcMessage.setNumOfSends(tcMessage.getNumOfSends()+1);
        
        //Construct a message header using the updated message
        // header msgId, sender, BoolAck, AckId
        //checksum,sphereName,HeaderLenght,encrypted(Header+Data)
        if(tcMessage.getNumOfSends() == 1){
        	String preHeader =  "," + tcMessage.getHeader().getSphereName()+",";

            byte[] header = UhuCompManager.getSphereForSadl().encryptSphereContent(tcMessage.getHeader().getSphereName(), tcMessage.getSerializedHeader());
            if(header == null){
                log.error(" Failed: Header cannot be encrypted");
                return false;
            }
            // Set the encrypted Header
            tcMessage.setEncryptedHeader(header);
            
            preHeader+=Integer.toString(header.length)+",";
            byte[] payload = tcMessage.getEncryptedMessage();
            if(payload == null){
                payload = new String("null").getBytes();
            }
            byte[] sendData = new byte[preHeader.getBytes().length+ header.length + payload.length];
            //Copy header and payload
            System.arraycopy(preHeader.getBytes(),0,sendData,0,preHeader.getBytes().length);
            System.arraycopy(header,0,sendData,preHeader.getBytes().length,header.length);
            System.arraycopy(payload,0,sendData,preHeader.getBytes().length+header.length,payload.length);

            if(sendData == null){
                log.error("  Failed: trying to send null data");
                return false;
            }

            //fill the checksum
            tcMessage.setChecksum(UhuCheckSum.computeCRC(sendData));
            
            StringBuilder tempString = new StringBuilder(UhuVersion.UHU_VERSION+SEP);
            byte[] dataOnWire = new byte[tempString.toString().getBytes().length + tcMessage.getChecksum().length + sendData.length];
            try{
            //arraycopy(Object src, int srcPos, Object dest, int destPos, int length);
            	System.arraycopy(tempString.toString().getBytes(),0,dataOnWire,0,tempString.toString().length());
            	System.arraycopy(tcMessage.getChecksum(),0,dataOnWire,tempString.toString().length(),tcMessage.getChecksum().length);
            	System.arraycopy(sendData,0,dataOnWire,(tempString.toString().length()+tcMessage.getChecksum().length),sendData.length);
            	tcMessage.setDataOnWire(dataOnWire);
            }catch(Exception e){
            	log.error("Null Pointer exception while sending the data on the stack", e);
            	return false;
            }
        }
        
        byte[] dataOnWire = tcMessage.getDataOnWire();
        if(null == dataOnWire){
        	log.error("Trying to send null data");
        	return false;
        }
        // Can we make this logic simple.
        String recipient = null;
        if(tcMessage.getIsMulticast()){//multicast
        	isSent = sendMulticast(dataOnWire, true);
        }
        else{ //unicast
        	
        	UnicastHeader uHeader = (UnicastHeader) tcMessage.getHeader();
        	if(null == uHeader || uHeader.getRecipient() == null || uHeader.getRecipient().device == null || uHeader.getRecipient().device.isEmpty()){
        		log.error( " Message not of accepted type");
        		isSent = false;
        	}
        	recipient = uHeader.getRecipient().device;
        	isSent = sendUnicast(dataOnWire, recipient, true);
        }

        if(isSent && LoggingStatus.isLoggingEnabled() && tcMessage.getNumOfSends()==1 && FilterLogMessages.checkSphere(tcMessage.getHeader().getSphereName())){
        	sendEventLogMessage(tcMessage,recipient);
        }
        return isSent;
    }


    /**
     * This method sends the log message to the remote service if the logging is enabled.
     * @param eLedger is used to extract the contents needed to fill the log message
     * @param recipient is used to set the recipient in the log Message.
     */
    private static void sendEventLogMessage(final EventLedger eLedger, final String recipient){
    	try {
			LoggingQueueManager.loadLogSenderQueue(new UhuLoggingMessage(eLedger.getHeader().getSphereName(),
					String.valueOf(currentDate.getTime()), UhuCompManager.getUpaDevice().getDeviceName(),recipient, eLedger.getHeader().getUniqueMsgId(),
					eLedger.getHeader().getTopic(), Util.LOGGING_MESSAGE_TYPE.EVENT_MESSAGE_SEND.name(),Util.LOGGING_VERSION).serialize());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
    }
    /**
     * This method sends the log message to the remote service if the logging is enabled.
     * @param eLedger is used to extract the contents needed to fill the log message
     * @param recipient is used to set the recipient in the log Message.
     */
    private static void sendControlLogMessage(ControlLedger tcMsg, String recipient){
    	try {
    		LoggingQueueManager.loadLogSenderQueue(new UhuLoggingMessage(tcMsg.getSphereId(),
    				String.valueOf(currentDate.getTime()), UhuCompManager.getUpaDevice().getDeviceName(),recipient, tcMsg.getMessage().getUniqueKey(),
    				tcMsg.getMessage().getDiscriminator().toString(), Util.LOGGING_MESSAGE_TYPE.CONTROL_MESSAGE_SEND.name(),Util.LOGGING_VERSION).serialize());
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
    }
    
	private static boolean sendMulticast(byte[] sendData, boolean isEvent) {
		InetAddress ipAddress;
		DatagramPacket sendPacket;
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			if(isEvent){
				ipAddress = InetAddress.getByName(UhuComms.getMULTICAST_ADDRESS());
				sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, UhuComms.getMULTICAST_PORT());
			}
			else{
				ipAddress = InetAddress.getByName(UhuComms.getCTRL_MULTICAST_ADDRESS());
				sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, UhuComms.getCTRL_MULTICAST_PORT());
			}
			clientSocket.send(sendPacket);
			clientSocket.close();
			log.debug( "multicast Sent");
			return true;

		} catch (UnknownHostException e) {
			log.error( " Problem sending Muliticasts",e);
			return false;
		} catch (SocketException e) {
			log.error( " Problem sending Muliticasts",e);
			return false;
		} catch (IOException e) {
			log.error(" Problem sending Muliticasts",e);
			return false;
		}

	}

	private static boolean sendUnicast(byte[] sendData, String address, boolean isEvent) {
		InetAddress ipAddress;
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			ipAddress = InetAddress.getByName(address);
			DatagramPacket sendPacket;
			if(isEvent)
				sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, UhuComms.getUNICAST_PORT());
			else
				sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, UhuComms.getCTRL_UNICAST_PORT());
			clientSocket.send(sendPacket);
			clientSocket.close();
			return true;

		} catch (UnknownHostException e) {
			log.error( " Problem sending Unicast",e);
			return false;
		} catch (SocketException e) {
			log.error( " Problem sending Unicast",e);
			return false;
		} catch (IOException e) {
			log.error( " Problem sending Unicast",e);
			return false;
		}
	}

	public static boolean sendctrl(ControlLedger tcMsg){
		tcMsg.setLastSent(currentDate.getTime());
		//Update number of sends
		tcMsg.setNumOfSends(tcMsg.getNumOfSends()+1);
		
		if(tcMsg.getNumOfSends() == 1){ //only do this for the for the first transmit
			//1a. Compute the Encrypted Msg
			byte[] encryptMsg = UhuCompManager.getSphereForSadl().encryptSphereContent(tcMsg.getMessage().getSphereId(), tcMsg.getSerializedMessage());
			if(null == encryptMsg){
				log.info( "Uhu Sphere Failed: Could not encrypt msg");
				return false;
			}
			//1b. set encrypted message
			tcMsg.setEncryptedMessage(encryptMsg);
			
			//2a. compute the data to be sent
			String msgOpen = ","+tcMsg.getMessage().getSphereId()+",";
			byte[] msgClosed = tcMsg.getEncryptedMessage();
			if(msgClosed == null){
				log.error( " Problem with encrypting message");
				return false;
			}
			//Create the data to be sent in bytes
			byte[] sendData = new byte[msgOpen.getBytes().length+msgClosed.length];
			//Copy header and payload
			System.arraycopy(msgOpen.getBytes(),0,sendData,0,msgOpen.getBytes().length);
			System.arraycopy(msgClosed,0,sendData,msgOpen.getBytes().length,msgClosed.length);
			//2b. Set the sendData
			
			tcMsg.setChecksum(UhuCheckSum.computeCRC(sendData));
			StringBuilder tempString = new StringBuilder(UhuVersion.UHU_VERSION+SEP);
			byte[] dataOnWire = new byte[tempString.toString().getBytes().length+tcMsg.getChecksum().length+sendData.length];
			try{
				System.arraycopy(tempString.toString().getBytes(),0,dataOnWire,0,tempString.toString().length());
				System.arraycopy(tcMsg.getChecksum(),0,dataOnWire,tempString.toString().length(),tcMsg.getChecksum().length);
				System.arraycopy(sendData,0,dataOnWire,(tempString.toString().length()+tcMsg.getChecksum().length),sendData.length);
				tcMsg.setDataOnWire(dataOnWire);
			}catch(Exception e){
				log.error("Error in creating the dataOnWire field of the control message", e);
			}
		}

		boolean isSent = false;
		String recipient = null;
		//Send the message
		if(tcMsg.getMessage() instanceof MulticastControlMessage){
			log.info("About to send: "+tcMsg.getMessage().getDiscriminator().toString());
			isSent = UhuCommsSend.sendMulticast(tcMsg.getDataOnWire(), false);
		}
		else if(tcMsg.getMessage() instanceof UnicastControlMessage){
			log.info("About to send: "+tcMsg.getMessage().getDiscriminator().toString());
			UnicastControlMessage uMsg = (UnicastControlMessage) tcMsg.getMessage();
			recipient = uMsg.getRecipient().device;
			isSent = UhuCommsSend.sendUnicast(tcMsg.getDataOnWire(), recipient, false);
		}
	
		if(isSent && LoggingStatus.isLoggingEnabled() && tcMsg.getNumOfSends()==1){
			sendControlLogMessage(tcMsg,recipient);
        }
		
		return isSent;
	}    
}