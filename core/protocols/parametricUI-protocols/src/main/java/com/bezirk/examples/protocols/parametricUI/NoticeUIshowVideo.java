/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.Event;

/**
 * @see com.bosch.upa.uhu.API.messages.Event
 */
public class NoticeUIshowVideo extends Event {
	
	/**
	 * Useful for static references to the event topic, e.g. protocol definitions
	 */
	public static final String TOPIC = NoticeUIshowVideo.class.getSimpleName();

	// Payload
	private String videoURL;
	
	/**
	 * A notice to display the specified video
	 * 
	 * @param videoURL
	 */
	public NoticeUIshowVideo(String videoURL) {
		// set UhuMessage properties: useful for discrimination/deserialization
		super(Stripe.NOTICE, TOPIC);
		// set event payload: useful for the consumer services
		this.videoURL = videoURL;
	}
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return
	 */
	public static NoticeUIshowVideo deserialize(String json) {
		return Event.deserialize(json, NoticeUIshowVideo.class);
	}
	
	public String getVideoURL() {
		return videoURL;
	}

}
