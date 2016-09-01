package com.bezirk.middleware.core.comms;

/**
 * Interface to return the incoming message
 */
public interface MessageReceiver {
    boolean processIncomingMessage(String nodeId, byte[] data);
}
