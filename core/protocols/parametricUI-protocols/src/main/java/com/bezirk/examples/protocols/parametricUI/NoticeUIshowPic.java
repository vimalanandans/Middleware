/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bosch.upa.uhu.API.messages.Event
 */
public class NoticeUIshowPic extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = NoticeUIshowPic.class.getSimpleName();

	// Payload
	private String picURL;
	
	/**
	 * A notice to display the specified picture
	 * 
	 * @param picURL
	 */
	public NoticeUIshowPic(String picURL) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.NOTICE, TOPIC);
		// set event payload: useful for the consumer services
		this.picURL = picURL;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static NoticeUIshowPic deserialize(String json) {
		return Event.deserialize(json, NoticeUIshowPic.class);
	}
	
	public String getPicURL() {
		return picURL;
	}
}
