package com.bezirk.middleware.core.streaming.rtc;

import com.bezirk.middleware.core.control.messages.UnicastControlMessage;

/**
 * API for using bezirk as signaling
 */
public interface Signaling {

    /**
     * Starts the actual handshaking between two parties
     *
     * @param ctrlMsg control message containing handshake data
     */
    void startSignaling(final UnicastControlMessage ctrlMsg);

    /**
     * Send the handshake control message
     *
     * @param ctrlMsg control message containing handshake data
     */
    void sendControlMessage(final UnicastControlMessage ctrlMsg);

    /**
     * Receive the handshake control message
     *
     * @param ctrlMsg control message containing handshake data
     */
    void receiveControlMessage(final UnicastControlMessage ctrlMsg);
}
