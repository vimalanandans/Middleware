package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

/**
 * Created by Vimal on 12/9/2015.
 * <p/>
 * This is generic ledger which works only between comms without encryption.
 * this is meant for comms layer diagnostic tests / performance tests etc.
 */
public class MessageLedger extends Ledger /*implements Serializable*/ {

    String MsgType; // Generic Message type
    UhuServiceEndPoint sender;
    UhuServiceEndPoint recipient;
    String Msg; // serialized message

    public boolean isMulticast() {
        if (recipient != null &&
                (recipient.device != null) &&
                recipient.device.length() != 0) {
            return false;
        }
        return true;
    }
/*
    // wire format is serialized
    public byte[] toJSON()  throws IOException {
        Gson gson = new Gson();
        return gson.toJson(this).getBytes();
        // send the compressed string
        //return compress (gson.toJson(this));
    }
    //fromJSON data
    public static MessageLedger fromJSON(byte[] data) {

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

    public UhuServiceEndPoint getSender() {
        return sender;
    }

    public void setSender(UhuServiceEndPoint sender) {
        this.sender = sender;
    }

    public UhuServiceEndPoint getRecipient() {
        return recipient;
    }

    public void setRecipient(UhuServiceEndPoint recipient) {
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
