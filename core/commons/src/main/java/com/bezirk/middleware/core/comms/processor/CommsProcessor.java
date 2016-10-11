package com.bezirk.middleware.core.comms.processor;

import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CommsFeature;
import com.bezirk.middleware.core.comms.CommsMessageDispatcher;
import com.bezirk.middleware.core.comms.CommsNotification;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
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
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.core.util.TextCompressor;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This handles generic comms processing
 * new comms implementations shall use this as base class
 */
public abstract class CommsProcessor implements Comms, Observer {
    private static final Logger logger = LoggerFactory.getLogger(CommsProcessor.class);
    private static final int THREAD_POOL_SIZE = 4;

    private final CommsMessageDispatcher msgDispatcher;
    private final SphereSecurity sphereSecurity = null; //currently not initialized
    /**
     * Version Callback that will be used to inform the platforms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     */
    private CommsNotification notification = null;
    private ExecutorService executor;
    private Streaming bezirkStreamManager = null;
    private final NetworkManager networkManager;

    public CommsProcessor(NetworkManager networkManager, CommsNotification commsNotification, Streaming streaming) {
        this.notification = commsNotification;
        this.networkManager = networkManager;
        this.msgDispatcher = new CommsMessageDispatcher();
        if (streaming != null) {
            bezirkStreamManager = streaming;
        }
    }

    public void startComms() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void stopComms() {
        if (executor != null) {
            shutdownAndAwaitTermination(executor);
        }
        if (bezirkStreamManager != null) {
            bezirkStreamManager.endStreams();
        }

    }

    private void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS))
                    logger.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
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
    public boolean sendControlMessage(ControlMessage message) {
        ControlLedger ledger = new ControlLedger();
        ledger.setMessage(message);
        ledger.setSphereId(message.getSphereId());
        ledger.setSerializedMessage(ledger.getMessage().serialize());

        return sendControlLedger(ledger);
    }

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

        /*Step 1 :Compression :  Do the compression if message compression is enabled*/
        if (CommsFeature.WIRE_MSG_COMPRESSION.isActive()) {
            wireData = compressMsg(data);
            wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_COMPRESSED);
        }

        /*Step 2 :Encryption :  perform encryption if it is enabled*/
        if (CommsFeature.WIRE_MSG_ENCRYPTION.isActive()) {
            if (wireData != null) {
                //means compression has happened, now encrypt the content
                wireData = encryptMsg(wireMessage.getSphereId(), wireData);
                wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED);
            } else {
                //means compression was not enabled, now encrypt the msg content only
                try {
                    wireData = encryptMsg(sphereId, data.getBytes(WireMessage.ENCODING));
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getLocalizedMessage());
                    throw new AssertionError(e);
                }
                wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_ENCRYPTED);
            }
        }

        /*Step 3 : Check :  If the Compression and Encryption is disabled. set the Raw message*/
        //set data to wire message
        if (wireData == null) {
            // this means compression and encryption both were disabled
            try {
                wireData = data.getBytes(WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
            wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_RAW);
        }

        wireMessage.setMsg(wireData);
        return wireMessage;
    }

    private byte[] compressMsg(final String data) {
        final byte[] temp;
        try {
            temp = data.getBytes(WireMessage.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
        final long compStartTime = System.currentTimeMillis();
        final byte[] wireData = TextCompressor.compress(temp);
        final long compEndTime = System.currentTimeMillis();
        logger.info("Compression Took {} milliseconds", compEndTime - compStartTime);
        return wireData;
    }

    private byte[] encryptMsg(String sphereId, byte[] msgData) {
        long startTime = 0;

        if (logger.isTraceEnabled()) {
            logger.trace("Before Encryption Msg byte length: {}", msgData.length);
            startTime = System.nanoTime();
        }

        final String msgDataString;
        try {
            msgDataString = new String(msgData, WireMessage.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
        final byte[] msg;

        if (sphereSecurity != null) {
            msg = sphereSecurity.encryptSphereContent(sphereId, msgDataString);
        } else { // No encryption when there is no interface
            msg = msgData;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Encryption took {} nano seconds", System.nanoTime() - startTime);
            logger.trace("After Encryption Msg byte length: {}", msg.length);
        }
        return msg;
    }

    private byte[] decryptMsg(String sphereId, WireMessage.WireMsgStatus msgStatus, byte[] msgData) {
        byte[] msg = null;

        if ((msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
                || (msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED)) {
            String data;
            try {
                if (sphereSecurity != null) {
                    data = sphereSecurity.decryptSphereContent(sphereId, msgData);
                } else { // No decryption when there is no interface
                    data = new String(msgData, WireMessage.ENCODING);
                }
                if (data != null) {
                    msg = data.getBytes(WireMessage.ENCODING);
                } else {
                    logger.info("unable to decrypt msg for sphere id >> {}", sphereId);
                }
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
        } else {
            msg = msgData;
        }
        return msg;
    }

    @Override
    public boolean sendEventLedger(EventLedger ledger) {
        final String data = ledger.getSerializedMessage();

        if (data == null) {
            return false;
        }

        if (ledger.getHeader() instanceof MulticastHeader) {
            //TODO: for event message decrypt the header here
            // if the intended zirk is available in sadl message is decrypted
            WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereId(), data);

            // encrypt the header

            byte[] header;

            try {
                header = ledger.getSerializedHeader().getBytes(WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
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
            byte[] headerData;
            try {
                headerData = encryptMsg(wireMessage.getSphereId(),
                        ledger.getSerializedHeader().getBytes(WireMessage.ENCODING));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }

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

    public boolean sendMessageLedger(MessageLedger message) {
        WireMessage wireMessage = new WireMessage();
        // configure raw msg event
        wireMessage.setMsgType(WireMessage.WireMsgType.MSG_EVENT);

        try {
            wireMessage.setMsg(message.getMsg().getBytes(WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
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
     * Send the message to all nodes/peers (Multicast message)
     */
    public abstract boolean sendToAll(byte[] msg, boolean isEvent);

    /**
     * Send the message to a node/peer (Unicast message)
     * nodeId is equivalent to deviceId
     */
    public abstract boolean sendToOne(byte[] msg, String nodeId, boolean isEvent);

    public boolean processWireMessage(String deviceId, String msg) {
        if ((executor != null) && !executor.isShutdown()) {
            ProcessIncomingMessage inMsg = new ProcessIncomingMessage(deviceId, msg);
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
            String processedMsg;

            try {
                processedMsg = new String(msg, WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
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
        /*Step 1 : Decryption :  decrypt the message*/
        byte[] message = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(), wireMessage.getMsg());
        if (message == null) {
            // encryption failed return null
            return message;
        }

        /*Step 2 : De-compress the message :  de-compress the message*/
        if ((wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED)
                || (wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_COMPRESSED)) {
            //mean the data is decrypted and not to decompress
            byte[] temp = message;
            String processedMsg = TextCompressor.decompress(temp);

            if ((processedMsg != null) && !processedMsg.isEmpty())
                try {
                    message = processedMsg.getBytes(WireMessage.ENCODING);
                } catch (UnsupportedEncodingException e) {
                    logger.error(e.getLocalizedMessage());
                    throw new AssertionError(e);
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

        BezirkZirkEndPoint endPoint = new BezirkZirkEndPoint(deviceId, null);
        msgLedger.setSender(endPoint);

        try {
            msgLedger.setMsg(new String(wireMessage.getMsg(), WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }

        if (notification != null) {
            notification.diagMsg(msgLedger);
        }
        return true;
    }

    private boolean processEvent(String deviceId, WireMessage wireMessage) {
        EventLedger eventLedger = new EventLedger();
        // fixme: check the version

        if (setEventHeader(eventLedger, wireMessage)) {
            // override sender zirk end point device id with local id
            eventLedger.getHeader().getSender().device = deviceId;
            eventLedger.setEncryptedMessage(wireMessage.getMsg());
            msgDispatcher.dispatchServiceMessages(eventLedger);
            return true;
        }
        return false;
    }

    private boolean setEventHeader(EventLedger eLedger, WireMessage wireMessage) {
        // decrypt the header
        final byte[] data = decryptMsg(wireMessage.getSphereId(), wireMessage.getWireMsgStatus(),
                wireMessage.getHeaderMsg());
        if (data == null) {// header decrypt failed. unknown sphere id
            return false;
        }

        final String headerData;
        try {
            headerData = new String(data, WireMessage.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }

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
    public boolean registerNotification(CommsNotification notification) {
        if (this.notification == null) {
            this.notification = notification;
            return true;
        }
        return false;
    }

    @Override
    public boolean registerControlMessageReceiver(ControlMessage.Discriminator id, CtrlMsgReceiver receiver) {
        return msgDispatcher.registerControlMessageReceiver(id, receiver);
    }

    @Override
    public boolean registerEventMessageReceiver(EventMsgReceiver receiver) {
        msgDispatcher.registerEventMessageReceiver(receiver);
        return true;
    }

    private class ProcessIncomingMessage implements Runnable {
        private final String deviceId;
        private String msg = null;
        private final Ledger ledger = null;

        public ProcessIncomingMessage(String deviceId, String msg) {
            this.deviceId = deviceId;
            this.msg = msg;
        }

        @Override
        public void run() {
            if (ledger != null) {
                // not a loopback, dispatch it directly
                dispatchMessage(ledger);
                return;
            }

            if (!WireMessage.checkVersion(msg)) {
                String mismatchedVersion = WireMessage.getVersion(msg);
                if (notification != null) {
                    notification.versionMismatch(mismatchedVersion);
                }
                return;
            }

            WireMessage wireMessage = null;
            try {
                wireMessage = WireMessage.deserialize(msg.getBytes(WireMessage.ENCODING));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
            if (wireMessage == null) {
                logger.error(" deserialization failed >> " + msg);
                return;
            }

            switch (wireMessage.getMsgType()) {
                case MSG_MULTICAST_CTRL:
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
