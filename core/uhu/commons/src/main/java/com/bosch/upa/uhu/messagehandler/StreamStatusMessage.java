package com.bosch.upa.uhu.messagehandler;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


/**
 * Sub class of UhuCallbackMessage that gives back the StreamStatusCallback to proxyForUhu
 */
public final class StreamStatusMessage extends ServiceIncomingMessage {
	/**
	 * StreamStatus id. 0 -> UnSuccessful | 1 -> Successful.
	 */
	public int streamStatus = 0;
	
	/**
	 * Id of the pushed Stream
	 */
	public short streamId;
	
	public StreamStatusMessage() {
		callbackDiscriminator = "STREAM_STATUS";
	}

	public StreamStatusMessage(UhuServiceId recipientId, int streamStatus, short streamId) {
		callbackDiscriminator = "STREAM_STATUS";
		recipient = recipientId;
		this.streamStatus = streamStatus;
		this.streamId = streamId;
	}
	
	
}
 	