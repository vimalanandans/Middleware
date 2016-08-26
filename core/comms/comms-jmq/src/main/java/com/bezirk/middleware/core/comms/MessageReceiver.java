package com.bezirk.middleware.core.comms;

/**
 * Interface to return the incoming message
 */
public interface MessageReceiver {
    public boolean processIncomingMessage(String nodeId, byte[] data);
}
