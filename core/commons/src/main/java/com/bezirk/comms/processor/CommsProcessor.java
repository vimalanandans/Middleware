package com.bezirk.comms.processor;



import com.bezirk.comms.Comms;
import com.bezirk.comms.CommsMessageDispatcher;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.CommsProperties;
import com.bezirk.comms.CtrlMsgReceiver;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.streaming.Streaming;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.Ledger;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.comms.CommsFeature;


import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
//import com.bezirk.sphere.security.UPABlockCipherService;
import com.bezirk.streaming.control.Objects.StreamRecord;
import com.bezirk.util.TextCompressor;
import com.bezrik.network.NetworkUtilities;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public abstract class CommsProcessor implements Comms {
    private static final Logger logger = LoggerFactory.getLogger(CommsProcessor.class);

    // thread pool size
    private static final int THREAD_SIZE = 4;
  //  private final UPABlockCipherService cipherService = new UPABlockCipherService();

    CommsMessageDispatcher msgDispatcher = null;

    //LogServiceMessageHandler logServiceMsgHandler = null;
    PubSubBroker pubSubBroker = null;

    SphereSecurity sphereSecurity = null;

    //generic notifications
    List ICommsNotification = new ArrayList<CommsNotification>();
    /**
     * Version Callback that will be used to inform the platforms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     */
    private CommsNotification notification = null;

    //private final byte[] testKey = {'B','E','Z','I','R','K','_','G','R','O','U','P','N','E','W','1'};
    private ExecutorService executor;
    private Streaming bezirkStreamManager = null;

    @Override
    public boolean initComms(CommsProperties commsProperties, InetAddress addr,
                             PubSubBroker pubSubBroker, SphereSecurity sphereSecurity, Streaming streaming) {

        this.pubSubBroker = pubSubBroker;

        msgDispatcher = new CommsMessageDispatcher(pubSubBroker);

        if (streaming != null) {

            bezirkStreamManager = streaming;

            //bezirkStreamManager.initStreams(this);

        }

        return true;
    }

    @Override
    public boolean startComms() {

        // create thread pool. every start creates new thread pool.
        // old ones are cleared with stopComms
        executor = Executors.newFixedThreadPool(THREAD_SIZE);

        if (bezirkStreamManager != null) {
            bezirkStreamManager.startStreams();
        }



        return true;
    }

    @Override
    public boolean stopComms() {

        if (executor != null) {
            executor.shutdown();
            // will shutdown eventually
            // if any problem persists wait for some time (less then 200ms) using awaitTermination
            // and then shutdownNow
        }


        if (bezirkStreamManager != null) {
            bezirkStreamManager.endStreams();
        }


        return true;
    }

    @Override
    public boolean closeComms() {
        /* in stop we end streams already, so skip the below
        if (bezirkStreamManager != null) {
            bezirkStreamManager.endStreams();
        } */

        return true;
    }


    @Override
    public boolean sendMessage(Ledger message) {
        // send as it is
        if (message instanceof ControlLedger) {
            return this.sendControlMessage((ControlLedger) message);
        }else if (message instanceof EventLedger) {
            return this.sendEventMessage((EventLedger) message);
        }else if (message instanceof MessageLedger) {
            return sendMessageLedger((MessageLedger) message);
        }else { // stream ledger // hopefully there are no other types
            //return this.sendStreamMessage(message);
            return false;
        }
        // FIXME: Bridge the local message. look udp sendControlMessage


    }

    /**
     * Send the control message
     */
    public boolean sendControlMessage(ControlLedger message) {
        boolean ret = false;
        String data = message.getSerializedMessage();
        if (data != null) {
            if (message.getMessage() instanceof MulticastControlMessage) {

                WireMessage wireMessage = prepareWireMessage(message.getMessage().getSphereId(), data);

                wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_CTRL);
                byte[] wireByteMessage = wireMessage.serialize();
                ret = sendToAll(wireByteMessage, false);

                // bridge local
                bridgeControlMessage(getDeviceId(), message);

            } else if (message.getMessage() instanceof UnicastControlMessage) {
               /* UnicastControlMessage uMsg = (UnicastControlMessage) message.getMessage();

				 String recipient = uMsg.getRecipient().device;

				if(isLocalMessage(recipient))
                {
					return bridgeControlMessage(getDeviceId(),message);
				}
				else */
                if (ControlMessage.Discriminator.StreamRequest == message.getMessage().getDiscriminator()) {
                    return sendStream(message.getMessage().getUniqueKey());
                }


                WireMessage wireMessage = prepareWireMessage(message.getMessage().getSphereId(), data);

                wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_CTRL);

                byte[] wireByteMessage = wireMessage.serialize();
                ret = sendToAll(wireByteMessage, false);


            } else {
                logger.debug("unknown control message");
            }
        }


        return ret;
    }

    /**
     * prepares the WireMessage and returns it based on encryption and compression settings
     *
     * @param data
     * @return
     */
    private WireMessage prepareWireMessage(String sphereId, String data) {
        WireMessage wireMessage = new WireMessage();
        wireMessage.setSphereId(sphereId);
        byte[] wireData = null;

        /**
         * ##########
         * Step 1 :Compression :  Do the compression if message compression is enabled
         * ##########
         */
        if (CommsFeature.WIRE_MSG_COMPRESSION.isActive()) {
            wireData = compressMsg(data);
            wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_COMPRESSED);
        }

        /**
         * ##########
         * Step 2 :Encryption :  perform encryption if it is enabled.
         * ##########
         */
        if (CommsFeature.WIRE_MSG_ENCRYPTION.isActive()) {

            if (wireData != null) {
                //means compression has happened, now encrypt the content
                wireData = encryptMsg(wireMessage.getSphereId(), wireData);
                wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED);
            } else {
                //means compression was not enabled, now encrypt the msg content only
                wireData = encryptMsg(sphereId, data.getBytes());
                wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED);
            }

        }

        /**
         * ##########
         * Step 3 : Check :  If the Compression and Encryption is disabled. set the Raw message
         * ##########
         */
        //set data to wire message
        if (wireData == null) {
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
     *
     * @param data
     * @return
     */
    private byte[] compressMsg(final String data) {
        final byte[] temp = data.getBytes();
        logger.info("Before Compression Msg byte length: {}", temp.length);

        final long compStartTime = System.currentTimeMillis();
        final byte[] wireData = TextCompressor.compress(temp);
        final long compEndTime = System.currentTimeMillis();

        logger.info("Compression Took {} milliseconds", compEndTime - compStartTime);

        //After Compression Byte Length is
        logger.info("After Compression Msg byte length: {}", wireData.length);
        return wireData;
    }

    /**
     * Encrypts the String msg (testing with local testKey)
     *
     * @param - data
     * @return
     */
    private byte[] encryptMsg(String sphereId, byte[] msgData) {
        logger.info("Before Encryption Msg byte length : " + msgData.length);
        long startTime = System.nanoTime();

        //Encrypt the data.. To test the local encryption
        //msg = cipherService.encrypt(msgData, testKey).getBytes();
        // temp fix of sending the byte stream
        String msgDataString = new String(msgData);
        byte[] msg ;

        if(sphereSecurity != null)
            msg = sphereSecurity.encryptSphereContent(sphereId, msgDataString);
        else // No encryption when there is no interface
            msg = msgData;

        long endTime = System.nanoTime();
        logger.info("Encryption Took " + (endTime - startTime) + " nano seconds");

        //After Encryption Byte Length
        if (msg != null) {
            logger.info("After Encryption Msg byte length : " + msg.length);
        }

        return msg;
    }

    /**
     * Encrypts the String msg
     * if not enabled, puts the incoming message to outgoing
     * return null means, encryption failed
     *
     * @param - data
     * @return
     */
    private byte[] decryptMsg(String sphereId, WireMessage.WireMsgStatus msgStatus, byte[] msgData) {

        byte[] msg = null;

        if ((msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
                || (msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED)) {

            String data ;

            if(sphereSecurity != null)
                data = sphereSecurity.decryptSphereContent(sphereId, msgData);
            else // No decryption when there is no interface
                data = new String(msgData);

            if (data != null) {
                msg = data.getBytes();
                //logger.info("decrypted size >> " + message.length);
            } else {
                logger.info("unable to decrypt msg for sphere id >> " + sphereId);

            }
        } else // encryption not enabled . send back same data
        {
            msg = msgData;
        }
        return msg;
    }


    /**
     * Send the event message
     */
    public boolean sendEventMessage(EventLedger ledger) {
        boolean ret = false;
        String data = ledger.getSerializedMessage();

        if (data != null) {
            if (ledger.getIsMulticast()) {

                //TODO: for event message decrypt the header here
                // if the intended zirk is available in sadl message is decrypted
                WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereName(), data);

                // encrypt the header

                byte[] headerData = encryptMsg(wireMessage.getSphereId(), ledger.getSerializedHeader().getBytes());

                wireMessage.setHeaderMsg(headerData);

                wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_EVENT);

                byte[] wireByteMessage = wireMessage.serialize();
                ret = sendToAll(wireByteMessage, false);


                // also send it locally
                processWireMessage(getDeviceId(), ledger);

            } else {

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
                    // if the intended zirk is available in sadl message is decrypted
                    WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereName(), data);

                    // encrypt the header

                    byte[] headerData = encryptMsg(wireMessage.getSphereId(), ledger.getSerializedHeader().getBytes());

                    wireMessage.setHeaderMsg(headerData);

                    wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_EVENT);


                    if (null == uHeader || uHeader.getRecipient() == null
                            || uHeader.getRecipient().device == null || uHeader.getRecipient().device.length() == 0) {
                        logger.error(" Message not of accepted type");
                        return ret;
                    }

                    byte[] wireByteMessage = wireMessage.serialize();
                    ret = sendToOne(wireByteMessage, recipient, false);

                    // FIXME : since we don't know the zyre-jni device id. we are sending now.
                    processWireMessage(recipient, ledger);
                }
            }
        }
        return ret;
    }

    /**
     * send the stream data
     */
    /*public boolean sendStreamMessage(Ledger message) {
        if (bezirkStreamManager != null) {

            bezirkStreamManager.sendStreamMessage(message);
            return true;
        } else {

            logger.error("BezirkStreamManager is not initialized.");
            return false;
        }

    }*/

    /**
     * send the raw message to comms
     */
    public boolean sendMessageLedger(MessageLedger message) {
        WireMessage wireMessage = new WireMessage();
        // configure raw msg event
        wireMessage.setMsgType(WireMessage.WireMsgType.MSG_EVENT);

        wireMessage.setMsg(message.getMsg().getBytes());

        wireMessage.setSphereId("COMMS_DIAG");

        wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_RAW);

        byte[] data = wireMessage.serialize();

        if (message.isMulticast()) {
            sendToAll(data, false);
        } else {
            sendToOne(data, message.getRecipient().device, false);
        }

        return true;
    }

    /**
     * send to all : Multicast message . How to send is up to the specific comms manager
     */
    public abstract boolean sendToAll(byte[] msg, boolean isEvent);

    /**
     * send to one : Unicast message . How to send is up to the specific comms manager
     * nodeId = device id
     */
    public abstract boolean sendToOne(byte[] msg, String nodeId, boolean isEvent);

    /**
     * handle the wire message
     */
    public boolean processWireMessage(String deviceId, String msg) {
        // start thread pool
        if ((executor != null) && !executor.isShutdown()) {

            ProcessIncomingMessage inMsg = new ProcessIncomingMessage(/*this, */deviceId, msg);

            executor.execute(inMsg);
        } else {
            logger.error("thread pool is not active.");
        }

        return true;
    }

    /**
     * handle the wire message - loop back
     */
    public boolean processWireMessage(String deviceId, Ledger ledger) {
        // start thread pool
        if ((executor != null) && !executor.isShutdown()) {

            ProcessIncomingMessage inMsg = new ProcessIncomingMessage(/*this, */deviceId, ledger);

            executor.execute(inMsg);
        } else {
            logger.error("thread pool is not active.");
        }

        return true;
    }

    private boolean processCtrl(String deviceId, WireMessage wireMessage) {

        // fixme: check the version
        byte[] msg = parseCtrlMessage(wireMessage);

        if (msg != null) {
            String processedMsg = new String(msg);
            //logger.info("Ctrl Msg size "+data.length());
            ControlMessage ctrl = ControlMessage.deserialize(processedMsg, ControlMessage.class);

            // Quickfix for zyre-jni: update the sender device id
            ctrl.getSender().device = deviceId;
            //processedMsg = ctrl.toJson();
            // instead of deserialization, you shall try to use
            // pattern match to speed up the discriminator
            //logger.info("ctrl msg >> " + ctrl.toString());
            msgDispatcher.dispatchControlMessages(ctrl, processedMsg);

            return true;
        }

        return false;
    }

    //send the message to intended modules
    boolean dispatchMessage(Ledger ledger) {
        if (ledger instanceof ControlLedger) {
            ControlLedger ctrlLedger = (ControlLedger) ledger;
            msgDispatcher.dispatchControlMessages(ctrlLedger.getMessage(), ctrlLedger.getSerializedMessage());
        } else if (ledger instanceof EventLedger) {
            EventLedger eventLedger = (EventLedger) ledger;
            msgDispatcher.dispatchServiceMessages(eventLedger);
        } else {
            logger.error("unknown msg to dispatch ");
        }

        return false;
    }

    /**
     * Process wiremessage which will decompress and decrypt based on the wire message.
     *
     * @param wireMessage
     * @param -           message
     * @return
     */
    private byte[] parseCtrlMessage(WireMessage wireMessage) {
        //process wiremessage to decrypt

        /**
         * ##########
         * Step 1 : Decryption :  decrypt the message.
         * ##########
         */
        byte[] message = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(), wireMessage.getMsg());

        if (message == null) {
            // encryption failed return null
            return message;
        }
        /**
         * ##########
         * Step 2 : De-compress the message :  de-compress the message.
         * ##########
         */
        if ((wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
                || (wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_COMPRESSED)) {
            //mean the data is decrypted and not to decompress
            byte[] temp = message;
            String processedMsg = TextCompressor.decompress(temp);

            if ((processedMsg != null) && !processedMsg.isEmpty())
                message = processedMsg.getBytes();

        }


        return message;
    }

    //enable the above code later. Quickfix network device id is taken as local ip as of now
    // for zyre this needs to return from actual comms
    public String getDeviceId() {
        return NetworkUtilities.getDeviceIp();
    }

    //public abstract String getDeviceId();

    //enable the above code later. Quickfix network device id is taken as local ip as of now
    // for zyre this needs to return from actual comms
    public boolean isLocalMessage(String deviceId) {

        return deviceId.equals(NetworkUtilities.getDeviceIp());
    }

    //public abstract String isLocalMessage();

    private boolean processMessageEvent(String deviceId, WireMessage wireMessage) {

        MessageLedger msgLedger = new MessageLedger();


        // fixme: check the version

        // device Id
        BezirkZirkEndPoint endPoint = new BezirkZirkEndPoint(deviceId, null);
        msgLedger.setSender(endPoint);

        msgLedger.setMsg(new String(wireMessage.getMsg()));

        // for diag the message is not compressed
        notification.diagMsg(msgLedger);

        return true;


    }

    private boolean processEvent(String deviceId, WireMessage wireMessage) {

        EventLedger eventLedger = new EventLedger();
        //eventLedger

        if (!isLocalMessage(deviceId))
            eventLedger.setIsLocal(false);

        // fixme: check the version
        // setting sphere id instead of name
        eventLedger.getHeader().setSphereName(wireMessage.getSphereId());

        if (setEventHeader(eventLedger, wireMessage)) {

            // override sender zirk end point device id with local id
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

    private boolean setEventHeader(EventLedger eLedger, WireMessage wireMessage) {
        // decrypt the header
        byte[] data = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(), wireMessage.getHeaderMsg());

        if (data == null) // header decrypt failed. unknown sphere id
            return false;

        String header = new String(data);

        if (wireMessage.isMulticast()) {

            MulticastHeader mHeader = new Gson().fromJson(header, MulticastHeader.class);

            eLedger.setHeader(mHeader);

            eLedger.setIsMulticast(true);

        } else {

            UnicastHeader uHeader = new Gson().fromJson(header, UnicastHeader.class);

            eLedger.setHeader(uHeader);

            eLedger.setIsMulticast(false);

        }
        return true;
    }

    @Override
    public boolean sendStream(String uniqueKey) {
        if (bezirkStreamManager != null) {
            logger.info("sending stream >" + uniqueKey);
            return bezirkStreamManager.sendStream(uniqueKey);
        } else {

            logger.error("BezirkStreamManager is not initialized.");
            return false;
        }
    }

    @Override
    public boolean registerStreamBook(String key, StreamRecord sRecord) {

        if (bezirkStreamManager != null) {

            return bezirkStreamManager.addStreamRecordToStreamStore(key, sRecord);

        } else {

            logger.error("BezirkStreamManager is not initialized.");
            return false;
        }
    }

    @Override
    public boolean registerNotification(CommsNotification notification) {

        //If notification is null.. register the notification object
        if (this.notification == null) {
            this.notification = notification;
            return true;
        }
        return false;

    }

    /**
     * Bridges the request locally to dispatcher or StreamingQueue
     */
    private boolean bridgeControlMessage(String deviceId, final ControlLedger tcMessage) {

        if (ControlMessage.Discriminator.StreamRequest == tcMessage.getMessage().getDiscriminator()) {
            return sendStream(tcMessage.getMessage().getUniqueKey());
        } else {
            return processWireMessage(deviceId, tcMessage);
        }
    }

    @Override
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver) {

        return msgDispatcher.registerControlMessageReceiver(id, receiver);

    }

    @Override
    public void setSphereSecurity(SphereSecurity sphereSecurity) {
        bezirkStreamManager.setSphereSecurityForEncryption(sphereSecurity);
    }

    /**
     * process the incoming message via thread pool for better throughput
     */
    class ProcessIncomingMessage implements Runnable {

		/*CommsProcessor commsProcessor;*/

        String deviceId;

        String msg = null;

        Ledger ledger = null;

        public ProcessIncomingMessage(/*CommsProcessor commsProcessor, */String deviceId, String msg) {
            /*this.commsProcessor = commsProcessor;*/
            this.deviceId = deviceId;
            this.msg = msg;
        }

        /**
         * processing loop back
         */
        public ProcessIncomingMessage(/*CommsProcessor commsProcessor, */String deviceId, Ledger ledger) {
            /*this.commsProcessor = commsProcessor;*/
            this.deviceId = deviceId;
            this.ledger = ledger;
        }

        @Override
        public void run() {

            if (ledger != null) {
                // ledger is not null. means this is not loop back
                // dispatch it directly
                dispatchMessage(ledger);
                return;
            }

            if (!WireMessage.checkVersion(msg)) {
                String mismatchedVersion = WireMessage.getVersion(msg);
                /*logger.error("Unknown message received. Bezirk version > "+ BezirkVersion.getWireVersion() +
                        " . Incoming msg version > " + mismatchedVersion);*/
                notification.versionMismatch(mismatchedVersion);
                return;
            }

            WireMessage wireMessage = WireMessage.deserialize(msg.getBytes());

            if (wireMessage == null) {
                logger.error(" deserialization failed >> " + msg);
                return;
            }


            switch (wireMessage.getMsgType()) {
                case MSG_MULTICAST_CTRL:

				/*commsProcessor.*/
                    processCtrl(deviceId, wireMessage);
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
                    processMessageEvent(deviceId, wireMessage);
                    break;
                default:
                    logger.error(" Unknown event >> " + msg);
            }
        }
    }

}
