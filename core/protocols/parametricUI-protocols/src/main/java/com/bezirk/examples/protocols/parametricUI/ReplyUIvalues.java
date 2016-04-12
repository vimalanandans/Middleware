/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bezirk.API.messages.Event
 */
public class ReplyUIvalues extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = ReplyUIvalues.class.getSimpleName();

	// Payload
	private InputValuesStringPair[] values;
	
	/**
	 * A reply with the user's chosen values
	 * 
	 * @param values
	 */
	public ReplyUIvalues(InputValuesStringPair[] values) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.REPLY, TOPIC);
		// set event payload: useful for the consumer services
		this.values = values==null ?null:values.clone();
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static ReplyUIvalues deserialize(String json) {
		return Event.deserialize(json, ReplyUIvalues.class);
	}
	
	public InputValuesStringPair[] getValues() {
		return values==null ?null:values.clone();
	}
}
