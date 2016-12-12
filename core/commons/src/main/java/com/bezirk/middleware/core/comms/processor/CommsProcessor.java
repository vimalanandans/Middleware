/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.comms.processor;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CommsMessageDispatcher;
import com.bezirk.middleware.core.comms.CommsNotification;
import com.bezirk.middleware.core.comms.CtrlMsgReceiver;
import com.bezirk.middleware.core.control.messages.ControlLedger;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastHeader;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.core.util.TextCompressor;

import org.jetbrains.annotations.NotNull;
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
    private static final boolean WIRE_MSG_COMPRESSION = false;
    private static final boolean WIRE_MSG_ENCRYPTION = true;
    private final CommsMessageDispatcher msgDispatcher;
    //currently not initialized
    private final SphereSecurity sphereSecurity = null;
    /**
     * Version Callback that will be used to inform the platforms when there is mismatch in versions.
     * This parameter will be injected in all the components that will be checking for versions to
     * be compatible before they are processed.
     */
    private final CommsNotification notification;
    private ExecutorService executor;

    public CommsProcessor(CommsNotification commsNotification) {
        this.notification = commsNotification;
        this.msgDispatcher = new CommsMessageDispatcher();
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        // Disable new tasks from being submitted
        pool.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                // Cancel currently executing tasks
                pool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    logger.error("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    private static byte[] compressMsg(@NotNull final String data) {
        final byte[] messageBytes;
        try {
            messageBytes = data.getBytes(WireMessage.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to get wire message bytes to compress the message", e);
            throw new AssertionError(e);
        }
        final long compStartTime = System.currentTimeMillis();
        final byte[] wireData = TextCompressor.compress(messageBytes);
        final long compEndTime = System.currentTimeMillis();
        logger.trace("Compression Took {} milliseconds", compEndTime - compStartTime);
        return wireData;
    }

    protected void startComms() {
        executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    protected void stopComms() {
        if (executor != null) {
            shutdownAndAwaitTermination(executor);
        }
    }

    @Override
    public boolean sendControlMessage(@NotNull ControlMessage message) {
        final ControlLedger ledger = new ControlLedger();
        ledger.setMessage(message);
        ledger.setSphereId(message.getSphereId());
        ledger.setSerializedMessage(ledger.getMessage().serialize());

        return sendControlLedger(ledger);
    }

    @Override
    public boolean sendControlLedger(@NotNull ControlLedger ledger) {
        final ControlMessage ledgerMessage = ledger.getMessage();

        if (!(ledgerMessage instanceof MulticastControlMessage) &&
                !(ledgerMessage instanceof UnicastControlMessage)) {
            logger.debug("unknown control message type {}", ledgerMessage.getClass().getName());
            return false;
        }

        final String data = ledger.getSerializedMessage();
        if (data == null) {
            return false;
        }

        // At this point we know the message is a Multicast or Unicast control message
        final WireMessage.WireMsgType messageType = ledgerMessage instanceof MulticastControlMessage ?
                WireMessage.WireMsgType.MSG_MULTICAST_CTRL : WireMessage.WireMsgType.MSG_UNICAST_CTRL;

        final WireMessage wireMessage = prepareWireMessage(ledgerMessage.getSphereId(), data);
        wireMessage.setMsgType(messageType);
        final byte[] wireByteMessage = wireMessage.serialize();
        return sendToAll(wireByteMessage, false);
    }

    /**
     * prepares the WireMessage and returns it based on encryption and compression settings
     */
    private WireMessage prepareWireMessage(String sphereId, String data) {
        final WireMessage wireMessage = new WireMessage();
        wireMessage.setSphereId(sphereId);
        byte[] wireData = null;

        /*Step 1 :Compression :  Do the compression if message compression is enabled*/
        if (WIRE_MSG_COMPRESSION) {
            wireData = compressMsg(data);
            wireMessage.setWireMsgStatus(WireMessage.WireMsgStatus.MSG_COMPRESSED);
        }

        /*Step 2 :Encryption :  perform encryption if it is enabled*/
        if (WIRE_MSG_ENCRYPTION) {
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

    private byte[] encryptMsg(String sphereId, byte[] msgData) {
        final String msgDataString;
        try {
            msgDataString = new String(msgData, WireMessage.ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("Failed to get wire message bytes to encrypt the message", e);
            throw new AssertionError(e);
        }
        final byte[] msg;

        if (sphereSecurity != null) {
            msg = sphereSecurity.encryptSphereContent(sphereId, msgDataString);
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
            //TODO: for event message decrypt the header here if the intended zirk is available in
            // PubSubBroker message is decrypted

            // encrypt the header
            final byte[] header;

            try {
                header = ledger.getSerializedHeader().getBytes(WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }

            final WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereId(), data);
            final byte[] headerData = encryptMsg(wireMessage.getSphereId(), header);

            wireMessage.setHeaderMsg(headerData);

            wireMessage.setMsgType(WireMessage.WireMsgType.MSG_MULTICAST_EVENT);

            final byte[] wireByteMessage = wireMessage.serialize();
            return sendToAll(wireByteMessage, false);
        } else {
            final UnicastHeader unicastHeader = (UnicastHeader) ledger.getHeader();

            if (unicastHeader.getRecipient() == null) {
                logger.error("Unicast message does not have a recipient");
                return false;
            }

            final String recipient = unicastHeader.getRecipient().getDevice();

            if (recipient == null || recipient.isEmpty()) {
                logger.error("Unicast message does not have a recipient device");
                return false;
            }

            //TODO: for event message decrypt the header here if the intended zirk is available in
            // PubSubBroker message is decrypted
            final WireMessage wireMessage = prepareWireMessage(ledger.getHeader().getSphereId(), data);

            // encrypt the header
            final byte[] headerData;
            try {
                headerData = encryptMsg(wireMessage.getSphereId(),
                        ledger.getSerializedHeader().getBytes(WireMessage.ENCODING));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }

            wireMessage.setHeaderMsg(headerData);

            wireMessage.setMsgType(WireMessage.WireMsgType.MSG_UNICAST_EVENT);

            final byte[] wireByteMessage = wireMessage.serialize();
            return sendToOne(wireByteMessage, recipient, false);
        }
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

    protected boolean processWireMessage(String deviceId, String msg) {
        if (executor != null && !executor.isShutdown()) {
            final IncomingMessageProcessor messageProcessor
                    = new IncomingMessageProcessor(notification, msgDispatcher, deviceId, msg);
            executor.execute(messageProcessor);
        } else {
            logger.error("thread pool is not active.");
            return false;
        }

        return true;
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
}
