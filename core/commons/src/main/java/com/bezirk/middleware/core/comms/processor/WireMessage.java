package com.bezirk.middleware.core.comms.processor;

import com.bezirk.middleware.core.util.BezirkVersion;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.zip.InflaterInputStream;

/**
 * Created by Vimal on 11/12/2015.
 * New wire message will contain the control / even ledger message to send and receive
 */
public class WireMessage implements Serializable {
    private static final long serialVersionUID = 8351148484329885907L;
    private static final Logger logger = LoggerFactory.getLogger(WireMessage.class);
    private static final String MSG_VER_STRING = "\"msgVer\":\"";
    /// if the parser type is json, to check the message version VERSION STRING
    private static final String MSG_VER = MSG_VER_STRING + BezirkVersion.getWireVersion() + "\"";
    public static final String ENCODING = "UTF-8";

    //private String msgVer = BezirkVersion.BEZIRK_VERSION;
    // increment the wire message version in bezirk version. when there is a change in message format
    private String msgVer = BezirkVersion.getWireVersion();
    private String sphereId; // sphere id
    private WireMsgType msgType; // control / event. do we need?
    private MsgParserType parserType; // type of parser JSON / BSON for event/ctrl messages
    private WireMsgStatus wireMsgStatus; // status of the msg, if its encrypted and compressed or Raw

    // not don't sore the byte
    // stream in to wiremessage, while json serialization,
    // this would drastically increase the size of wire msg
    private byte[] msg = null; // encrypted msg

    private byte[] headerMsg = null; // message header for event. FIXME : generalize this

    public static boolean checkVersion(String msg) {
        // for json format
        return msg.contains(MSG_VER);
    }

    /**
     * call this when the message format is not able to fetch the data
     */
    public static String getVersion(String msg) {
        String version = "";

        // for json format.
        int start = msg.indexOf(MSG_VER_STRING) + MSG_VER_STRING.length();

        if (start > MSG_VER_STRING.length()) {
            int end = msg.substring(start).indexOf("\"");
            if (start + end < msg.length())
                version = msg.substring(start, start + end);
        }
        return version;
    }

    public static String decompress(byte[] bytes) {
        InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
    }

    public static WireMessage deserialize(byte[] data) {

        WireMessage wireMessage = null;
        String json = null;
        try {
            json = new String(data, ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getLocalizedMessage());
            throw new AssertionError(e);
        }
        Gson gson = new Gson();
        try {
            wireMessage = gson.fromJson(json, WireMessage.class);
        } catch (JsonParseException e) {
            logger.error("Exception in parsing json message from wire message.", e);
        }
        return wireMessage;
    }

    public static WireMessage deserialize(String data) throws UnsupportedEncodingException {
        WireMessage wireMessage = null;
        final String json = decompress(data.getBytes(ENCODING));
        final Gson gson = new Gson();

        try {
            wireMessage = gson.fromJson(json, WireMessage.class);
        } catch (JsonParseException e) {
            logger.error("Exception in parsing json message from wire message.", e);
        }

        return wireMessage;
    }

    public byte[] getHeaderMsg() {
        byte[] headerMsgCopy = new byte[headerMsg.length];
        System.arraycopy(headerMsg, 0, headerMsgCopy, 0, headerMsg.length);
        return headerMsgCopy;
    }

    public void setHeaderMsg(byte[] headerMsg) {
        byte[] headerMsgCopy = new byte[headerMsg.length];
        System.arraycopy(headerMsg, 0, headerMsgCopy, 0, headerMsg.length);

        this.headerMsg = headerMsgCopy;
    }

    public boolean isMulticast() {
        return msgType == WireMsgType.MSG_MULTICAST_CTRL ||
                msgType == WireMsgType.MSG_MULTICAST_EVENT;
    }

    public boolean isCtrlMsg() {
        return msgType == WireMsgType.MSG_MULTICAST_CTRL ||
                msgType == WireMsgType.MSG_UNICAST_CTRL;
    }

    // getter and setter
    public String getMsgVer() {
        return msgVer;
    }

    public void setMsgVer(String msgVer) {
        this.msgVer = msgVer;
    }

    public String getSphereId() {
        return sphereId;
    }

    public void setSphereId(String sphereId) {
        this.sphereId = sphereId;
    }

    public WireMsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(WireMsgType msgType) {
        this.msgType = msgType;
    }

    public MsgParserType getParserType() {
        return parserType;
    }

    public void setParserType(MsgParserType parserType) {
        this.parserType = parserType;
    }

    public byte[] getMsg() {
        byte[] msgCopy = new byte[msg.length];
        System.arraycopy(msg, 0, msgCopy, 0, msg.length);
        return msgCopy;
    }

    public void setMsg(byte[] msg) {
        byte[] msgCopy = new byte[msg.length];
        System.arraycopy(msg, 0, msgCopy, 0, msg.length);
        this.msg = msgCopy;
    }

    public WireMsgStatus getWireMsgStatus() {
        return wireMsgStatus;
    }

    public void setWireMsgStatus(WireMsgStatus wireMsgStatus) {
        this.wireMsgStatus = wireMsgStatus;
    }

    // wire format is serialized
    public byte[] serialize() {
        Gson gson = new Gson();
        try {
            return gson.toJson(this).getBytes(ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("Unsupported encoding for wire message serializer", e);
        }
        return null;
        // send the compressed string
        //return compress (gson.toJson(this));
    }

    public enum WireMsgType {
        MSG_MULTICAST_CTRL,
        MSG_UNICAST_CTRL,
        MSG_MULTICAST_EVENT,
        MSG_UNICAST_EVENT,
        MSG_EVENT, // future use
    }

    public enum WireMsgStatus {
        MSG_RAW,
        MSG_ENCRYPTED,
        MSG_COMPRESSED,
        MSG_ENCRYPTED_COMPRESSED,
        MSG_DECRYPTED
    }

    public enum MsgParserType {
        PARSER_JSON, // default / legacy
        PARSER_BSON //
    }

    /**

     public static <C> C fromJson(String json, Class<C> dC) {
     Gson gson = new Gson();
     return (C) gson.fromJson(json, dC);
     }

     * */
}
