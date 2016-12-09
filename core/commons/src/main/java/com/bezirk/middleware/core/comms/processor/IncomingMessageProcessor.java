package com.bezirk.middleware.core.comms.processor;

import com.bezirk.middleware.core.comms.CommsMessageDispatcher;
import com.bezirk.middleware.core.comms.CommsNotification;
import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.EventLedger;
import com.bezirk.middleware.core.control.messages.MessageLedger;
import com.bezirk.middleware.core.control.messages.MulticastHeader;
import com.bezirk.middleware.core.control.messages.UnicastHeader;
import com.bezirk.middleware.core.util.TextCompressor;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

final class IncomingMessageProcessor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(IncomingMessageProcessor.class);

    private final CommsNotification notification;
    private final CommsMessageDispatcher msgDispatcher;
    private final String deviceId;
    private final String msg;

    IncomingMessageProcessor(@NotNull final CommsNotification notification,
                             @NotNull final CommsMessageDispatcher msgDispatcher,
                             final String deviceId, @NotNull String msg) {
        this.notification = notification;
        this.msgDispatcher = msgDispatcher;
        this.deviceId = deviceId;
        this.msg = msg;
    }

    @Override
    public void run() {
        if (!WireMessage.checkVersion(msg)) {
            notification.versionMismatch(WireMessage.getVersion(msg));
            return;
        }

        final WireMessage wireMessage;
        try {
            wireMessage = WireMessage.deserialize(msg.getBytes(WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }

        if (wireMessage == null) {
            logger.error("deserialization failed {}", msg);
            return;
        }

        switch (wireMessage.getMsgType()) {
            case MSG_MULTICAST_CTRL:
            case MSG_UNICAST_CTRL:
                processCtrl(deviceId, wireMessage);
                break;
            case MSG_MULTICAST_EVENT:
            case MSG_UNICAST_EVENT:
                processEvent(deviceId, wireMessage);
                break;
            case MSG_EVENT:
                processMessageEvent(deviceId, wireMessage);
                break;
            default:
                logger.error("Unknown event type {}", wireMessage.getMsgType());
        }
    }

    private boolean processEvent(final String deviceId, @NotNull WireMessage wireMessage) {
        final EventLedger eventLedger = new EventLedger();
        // fixme: check the version

        if (!setEventHeader(eventLedger, wireMessage)) {
            return false;
        }

        // override sender zirk end point device id with local id
        eventLedger.getHeader().getSender().device = deviceId;
        eventLedger.setEncryptedMessage(wireMessage.getMsg());
        msgDispatcher.dispatchServiceMessages(eventLedger);

        return true;
    }

    private boolean setEventHeader(@NotNull EventLedger eLedger, @NotNull WireMessage wireMessage) {
        // decrypt the header
        final byte[] data = decryptMsg(wireMessage.getWireMsgStatus(), wireMessage.getHeaderMsg());
        if (data == null) {
            // header decrypt failed. unknown sphere id
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
            final MulticastHeader mHeader = gson.fromJson(headerData, MulticastHeader.class);
            eLedger.setHeader(mHeader);
            eLedger.setIsMulticast(true);
        } else {
            final UnicastHeader uHeader = gson.fromJson(headerData, UnicastHeader.class);
            eLedger.setHeader(uHeader);
            eLedger.setIsMulticast(false);
        }

        return true;
    }

    private boolean processCtrl(String deviceId, WireMessage wireMessage) {
        // fixme: check the version
        final byte[] msg = parseCtrlMessage(wireMessage);

        if (msg != null) {
            final String processedMsg;

            try {
                processedMsg = new String(msg, WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error("Failed to encode control message", e);
                throw new AssertionError(e);
            }

            final ControlMessage ctrl = ControlMessage.deserialize(processedMsg, ControlMessage.class);

            ctrl.getSender().device = deviceId;
            msgDispatcher.dispatchControlMessages(ctrl, processedMsg);
            return true;
        }

        return false;
    }

    /**
     * Returns decrypted and decompressed WireMessage.
     */
    private byte[] parseCtrlMessage(@NotNull WireMessage wireMessage) {
        /*Step 1 : Decryption :  decrypt the message*/
        byte[] message = decryptMsg(wireMessage.getWireMsgStatus(), wireMessage.getMsg());
        if (message == null) {
            // decryption failed
            return null;
        }

        /*Step 2 : De-compress the message :  de-compress the message*/
        if (wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED
                || wireMessage.getWireMsgStatus() == WireMessage.WireMsgStatus.MSG_COMPRESSED) {
            final String processedMsg = TextCompressor.decompress(message);

            if (!processedMsg.isEmpty()) {
                try {
                    message = processedMsg.getBytes(WireMessage.ENCODING);
                } catch (UnsupportedEncodingException e) {
                    logger.error("Missing encoding required to fetch control message bytes", e);
                    throw new AssertionError(e);
                }
            }
        }

        return message;
    }

    private boolean processMessageEvent(final String deviceId, @NotNull WireMessage wireMessage) {
        final MessageLedger msgLedger = new MessageLedger();
        // fixme: check the version

        final BezirkZirkEndPoint endPoint = new BezirkZirkEndPoint(deviceId, null);
        msgLedger.setSender(endPoint);

        try {
            msgLedger.setMsg(new String(wireMessage.getMsg(), WireMessage.ENCODING));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }

        notification.diagMsg(msgLedger);

        return true;
    }

    private byte[] decryptMsg(WireMessage.WireMsgStatus msgStatus, byte[] msgData) {
        byte[] msg;

        if (msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED_COMPRESSED
                || msgStatus == WireMessage.WireMsgStatus.MSG_ENCRYPTED) {
            try {
                final String data = new String(msgData, WireMessage.ENCODING);
                msg = data.getBytes(WireMessage.ENCODING);
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getLocalizedMessage());
                throw new AssertionError(e);
            }
        } else {
            msg = msgData;
        }
        return msg;
    }
}
