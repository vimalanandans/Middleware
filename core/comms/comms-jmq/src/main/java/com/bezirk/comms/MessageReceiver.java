package com.bezirk.comms;

/**
 * Interface to return the incoming message
 */
public interface MessageReceiver {
    public boolean processIncomingMessage(String nodeId, byte[] data);
}
