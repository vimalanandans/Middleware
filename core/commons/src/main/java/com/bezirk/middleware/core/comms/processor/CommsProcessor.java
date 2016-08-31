package com.bezirk.middleware.core.comms.processor;


import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.comms.CommsFeature;
import com.bezirk.middleware.core.comms.CommsMessageDispatcher;
import com.bezirk.middleware.core.comms.CommsProperties;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.Ledger;
import com.bezirk.middleware.core.control.messages.MessageLedger;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastHeader;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.core.util.TextCompressor;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import com.bezirk.sphere.security.UPABlockCipherService;

/**
 * This handles generic comms processing
 * new comms implementations shall use this as base class
 */

public abstract class CommsProcessor implements com.bezirk.middleware.core.comms.Comms, Observer {

    private static final Logger logger = LoggerFactory.getLogger(CommsProcessor.class);

    // thread pool size
    private static final int THREAD_SIZE = 4;
    //  private final UPABlockCipherService cipherService = new UPABlockCipherService();

    com.bezirk.middleware.core.comms.CommsMessageDispatcher msgDispatcher = null;

    //LogServiceMessageHandler logServiceMsgHandler = null;


    SphereSecurity sphereSecurity = null; // nullable object

    //generic notifications
    List ICommsNotification = new ArrayList<>();
    /**
     * Version Callback that will be used to inform the platforms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     */
    private com.bezirk.middleware.core.comms.CommsNotification notification = null;

    //private final byte[] testKey = {'B','E','Z','I','R','K','_','G','R','O','U','P','N','E','W','1'};
    private ExecutorService executor;
    private Streaming bezirkStreamManager = null;
    private final NetworkManager networkManager;

    public CommsProcessor(NetworkManager networkManager, com.bezirk.middleware.core.comms.CommsNotification commsNotification, Streaming streaming) {
        this.notification = commsNotification;
        this.networkManager = networkManager;
        this.msgDispatcher = new com.bezirk.middleware.core.comms.CommsMessageDispatcher();
        if (streaming != null) {
            bezirkStreamManager = streaming;
            //bezirkStreamManager.initStreams(this);
        }
    }

    /**
     * Create thread pool.
     */
    public void startComms() {
        executor = Executors.newFixedThreadPool(THREAD_SIZE);
        /*
        not required anymore!!

        if (bezirkStreamManager != null) {
            logger.debug("bezirkStreamManager not null");
            bezirkStreamManager.startStreams();
        } else {
            logger.debug("bezirkStreamManager is null");
        }
        return true;
        }*/
    }


    /**
     * Shutdown thread pool.
     */
    public void stopComms() {
        if (executor != null) {
            executor.shutdown();
        }
        if (bezirkStreamManager != null) {
            bezirkStreamManager.endStreams();
        }

    }

    @Override
    public boolean sendMessage(Ledger message) {
        // send as it is
        if (message instanceof ControlLedger)
            return this.sendControlLedger((ControlLedger) message);
        else if (message instanceof EventLedger)
            return this.sendEventLedger((EventLedger) message);
        else if (message instanceof MessageLedger)
            return this.sendMessageLedger((MessageLedger) message);
        else { // stream ledger // hopefully there are no other types
            //return this.sendStreamMessage(message);
            return false;
        }

    }

    @Override
    public boolean sendControlMessage(ControlMessage message)
    {
        ControlLedger ledger = new ControlLedger();
        ledger.setMessage(message);
        ledger.setSphereId(message.getSphereId());
        ledger.setSerializedMessage(ledger.getMessage().serialize());

        return sendControlLedger(ledger);
    }

    /**
     * Send the control message
     */
    @Override
    public boolean sendControlLedger(ControlLedger ledger) {
        boolean ret = false;

        String data = ledger.getSerializedMessage();
        if (data != null) {
            if (ledger.getMessage() instanceof MulticastControlMessage) {
                WireMessage wireMessage = prepareWireMessage(ledger.getMessage().getSphereId(), data);


                wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_CTRL);
                byte[] wireByteMessage = wireMessage.serialize();
                ret = sendToAll(wireByteMessage, false);

                // bridge local. // NOT NEEDED anymore . if needed for streaming local,
                //  pubsubbroker has to do.
                //bridgeControlMessage(getDeviceId(), message);

            } else if (ledger.getMessage() instanceof UnicastControlMessage) {
                WireMessage wireMessage = prepareWireMessage(ledger.getMessage().getSphereId(), data);

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
     */
    private byte[] compressMsg(final String data) {
        final byte[] temp;
        try {
            temp = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
        }

        //logger.info("Before Compression Msg byte length: {}", temp.length);

        final long compStartTime = System.currentTimeMillis();
        final byte[] wireData = TextCompressor.compress(temp);
        final long compEndTime = System.currentTimeMillis();

        logger.info("Compression Took {} milliseconds", compEndTime - compStartTime);

        //After Compression Byte Length is
        //logger.info("After Compression Msg byte length: {}", wireData.length);
        return wireData;
    }

    /**
     * Encrypts the String msg (testing with local testKey)
     */
    private byte[] encryptMsg(String sphereId, byte[] msgData) {
        long startTime = 0;

        if (logger.isTraceEnabled()) {
            logger.trace("Before Encryption Msg byte length: {}", msgData.length);
            startTime = System.nanoTime();
        }

        //Encrypt the data.. To test the local encryption
        //msg = cipherService.encrypt(msgData, testKey).getBytes();
        // temp fix of sending the byte stream
        final String msgDataString = new String(msgData);
        final byte[] msg;

        if (sphereSecurity != null)
            msg = sphereSecurity.encryptSphereContent(sphereId, msgDataString);
        else // No encryption when there is no interface
            msg = msgData;

        if (logger.isTraceEnabled()) {
            logger.trace("Encryption took {} nano seconds", System.nanoTime() - startTime);
            logger.trace("After Encryption Msg byte length: {}", msg.length);
        }

        return msg;
    }

    /**
     * Encrypts the String msg
     * if not enabled, puts the incoming message to outgoing
     * return null means, encryption failed
     */
    private byte[] decryptMsg(String sphereId, WireMessage.WireMsgStatus msgStatus, byte[] msgData) {

        byte[] msg = null;

        if ((msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
                || (msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED)) {

            String data;

            try {
                if (sphereSecurity != null)
                    data = sphereSecurity.decryptSphereContent(sphereId, msgData);
                else // No decryption when there is no interface
                    data = new String(msgData, "UTF-8");

                if (data != null) {
                    msg = data.getBytes("UTF-8");
                    //logger.info("decrypted size >> " + message.length);
                } else {
                    if (logger.isInfoEnabled())
                        logger.info("unable to decrypt msg for sphere id >> {}", sphereId);
                }
            } catch (UnsupportedEncodingException e) {
                throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
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
    @Override
    public boolean sendEventLedger(EventLedger ledger) {
        final String data = ledger.getSerializedMessage();

        if (data == null) return false;


        if (ledger.getHeader() instanceof MulticastHeader) {
            //TODO: for event message decrypt the header here
            // if the intended zirk is available in sadl message is decrypted
            WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereId(), data);

            // encrypt the header

            byte[] header;

            try {
                header = ledger.getSerializedHeader().getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
            }

            byte[] headerData = encryptMsg(wireMessage.getSphereId(), header);

            wireMessage.setHeaderMsg(headerData);

            wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_EVENT);


            byte[] wireByteMessage = wireMessage.serialize();
            return sendToAll(wireByteMessage, false);
        } else {
            UnicastHeader uHeader = (UnicastHeader) ledger.getHeader();
            String recipient = uHeader.getRecipient().device;

            //TODO: for event message decrypt the header here
            // if the intended zirk is available in sadl message is decrypted
            WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereId(), data);

            // encrypt the header

            byte[] headerData = encryptMsg(wireMessage.getSphereId(), ledger.getSerializedHeader().getBytes());

            wireMessage.setHeaderMsg(headerData);

            wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_EVENT);


            if (null == uHeader || uHeader.getRecipient() == null
                    || uHeader.getRecipient().device == null || uHeader.getRecipient().device.length() == 0) {
                logger.error("Message not of accepted type");
                return false;
            }

            byte[] wireByteMessage = wireMessage.serialize();
            return sendToOne(wireByteMessage, recipient, false);
        }
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

        try {
            wireMessage.setMsg(message.getMsg().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
        }

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
     * handle the wire message - loop back. Not used anymore. Pubsubbroker handles this.
     */

//    public boolean processWireMessage(String deviceId, Ledger ledger) {
//        // start thread pool
//        if ((executor != null) && !executor.isShutdown()) {
//
//            ProcessIncomingMessage inMsg = new ProcessIncomingMessage(/*this, */deviceId, ledger);
//
//            executor.execute(inMsg);
//        } else {
//            logger.error("thread pool is not active.");
//        }
//
//        return true;
//    }

    private boolean processCtrl(String deviceId, WireMessage wireMessage) {

        // fixme: check the version
        byte[] msg = parseCtrlMessage(wireMessage);

        if (msg != null) {
            String processedMsg;

            try {
                processedMsg = new String(msg, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
            }
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
                try {
                    message = processedMsg.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
                }


        }


        return message;
    }

    //enable the above code later. Quickfix network device id is taken as local ip as of now
    // for zyre this needs to return from actual comms
    public String getNodeId() {
        return networkManager.getDeviceIp();
    }


    private boolean processMessageEvent(String deviceId, WireMessage wireMessage) {

        MessageLedger msgLedger = new MessageLedger();


        // fixme: check the version

        // device Id
        BezirkZirkEndPoint endPoint = new BezirkZirkEndPoint(deviceId, null);
        msgLedger.setSender(endPoint);

        try {
            msgLedger.setMsg(new String(wireMessage.getMsg(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
        }

        // for diag the message is not compressed
        if (notification != null) {
            notification.diagMsg(msgLedger);
        }

        return true;


    }

    private boolean processEvent(String deviceId, WireMessage wireMessage) {

        EventLedger eventLedger = new EventLedger();
        //eventLedger

        // fixme: check the version
        // setting sphere id instead of name
        //eventLedger.getHeader().setSphereId(wireMessage.getSphereId());

        if (setEventHeader(eventLedger, wireMessage)) {

            // override sender zirk end point device id with local id
            eventLedger.getHeader().getSender().device = deviceId;

            eventLedger.setEncryptedMessage(wireMessage.getMsg());

            // pubsubbroker encrypts the data
            // FIXME: in case of compressed message sadl has to decompress
            // at the moment sadl is generic for udp and other comms, hence make changes there

            msgDispatcher.dispatchServiceMessages(eventLedger);

            return true;
        }


        return false;

    }

    private boolean setEventHeader(EventLedger eLedger, WireMessage wireMessage) {
        // decrypt the header
        final byte[] data = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(), wireMessage.getHeaderMsg());

        if (data == null) // header decrypt failed. unknown sphere id
            return false;

        final String headerData;

        try {
            headerData = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw (AssertionError) new AssertionError("UTF-8 is not supported").initCause(e);
        }

//        Header header = Header.fromJson(headerData,Header.class);

        final Gson gson = new Gson();

        if (wireMessage.isMulticast()) {
            MulticastHeader mHeader = gson.fromJson(headerData, MulticastHeader.class);

            eLedger.setHeader(mHeader);

            eLedger.setIsMulticast(true);

        } else {

            UnicastHeader uHeader = gson.fromJson(headerData, UnicastHeader.class);

            eLedger.setHeader(uHeader);

            eLedger.setIsMulticast(false);

        }

        return true;
    }

   /* @Override
    public boolean sendStream(String uniqueKey) {
        if (bezirkStreamManager != null) {
            logger.info("sending stream >" + uniqueKey);
            return bezirkStreamManager.sendStream(uniqueKey);
        } else {

            logger.error("BezirkStreamManager is not initialized.");
            return false;
        }
    }*/

    @Override
    public boolean processStreamRecord(SendFileStreamAction streamAction, Iterable<String> sphereList) {

        if (bezirkStreamManager != null) {

            return bezirkStreamManager.processStreamRecord(streamAction, sphereList);

        } else {

            logger.error("BezirkStreamManager is not initialized.");
            return false;
        }
    }

    @Override
    public boolean registerNotification(com.bezirk.middleware.core.comms.CommsNotification notification) {

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
//    private boolean bridgeControlMessage(String deviceId, final ControlLedger tcMessage) {
//
//        if (ControlMessage.Discriminator.StreamRequest == tcMessage.getMessage().getDiscriminator()) {
//            return sendStream(tcMessage.getMessage().getUniqueKey());
//        } else {
//            return processWireMessage(deviceId, tcMessage);
//        }
//    }
    @Override
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, com.bezirk.middleware.core.comms.CtrlMsgReceiver receiver) {

        return msgDispatcher.registerControlMessageReceiver(id, receiver);

    }

    /* register event message receiver */
    @Override
    public boolean registerEventMessageReceiver(EventMsgReceiver receiver) {

        msgDispatcher.registerEventMessageReceiver(receiver);

        return true;
    }

    /*@Override
    public void setSphereSecurity(SphereSecurity sphereSecurity) {
        if(null!=bezirkStreamManager){
            logger.debug("bezirkStreamManager is not null");
            bezirkStreamManager.setSphereSecurity(sphereSecurity);
        }else{
            logger.debug("bezirkStreamManager is null");
        }

    }
        bezirkStreamManager.setSphereSecurityForEncryption(sphereSecurity);
    }*/

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
                if (notification != null) {
                    notification.versionMismatch(mismatchedVersion);
                }
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
