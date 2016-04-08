package com.bosch.upa.uhu.comms.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMsg;
import org.zyre.ZreInterface;

import com.bosch.upa.uhu.processor.CommsProcessor;

public class JyreReceiverThread extends Thread{

	public static final Logger log = LoggerFactory.getLogger(JyreReceiverThread.class);
	private Boolean running=true;
	private ZreInterface zreInterface = null;
	private String zreGroup;
	private CommsProcessor commsProcessor;


	public JyreReceiverThread(CommsProcessor commsProcessor, String zreGroup) {
		this.commsProcessor = commsProcessor;
		this.zreGroup = zreGroup;
	}

	@Override
	public void run() {
		if (zreGroup == null) {
			log.error("group not set");
			return;
		}
		if (zreInterface == null) {
			log.error("Zyre not set");
			return;
		}

		// joining the group
		zreInterface.join(zreGroup);

		while (running) {
			log.info("Jyre Receiver Thread has started \n");
			ZMsg incoming = null;
			try{
				incoming = zreInterface.recv();
			}catch(Exception e){
				log.error("*********** Exception during receive ********", e);
			}

			if (incoming == null) {// Interrupted
				log.error("Interrupted during recv()");
				break;
			}

			// fix : check the incomming msg
			String eventType = incoming.popString();
			String peer = incoming.popString();
			String payLoad = "Test";

			if (Thread.currentThread().isInterrupted()){
				log.info("Jyre Receiver Thread has Stopped");
				running = false;
				continue;
			}

			// Message sent to a particular device
			if (eventType.equals("WHISPER")) {
				handleWhisper(peer, payLoad);
			} 
			// Message sent to all members of a group
			else if (eventType.equals("SHOUT")) {
				handleShout(peer, payLoad);
			}else {
				log.info("unkown event received: ");
			}
		}

		// when false, destroy the ZRE context.
		zreInterface.destroy();
		log.debug("Jyre Interface has been destroyed!!! \n");
	}

	/**
	 * handle the uni-cast message received
	 * @param -incoming
	 */
	private void handleWhisper(String zyreDeviceId, String payload) {
		log.info("Received a incoming whisper");
		commsProcessor.processWireMessage(zyreDeviceId, payload);
	}

	/**
	 * handle the multi-cast message received
	 * @param -incoming
	 */
	private void handleShout(String zyreDeviceId, String payload) {
		log.info("received a Shout!!!!!");
		commsProcessor.processWireMessage(zyreDeviceId, payload);
	}

	/** initialize the Jyre */
	public boolean initJyre(){
		log.debug("Constructing ZreInterface!!");
		zreInterface = new ZreInterface();
		return true;
	}

	/**
	 * this will clsose the comms
	 * @return
	 */
	public boolean closeComms(){
		log.debug("closing comms!!");
		if(zreInterface != null){
			// when false, destroy the ZRE context.
			zreInterface.destroy();
			running = false;
			return true;
		}
		return false;
	}

	/** start the Jyre */
	public boolean startJyre(){
		if(zreInterface != null) {
			log.debug("ZreInterface has jjoined group : "+getGroup());
			// join the group
			zreInterface.join(getGroup());

			// start the receiver
			log.debug("Zre receiver thread has been started : ");
			start();

			return true;
		}
		return false;
	}

	/** stop the zyre */
	public boolean stopJyre(){

		//stop the receiver thread
		interrupt();
		log.debug("Zre receiver thread has been interupted!!");
		return true;
	}

	public String getGroup() {
		return zreGroup;
	}


}
