package com.bezirk.comms.udp.listeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.IUhuCommsLegacy;
import com.bezirk.comms.UhuComms;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.comms.udp.validation.MessageValidators;
import com.bezrik.network.UhuNetworkUtilities;

public class ControlMulticastListener implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ControlMulticastListener.class);

    private final MulticastSocket multicastSocket;
	private Boolean running=false; 
	private ControlLedger receivedMessage;
	private InetAddress myAddress;
	private final ExecutorService executor;
    private IUhuCommsLegacy uhuComms = null;
    private ICommsNotification commsErrNotificationError = null;
    
	public ControlMulticastListener(MulticastSocket multicastSocket, IUhuCommsLegacy uhuComms,
									ICommsNotification commsNotificationCallback ) {
		this.multicastSocket = multicastSocket;
		this.commsErrNotificationError = commsNotificationCallback;
		executor = Executors.newFixedThreadPool(UhuComms.getPOOL_SIZE());
        this.uhuComms = uhuComms;
	}
	
	@Override
	public void run() {
		byte[] buf = new byte[UhuComms.getMAX_BUFFER_SIZE()];
		DatagramPacket receivePacket;
		try {
			multicastSocket.joinGroup(InetAddress.getByName(UhuComms.getCTRL_MULTICAST_ADDRESS()));	
			running=true;
			myAddress = UhuNetworkUtilities.getLocalInet();
			if(myAddress == null){
				log.error("Cannot resolve Ip: About to stop thread");
				return;
			}
			log.info("Control MulicastListener has Started");
			
		} catch (SocketException e1) {
            log.error("Error setting up Ctrl MulticastListener", e1);
		} catch (UnknownHostException e) {
            log.error("Error setting up Ctrl MulticastListener", e);
		} catch (IOException e) {
            log.error("Error setting up Ctrl MulticastListener", e);
		}

		while(running){
			if (Thread.interrupted()){
                log.info("Control MulicastListener has Stopped ");
				running = false;
				continue;
			}
			receivePacket = new DatagramPacket(buf, buf.length);
			try {
				multicastSocket.receive(receivePacket);
			} catch (SocketTimeoutException e){
				log.error("Socket has timed out", e);
			} catch (SocketException e){
                log.error("Control MulicastListener has Stopped \n", e);
				running = false;
				continue;
			}
			catch (IOException e) {
				log.error("IO Exception occured", e);
			}	

			receivedMessage = new ControlLedger();
			String computedSender = receivePacket.getAddress().getHostAddress().trim();
			if(!computedSender.equals(myAddress.getHostAddress())){
                log.info("RECEIVED ON Control Multicast: " + receivePacket.getLength() );
				receivedMessage.setIsMessageFromHost(false);
				try{
					if(ControlListenerUtility.constructMsg(receivedMessage, receivePacket,commsErrNotificationError)){
						Runnable worker = new MessageValidators(computedSender, receivedMessage, uhuComms);
						executor.execute(worker);
					}else{
						log.debug("Duplicate msg");
					}
				}catch(Exception e){
					log.error("Error in decrypting the message", e);
				}
			}
			else{
				//String retPayload = (String) UhuMessage.deserialize(received.split(",")[2].getBytes());
                log.info( "[DISCARD]Control Multicast Received: " + receivePacket.getLength()  );//+ " payload " + retPayload);
				continue;
			}
		}	
	}

	
	public void stop(){
		running = false;
	}
}