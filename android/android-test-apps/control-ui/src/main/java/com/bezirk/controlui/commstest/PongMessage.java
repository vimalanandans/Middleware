package com.bezirk.controlui.commstest;

/**
 * Sample Pong message that will be sent
 *
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
public class PongMessage extends TestMessage {
    protected String deviceName;
    protected int pingId;
    protected String senderIP;
    protected String pingRequestId;

    public PongMessage() {
        setMessageType(PongMessage.class.getSimpleName());
    }

    static boolean isPong(TestMessage msg) {
        return msg instanceof PongMessage;
    }
}
