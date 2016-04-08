/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bosch.upa.uhu.API.messages.Event
 */
public class NoticeUIshowText extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = NoticeUIshowText.class.getSimpleName();
	
	/**
	 * Enumerated: INFORMATION, WARNING, ERROR
	 */
	public enum TextType {INFORMATION, WARNING, ERROR};
	
	// Payload
	private String text;
	private TextType type;
	private long expiration;
	
	
	/**
	 * A notice to display the specified text
	 * 
	 * @param text to be shown
	 * @param type : display guideline
	 * @param expiration : how long to display the text
	 */
	public NoticeUIshowText(String text, TextType type, long expiration) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.NOTICE, TOPIC);
		// set event payload: useful for the consumer services
		this.text = text;
		this.type = type;
		this.expiration = expiration;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static NoticeUIshowText deserialize(String json) {
		return Event.deserialize(json, NoticeUIshowText.class);
	}

	public String getText() {
		return text;
	}

	public TextType getType() {
		return type;
	}

	public long getExpiration() {
		return expiration;
	}
}
