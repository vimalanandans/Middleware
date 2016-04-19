package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class RequestPolicy extends Event {
	public static final String TOPIC = RequestPolicy.class.getSimpleName();
	private String location;
	
	public RequestPolicy(String location) {
		super(Stripe.REQUEST, TOPIC);
		this.location = location;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

}
