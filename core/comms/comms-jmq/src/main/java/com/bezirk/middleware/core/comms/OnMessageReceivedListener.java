package com.bezirk.middleware.core.comms;

public interface OnMessageReceivedListener {
    boolean processIncomingMessage(String nodeId, byte[] data);
}
