package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

/**
 * This is generic ledger which works only between comms without encryption.
 * this is meant for comms layer diagnostic tests / performance tests etc.
 */
public class MessageLedger extends Ledger /*implements Serializable*/ {

    String MsgType; // Generic Message type
    BezirkZirkEndPoint sender;
    BezirkZirkEndPoint recipient;
    String Msg; // serialized message

    public boolean isMulticast() {
        return recipient != null &&
                recipient.device != null &&
                recipient.device.length() != 0;
    }
/*
    // wire format is serialized
    public byte[] toJson()  throws IOException {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
        // send the compressed string
        //return compress (gson.toJson(this));
    }
    //fromJson data
    public static MessageLedger fromJson(byte[] data) {

        MessageLedger wireMessage = null;
        String json = new String(data);
        //String json = new String(decompress(data));
        Gson gson = new Gson();
        try{
            wireMessage = gson.fromJson(json,MessageLedger.class);
        } catch (JsonParseException e){
            return null;
        }
        return wireMessage;
    }
*/

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
