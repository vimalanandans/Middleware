package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * This is generic ledger which works only between comms without encryption.
 * this is meant for comms layer diagnostic tests / performance tests etc.
 */
public class MessageLedger implements Ledger /*implements Serializable*/ {
    private String MsgType; // Generic Message type
    private BezirkZirkEndPoint sender;
    private BezirkZirkEndPoint recipient;
    private String Msg; // serialized message

    public boolean isMulticast() {
        return recipient != null &&
                recipient.device != null &&
                recipient.device.length() != 0;
    }

    public BezirkZirkEndPoint getSender() {
        return sender;
    }

    public void setSender(BezirkZirkEndPoint sender) {
        this.sender = sender;
    }

    public BezirkZirkEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(BezirkZirkEndPoint recipient) {
        this.recipient = recipient;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }
}
