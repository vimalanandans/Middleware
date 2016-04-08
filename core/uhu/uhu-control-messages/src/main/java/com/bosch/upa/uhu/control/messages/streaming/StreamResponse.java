/**
 * @author: Vijet Badignannvar (bvijet@in.bosch.com)
 */ 
package com.bosch.upa.uhu.control.messages.streaming;

import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.streaming.control.Objects.StreamRecord.StreamingStatus;

/**
 *	This Message is internally sent by the Uhu for hand shaking with the sender.
 */
public class StreamResponse extends UnicastControlMessage {
	/**
	 * Discriminator that uniquely defines the Control message!
	 */
	private final static Discriminator discriminator = ControlMessage.Discriminator.StreamResponse;
	
	/**
	 * Status of the Recipient. PENDING or READY or ADDRESSED or BUSY 
	 */
	public StreamingStatus status;

	/**
	 * The ip at which the recipient is listening. service end point is generic. may or maynot contain ip address
	 */
	public String streamIp = "";

	/**
	 * The port at which the recipient is listening
	 */
	public int streamPort = -1;
	
	/**
	 * This is StreamRequestKey that has to be set by taking the key from StreamRequest
	 */
	public String sReqKey;
	
	public StreamResponse(UhuServiceEndPoint sender, UhuServiceEndPoint recipient, String sphereName, String strmKey,
			StreamingStatus status, String streamIp, int streamPort) {
		super(sender, recipient, sphereName, discriminator, false, strmKey);
		this.status = status;
		this.streamIp = streamIp;
		this.streamPort = streamPort;

	}	
}
