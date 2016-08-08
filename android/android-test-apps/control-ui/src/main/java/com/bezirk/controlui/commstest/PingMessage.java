package com.bezirk.controlui.commstest;

/**
 * Sample Ping message that will be sent
 */
public class PingMessage extends TestMessage {
    protected String deviceName;
    protected int pingId;
    protected String deviceIp;

    public PingMessage() {
        setMessageType(PingMessage.class.getSimpleName());
    }

    static boolean isPing(TestMessage msg) {
        return msg instanceof PingMessage;
    }
}
