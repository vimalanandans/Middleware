/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bosch.upa.uhu.API.messages.Event
 */
public class ReplyUIchoices extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = ReplyUIchoices.class.getSimpleName();

	// Payload
	private int[] selectedChoices;
	
	/**
	 * A reply with the user's choices
	 * 
	 * @param selectedChoices
	 */
	public ReplyUIchoices(int[] selectedChoices) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.REPLY, TOPIC);
		// set event payload: useful for the consumer services
		this.selectedChoices = selectedChoices==null ?null:selectedChoices.clone();
	}
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static ReplyUIchoices deserialize(String json) {
		return Event.deserialize(json, ReplyUIchoices.class);
	}
	
	public int[] getSelectedChoices() {
		return selectedChoices==null ?null:selectedChoices.clone();
	}
}
