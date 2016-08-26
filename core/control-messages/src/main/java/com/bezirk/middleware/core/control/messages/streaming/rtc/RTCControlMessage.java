package com.bezirk.middleware.core.control.messages.streaming.rtc;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.UnicastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * This message is internally used by bezirk for RTC handshake.
 */
public class RTCControlMessage extends UnicastControlMessage {

    /**
     * Discriminator that uniquely defines the Control message!
     */
    private final static Discriminator discriminator = ControlMessage.Discriminator.RTCControlMessage;
    /**
     * Actual handshake message
     */
    private String rtcMsg;
    /**
     * Type of handshake message
     */
    private RTCControlMessageType msgType;

    /**
     * Creates a RTC handshake message with the following parameters
     *
     * @param sender    the sender-end-point
     * @param recipient the recipient-end-point
     * @param sphereId  the unique ID of the sphere
     * @param uniqueKey UniqueKey that is used to match responses to corresponding requests
     * @param msgType   Type of the RTC handshake message
     * @param rtcMsg    Actual message content
     */
    public RTCControlMessage(BezirkZirkEndPoint sender, BezirkZirkEndPoint recipient, String sphereId, String uniqueKey, RTCControlMessageType msgType, String rtcMsg) {
        super(sender, recipient, sphereId, discriminator, false, uniqueKey);
        this.setRtcMsg(rtcMsg);
        this.setMsgType(msgType);
    }

    /**
     * Get the message type of RTC handshake message
     *
     * @return type of message
     */
    public RTCControlMessageType getMsgType() {
        return msgType;
    }

    /**
     * Set the message type of RTC handshake message
     *
     * @param msgType type of message
     */
    public void setMsgType(RTCControlMessageType msgType) {
        this.msgType = msgType;
    }

    /**
     * Get the handshake message
     *
     * @return message content
     */
    public String getRtcMsg() {
        return rtcMsg;
    }

    /**
     * Set the handshake message
     *
     * @param rtcMsg message content
     */
    public void setRtcMsg(String rtcMsg) {
        this.rtcMsg = rtcMsg;
    }

    /**
     * Different types of RTC handshake messages
     */
    public enum RTCControlMessageType {
        RTCOffer, RTCAnswer, RTCCandidate, RTCSessionId
    }
}
