/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bosch.upa.uhu.API.messages.Event
 */
public class RequestUImultipleChoice extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = RequestUImultipleChoice.class.getSimpleName();

	// Payload
	private String[] availableChoices;
	private long expiration;
	
	/**
	 * Request user to select options
	 * 
	 * @param availableChoices
	 * @param expiration : how long to offer the request
	 */
	public RequestUImultipleChoice(String[] availableChoices, long expiration) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.REQUEST, TOPIC);
		// set event payload: useful for the consumer services
		this.availableChoices = availableChoices==null ?null:availableChoices.clone();
		this.expiration = expiration;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static RequestUImultipleChoice deserialize(String json) {
		return Event.deserialize(json, RequestUImultipleChoice.class);
	}
	
	public String[] getAvailableChoices() {
		return availableChoices==null ?null:availableChoices.clone();
	}

	public long getExpiration() {
		return expiration;
	}
}
