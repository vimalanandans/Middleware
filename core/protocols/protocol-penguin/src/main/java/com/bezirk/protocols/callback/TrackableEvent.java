/**
 * TrackableEvent
 * 
 * Adds a request ID to an event, with affordances for creating them automatically.
 * Responses should be given the same requestID as the message they're responding to.
 * 
 * @author dadamson
 */
package com.bezirk.protocols.callback;

import com.bezirk.api.messages.Event;

public class TrackableEvent extends Event
{
	/**
	 * if a response, the ID of the request to which this is a response. If a response, a unique ID that the response can reference.
	 */
	protected String requestID;
	
	/* constructors */
	
	public TrackableEvent (Stripe stripe, String topic) {
		super(stripe, topic);
		requestID = generateRequestID();
	}
	
	public TrackableEvent (Stripe stripe, String topic, String requestID) {
		super(stripe, topic);
		this.setRequestID(requestID);
	}
	

	/* getters and setters */

	// requestID
	public String getRequestID () { return requestID; }
	public void setRequestID (String requestID) { this.requestID = requestID; }

	/**
	 * auto-creates requestID for this new message.
	 * @return
	 */
	protected String generateRequestID ()
	{
		//unique by device, topic, event-object, and time. Possibly overkill.
		return this.hashCode() + "_" + System.currentTimeMillis();
	}
	

}