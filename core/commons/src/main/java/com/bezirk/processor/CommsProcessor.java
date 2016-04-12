package com.bezirk.processor;


import com.bezirk.comms.CommsProperties;
import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.ICtrlMsgReceiver;
import com.bezirk.comms.UhuComms;
import com.bezirk.logging.LogServiceMessageHandler;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.sphere.security.UPABlockCipherService;
import com.bezirk.streaming.UhuStreamManager;
import com.bezirk.util.TextCompressor;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.MessageDispatcher;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.features.CommsFeature;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Vimal on 11/19/2015.
 * This handles generic comms processing
 * new comms implementations shall use this as base class
 */

public abstract class CommsProcessor implements IUhuComms{

	private static final Logger log = LoggerFactory.getLogger(CommsProcessor.class);

	// thread pool size
	private static final int THREAD_SIZE = 4;

	MessageDispatcher msgDispatcher = null;

	//private UhuStreamManager uhuStreamManager = null;

	UhuSadlManager uhuSadlManager = null;

	//LogServiceMessageHandler logServiceMsgHandler = null;
	/**
	 * Version Callback that will be used to inform the platfroms when there is mismatch in versions.
	 * This parameter will be injected in all the components that will be checking for versions to
	 * be compatible before they are processed.
	 *
	 */
	private com.bezirk.comms.ICommsNotification notification = null;

	//generic notifications

	List ICommsNotification = new ArrayList<ICommsNotification> ();

	private IUhuSphereForSadl sphereForSadl;

	//private IUhuCallback uhuCallback = null;

	private ExecutorService executor;


	private UhuStreamManager uhuStreamManager = null;

	private final UPABlockCipherService cipherService = new UPABlockCipherService();

	//private final byte[] testKey = {'B','E','Z','I','R','K','_','G','R','O','U','P','N','E','W','1'};

	//Quickfix : using the logging manger from udp comms. fix this.
	LogServiceMessageHandler logServiceMsgHandler = null;

	CommCtrlReceiver ctrlReceiver = new CommCtrlReceiver();
	

	@Override
	public boolean initComms(CommsProperties commsProperties, InetAddress addr,
			UhuSadlManager sadl, PipeManager pipe) {

		this.uhuSadlManager = sadl;

		msgDispatcher = new MessageDispatcher(uhuSadlManager);

		if(UhuComms.isStreamingEnabled()){

			uhuStreamManager = new UhuStreamManager(this,msgDispatcher,sadl);

			uhuStreamManager.initStreams();

		}

		// register the logging service message
		msgDispatcher.registerControlMessageReceiver(ControlMessage.Discriminator.LoggingServiceMessage,ctrlReceiver);

		return true;
	}

	@Override
	public boolean startComms() {

		// create thread pool. every start creates new thread pool.
		// old ones are cleared with stopComms
		executor = Executors.newFixedThreadPool(THREAD_SIZE);

		if(UhuComms.isStreamingEnabled()){

			if (uhuStreamManager != null) {
				uhuStreamManager.startStreams();
			}
		}
		

		return true;
	}

	@Override
	public boolean stopComms() {

		if(executor != null) {
			executor.shutdown();
			// will shutdown eventually
			// if any poroblem persists wait for some time (less then 200ms) using awaitTermination
			// and then shutdownnow
		}

		if(UhuComms.isStreamingEnabled()){

			if (uhuStreamManager != null) {
				uhuStreamManager.endStreams();
			}
		}
		
		return true;
	}

	@Override
	public boolean closeComms() {
		/* in stop we end streams already, so skip the below
        if (uhuStreamManager != null) {
            uhuStreamManager.endStreams();
        } */

		return true;
	}


	@Override
	public boolean sendMessage(Ledger message) {
		// send as it is
		if(message instanceof ControlLedger)
			return this.sendControlMessage((ControlLedger) message);
		else if (message instanceof EventLedger)
			return this.sendEventMessage((EventLedger) message);
		else if (message instanceof MessageLedger)
			return sendMessageLedger((MessageLedger)message);
		else // stream ledger // hopefully there are no other types
			return this.sendStreamMessage(message);

		// FIXME: Bridge the local message. look udp sendControlMessage


	}

	/**
	 * Send the control message
	 * */
	public boolean sendControlMessage(ControlLedger message)  {
		boolean ret = false;
		String data = message.getSerializedMessage();
		if(data != null){
			byte[] wireByteMessage = null;


			if(message.getMessage() instanceof MulticastControlMessage){

				WireMessage wireMessage = prepareWireMessgae(message.getMessage().getSphereId(),data);

				wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_CTRL);
				try {
					wireByteMessage = wireMessage.serialize();

					//log.info("wireData size after "+wireData.length );
				} catch (IOException e) {
					log.error("unable to serialize the wire msg "+e);
					return ret;
				}

				ret = sendToAll(wireByteMessage,false);

				// bridge local
				bridgeControlMessage(getDeviceId(),message);

			}
			else if(message.getMessage() instanceof UnicastControlMessage){

				UnicastControlMessage uMsg = (UnicastControlMessage) message.getMessage();

				String recipient = uMsg.getRecipient().device;

				/*if(isLocalMessage(recipient))
				{
					return bridgeControlMessage(getDeviceId(),message);
				}
				else */{
					WireMessage wireMessage = prepareWireMessgae(message.getMessage().getSphereId(), data);

					wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_CTRL);

					try {
						wireByteMessage = wireMessage.serialize();

					} catch (IOException e) {
						log.error("unable to serialize the wire msg " + e);
						return false;
					}


					//ret = sendToOne(wireByteMessage, recipient, false);
					// quick fix . since we are not able to extract the device from incoming message
					// unicast reply fails, so send everything as multicast
					ret = sendToAll(wireByteMessage,false);
				}

			}
			else{
				log.debug("unknown control message");
			}
		}


		return ret;
	}

	/**
	 * prepares the WireMessage and returns it based on encryption and compression settings
	 * @param data
	 * @return
	 */
	private WireMessage prepareWireMessgae(String sphereId,String data) {
		WireMessage wireMessage = new WireMessage();
		wireMessage.setSphereId(sphereId);
		byte[] wireData = null;
		
		/**
		 * ##########
		 * Step 1 :Compression :  Do the compression if message compression is enabled
		 * ##########
		 */
		if(CommsFeature.WIRE_MSG_COMPRESSION.isActive()){
			wireData = compressMsg(data);
			wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_COMPRESSED);
		}
		
		/**
		 * ##########
		 * Step 2 :Encryption :  perform encryption if it is enabled.
		 * ##########
		 */
		if(CommsFeature.WIRE_MSG_ENCRYPTION.isActive()){
			
			if(wireData != null){
				//means compression has happened, now encrypt the content
				wireData = encryptMsg(wireMessage.getSphereId(),wireData);
				wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED);
			}else{
				//means compression was not enabled, now encrypt the msg content only
				wireData = encryptMsg(sphereId,data.getBytes());
				wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED);
			}
			
		}
		
		/**
		 * ##########
		 * Step 3 : Check :  If the Compression and Encryption is disabled. set the Raw message
		 * ##########
		 */
		//set data to wire message
		if(wireData == null){
			// this means compression and encryption both were disabled
			wireData = data.getBytes();
			wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_RAW);
		}

		//@punith storing the byte stream increase the wiredata serialization huge
		// either store as string or send the data as flat like earlier udp implementation
		wireMessage.setMsg(wireData);
		return wireMessage;
	}

	/**
	 * This wil compress the msg data
	 * @param data
	 * @return
	 */
	private byte[] compressMsg(final String data) {
		byte[] temp = null;
		byte[] wireData = null;
		
		temp = data.getBytes();
		log.info("Before Compression Msg byte length : "+temp.length);

		long compStartTime = System.currentTimeMillis();
		wireData = TextCompressor.compress(temp);
		long compEndTime = System.currentTimeMillis();

		log.info("Compression Took "+(compEndTime - compStartTime) + " milli seconds");

		//After Compression Byte Length is
		log.info("After Compression Msg byte length : " + wireData.length);
		return wireData;
	}

	/**
	 * Encrypts the String msg (testing with local testKey)
	 * @param - data
	 * @return
	 */
	private byte[] encryptMsg(String sphereId, byte[] msgData) {

		byte[] msg = null;
		
		log.info("Before Encryption Msg byte length : " + msgData.length);
		long startTime = System.nanoTime();

		//Encrypt the data.. To test the local encryption
		//msg = cipherService.encrypt(msgData, testKey).getBytes();
		// temp fix of sending the byte stream
		String msgDataString = new String(msgData);
		msg = UhuCompManager.getSphereForSadl().encryptSphereContent(sphereId, msgDataString);

		long endTime = System.nanoTime();
		log.info("Encryption Took "+(endTime - startTime) + " nano seconds"); 

		//After Encryption Byte Length
		if(msg != null) {
			log.info("After Encryption Msg byte length : " + msg.length );
		}

		return msg;
	}

	/**
	 * Encrypts the String msg
	 * if not enabled, puts the incomming message to outgoing
	 * return null means, encryption failed
	 * @param - data
	 * @return
	 */
	private byte[] decryptMsg(String sphereId, WireMessage.WireMsgStatus msgStatus, byte[] msgData) {

		byte [] msg = null;

		if((msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
				|| (msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED)){

			//message = cipherService.decrypt(wireMessage.getMsg(), testKey).getBytes();
			String data = UhuCompManager.getSphereForSadl().decryptSphereContent(sphereId, msgData);

			if(data != null){
				msg  = data.getBytes();
				//log.info("decrypted size >> " + message.length);
			}
			else
			{
				log.info("unable to decrypt msg for sphere id >> " + sphereId);

			}
		}else // encryption not enabled . send back same data
		{
			msg = msgData;
		}
		return msg;
	}


	/**
	 * Send the event message
	 * */
	public boolean sendEventMessage(EventLedger ledger)
	{
		boolean ret = false;
		String data = ledger.getSerializedMessage();

		if(data != null){
			byte[] wireByteMessage = null;

			if(ledger.getIsMulticast()) {

				//TODO: for event message decrypt the header here
				// if the intended service is available in sadl message is decrypted
				WireMessage wireMessage = prepareWireMessgae(ledger.getHeader().getSphereName(),data);

				// encrypt the header

				byte[] headerData = encryptMsg(wireMessage.getSphereId(), ledger.getSerializedHeader().getBytes());

				wireMessage.setHeaderMsg(headerData);

				wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_EVENT);


				try {
					wireByteMessage = wireMessage.serialize();
				} catch (IOException e) {
					log.error("unable to serialize the wire msg "+e);
					return ret;
				}

				ret = sendToAll(wireByteMessage,false);

				// also send it locally
				processWireMessage(getDeviceId(),ledger);

			}
			else {

				UnicastHeader uHeader = (UnicastHeader) ledger.getHeader();
				String recipient = uHeader.getRecipient().device;

				//FIXME: since current zyre-jni doesn't support the self device identification
				// we are sending the unicast always loop back
				/*if(isLocalMessage(recipient)) {
					// if it is unicast and targeted to same device. sent it only to local
					return processWireMessage(recipient,ledger);
				}
				else*/
				{

					//TODO: for event message decrypt the header here
					// if the intended service is available in sadl message is decrypted
					WireMessage wireMessage = prepareWireMessgae(ledger.getHeader().getSphereName(),data);

					// encrypt the header

					byte[] headerData = encryptMsg(wireMessage.getSphereId(), ledger.getSerializedHeader().getBytes());

					wireMessage.setHeaderMsg(headerData);

					wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_EVENT);

					try {
						wireByteMessage = wireMessage.serialize();
					} catch (IOException e) {
						log.error("unable to serialize the wire msg "+e);
						return false;
					}



					if(null == uHeader || uHeader.getRecipient() == null
							|| uHeader.getRecipient().device == null || uHeader.getRecipient().device.length() == 0) {
						log.error(" Message not of accepted type");
						return ret;
					}

					ret = sendToOne(wireByteMessage, recipient, false);
					// FIXME : since we don't know the zyre-jni device id. we are sending now.
					processWireMessage(recipient,ledger);
				}
			}
		}
		return ret;
	}

	/** send the stream data */
	public boolean sendStreamMessage(Ledger message)
	{
		if (uhuStreamManager !=null) {

			uhuStreamManager.sendStreamMessage(message);
			return true;
		}else{

			log.error("UhuStreamManager is not initialized.");
			return false;
		}

	}

	/** send the raw message to comms */
	public boolean sendMessageLedger(MessageLedger message)
	{
		WireMessage wireMessage = new WireMessage();
		// configure raw msg event
		wireMessage.setMsgType(WireMessage.WireMsgType.MSG_EVENT);

		wireMessage.setMsg(message.getMsg().getBytes());

		wireMessage.setSphereId("COMMS_DIAG");

		wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_RAW);

		byte[] data  = null;

		try {
			data  = wireMessage.serialize();
		} catch (IOException e) {
			log.error("Ledger wire message serialization error " + e);
			return false;
		}

		if(message.isMulticast())
		{
			sendToAll(data,false);
		}
		else{
			sendToOne(data,message.getRecipient().device,false);
		}

		return true;
	}

	/** send to all : Multicast message . How to send is up to the specific comms manager */
	public abstract boolean sendToAll(byte[] msg, boolean isEvent);

	/** send to one : Unicast message . How to send is up to the specific comms manager
	 * nodeId = device id
	 * */
	public abstract boolean sendToOne(byte[] msg, String nodeId, boolean isEvent);

	/** handle the wire message */
	public boolean processWireMessage(String deviceId, String msg)
	{
		// start thread pool
		if((executor != null) && !executor.isShutdown()){

			ProcessIncomingMessage inMsg = new ProcessIncomingMessage(/*this, */deviceId, msg);

			executor.execute(inMsg);
		}
		else
		{
			log.error("thread pool is not active.");
		}

		return true;
	}

	/** handle the wire message - loop back */
	public boolean processWireMessage(String deviceId, Ledger ledger)
	{
		// start thread pool
		if((executor != null) && !executor.isShutdown()){

			ProcessIncomingMessage inMsg = new ProcessIncomingMessage(/*this, */deviceId, ledger);

			executor.execute(inMsg);
		}
		else
		{
			log.error("thread pool is not active.");
		}

		return true;
	}

	/** process the incoming message via thread pool for better throughput */
	class ProcessIncomingMessage implements Runnable{

		/*CommsProcessor commsProcessor;*/

		String deviceId;

		String msg = null;

		Ledger ledger = null;

		public ProcessIncomingMessage(/*CommsProcessor commsProcessor, */String deviceId, String msg)
		{
			/*this.commsProcessor = commsProcessor;*/
			this.deviceId = deviceId;
			this.msg = msg;
		}

		/** processing loop back */
		public ProcessIncomingMessage(/*CommsProcessor commsProcessor, */String deviceId, Ledger ledger)
		{
			/*this.commsProcessor = commsProcessor;*/
			this.deviceId = deviceId;
			this.ledger = ledger;
		}

		@Override
		public void run() {

			if(ledger != null) {
				// ledger is not null. means this is not loop back
				// dispatch it directly
				dispatchMessage(ledger);
				return;
			}

			if(!WireMessage.checkVersion(msg))
			{
				String mismatchedVersion = WireMessage.getVersion(msg);
				/*log.error("Unknown message received. Uhu version > "+ UhuVersion.getWireVersion() +
						" . Incoming msg version > " + mismatchedVersion);*/
				notification.versionMismatch(mismatchedVersion);
				return;
			}

			WireMessage	wireMessage = WireMessage.deserialize(msg.getBytes());

			if (wireMessage == null) {
				log.error(" deserialization failed >> " + msg);
				return;
			}


			switch(wireMessage.getMsgType())
			{
			case MSG_MULTICAST_CTRL:

				/*commsProcessor.*/processCtrl(deviceId, wireMessage);
				break;
			case MSG_UNICAST_CTRL:
				processCtrl(deviceId, wireMessage);
				break;
			case MSG_MULTICAST_EVENT:
				processEvent(deviceId, wireMessage);
				break;
			case MSG_UNICAST_EVENT:
				processEvent(deviceId, wireMessage);
				break;
			case MSG_EVENT: //handling diag event
				processMessageEvent(deviceId,wireMessage) ;
				break;
			default:
				log.error(" Unknown event >> "+msg);
				return ;
			}

		}
	}

	private boolean processCtrl(String deviceId, WireMessage wireMessage){

		// fixme: check the version
		byte[] msg = parseCtrlMessage(wireMessage);

		if(msg != null) {
			String processedMsg = new String(msg);
			//log.info("Ctrl Msg size "+data.length());
			ControlMessage ctrl = ControlMessage.deserialize(processedMsg, ControlMessage.class);

			// Quickfix for zyre-jni: update the sender device id
			ctrl.getSender().device = deviceId;
			//processedMsg = ctrl.serialize();
			// instead of deserialization, you shall try to use
			// pattern match to speed up the discriminator
			//log.info("ctrl msg >> " + ctrl.toString());
			msgDispatcher.dispatchControlMessages(ctrl, processedMsg);

			return true;
		}

		return false;
	}

	//send the message to intended modules
	boolean dispatchMessage(Ledger ledger)
	{
		if(ledger instanceof ControlLedger)
		{
			ControlLedger ctrlLedger = (ControlLedger)ledger;
			msgDispatcher.dispatchControlMessages(ctrlLedger.getMessage(), ctrlLedger.getSerializedMessage());
		}
		else if(ledger instanceof EventLedger)
		{
			EventLedger eventLedger = (EventLedger)ledger;
			msgDispatcher.dispatchServiceMessages(eventLedger);
		}
		else{
			log.error("unknown msg to dispatch ");
		}

		return false;
	}
	/**
	 * Process wiremessage which will decompress and decrypt based on the wire message. 
	 * @param wireMessage
	 * @param - message
	 * @return
	 */
	private byte[] parseCtrlMessage(WireMessage wireMessage) {

		byte[] message = null;
		//process wiremessage to decrypt

		/**
		 * ##########
		 * Step 1 : Decryption :  decrypt the message.
		 * ##########
		 */
		message = decryptMsg(wireMessage.getSphereId(),wireMessage.getWireMsgStatus(),wireMessage.getMsg());

		if(message == null)
		{
			// encrption failed return null
			return message;
		}
		/**
		 * ##########
		 * Step 2 : De-compress the message :  de-compress the message.
		 * ##########
		 */
		if((wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
				|| (wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_COMPRESSED)){
				//mean the data is decrypted and not to decompress
				byte[] temp = message;
				String processedMsg = TextCompressor.decompress(temp);

				if((processedMsg != null) && !processedMsg.isEmpty())
					message = processedMsg.getBytes();

		}



		return message;
	}

	//public abstract String getDeviceId();

	//enable the above code later. Quickfix network device id is taken as local ip as of now
	// for zyre this needs to return from actual comms
	public String getDeviceId(){
		return UhuNetworkUtilities.getDeviceIp();
	}

	//public abstract String isLocalMessage();

	//enable the above code later. Quickfix network device id is taken as local ip as of now
	// for zyre this needs to return from actual comms
	public boolean isLocalMessage(String deviceId){

		return deviceId.equals(UhuNetworkUtilities.getDeviceIp()) ;
	}
	private boolean processMessageEvent(String deviceId, WireMessage wireMessage) {

		MessageLedger msgLedger = new MessageLedger();



		// fixme: check the version

		// device Id
		UhuServiceEndPoint endPoint = new UhuServiceEndPoint(deviceId,null);
		msgLedger.setSender(endPoint);

		msgLedger.setMsg(new String(wireMessage.getMsg()));

		// for diag the message is not compressed
		notification.diagMsg(msgLedger);

		return true;


	}

	private boolean processEvent(String deviceId, WireMessage wireMessage){

		EventLedger eventLedger = new EventLedger();
		//eventLedger

		if(!isLocalMessage(deviceId))
			eventLedger.setIsLocal(false);

		// fixme: check the version
		// setting sphere id instead of name
		eventLedger.getHeader().setSphereName(wireMessage.getSphereId());

		if(setEventHeader(eventLedger, wireMessage)) {

			// override sender service end point device id with local id
			eventLedger.getHeader().getSenderSEP().device = deviceId;

			eventLedger.setEncryptedMessage(wireMessage.getMsg());

			// sadl encrypts the data
			// FIXME: in case of compressed message sadl has to decompress
			// at the moment sadl is generic for udp and other comms, hence make changes there

			msgDispatcher.dispatchServiceMessages(eventLedger);

			return true;
		}


		return false;

	}

	private boolean setEventHeader(EventLedger eLedger, WireMessage wireMessage){

		byte data[] = null;

		// decrypt the header
		data = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(), wireMessage.getHeaderMsg());

		if(data == null) // header decrpt failed. unknown sphere id
			return false;

		String header = new String (data);

		if(wireMessage.isMulticast()){

			MulticastHeader mHeader = new Gson().fromJson(header, MulticastHeader.class);

			eLedger.setHeader(mHeader);

			eLedger.setIsMulticast(true);

		}else{

			UnicastHeader uHeader = new Gson().fromJson(header, UnicastHeader.class);

			eLedger.setHeader(uHeader);

			eLedger.setIsMulticast(false);

		}
		return true;
	}

	@Override
	public boolean sendStream(String uniqueKey) {
		if (uhuStreamManager != null) {
			log.info("sending stream >"+uniqueKey);
			return uhuStreamManager.sendStream(uniqueKey);
		} else {

			log.error("UhuStreamManager is not initialized.");
			return false;
		}
	}

	@Override
	public boolean registerStreamBook(String key, StreamRecord sRecord) {

		if (uhuStreamManager != null) {

			return uhuStreamManager.registerStreamBook(key, sRecord);

		}else{

			log.error("UhuStreamManager is not initialized.");
			return false;
		}
	}

	@Override
	public boolean registerNotification(ICommsNotification notification) {

		//If notification is null.. register the notification object
		if(this.notification == null){
			this.notification = notification;
			return true;
		}
		return false;

	}

	/**
	 * Bridges the request locally to dispatcher or StreamingQueue
	 *
	 */
	private boolean bridgeControlMessage(String deviceId, final ControlLedger tcMessage){

		if(ControlMessage.Discriminator.StreamRequest == tcMessage.getMessage().getDiscriminator()){
			return sendStream(tcMessage.getMessage().getUniqueKey());
		}else{
			return processWireMessage(deviceId,tcMessage);
		}
	}

	@Override
	 public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, ICtrlMsgReceiver receiver) {

		return msgDispatcher.registerControlMessageReceiver(id,receiver);

	}

	@Override
	public void setSphereForSadl(IUhuSphereForSadl uhuSphere) {
		this.sphereForSadl = uhuSphere;
		uhuStreamManager.setSphereForSadl(sphereForSadl);
	}

	class CommCtrlReceiver implements ICtrlMsgReceiver{
		@Override
		// FIXME : remove the below Log related quickfix, by moving the implementation to respective module
		public boolean processControlMessage(ControlMessage.Discriminator id, String serializedMsg) {
			switch (id)
			{
				case LoggingServiceMessage:
					//log.debug("<<<<<<<<  LOGGING MESSAGE RECEIVED FROM LOGGING SERVICE  >>>>>>>>>");
					log.debug("ReceivedLogMessage-> " + serializedMsg);
					try{
						final LoggingServiceMessage loggingServiceMsg = ControlMessage.deserialize(serializedMsg, LoggingServiceMessage.class);

						if(null == logServiceMsgHandler){
							logServiceMsgHandler = new LogServiceMessageHandler();
						}
						logServiceMsgHandler.handleLogServiceMessage(loggingServiceMsg);
					}catch(Exception e){
						log.error("Error in Deserializing LogServiceMessage", e);
					}
					break;
				default:
					log.error("Unknown control message > "+id);
					return false;
			}
			return true;
		}
	}
}
