package com.bosch.upa.uhu.streaming.rtc;

import com.bosch.upa.uhu.control.messages.UnicastControlMessage;

/**
 * API for using uhu as signaling 
 *
 */
public interface ISignaling {

    /**
     * Starts the actual handshaking between two parties
     *
     * @param ctrlMsg control message containing handshake data
     */
	public void startSignaling(final UnicastControlMessage ctrlMsg);
	
	/**
     * Send the handshake control message
     *
     * @param ctrlMsg control message containing handshake data
     */
	public void sendControlMessage(final UnicastControlMessage ctrlMsg);
	
	/**
     * Receive the handshake control message
     *
     * @param ctrlMsg control message containing handshake data
     */
	public void receiveControlMessage(final UnicastControlMessage ctrlMsg);
}
