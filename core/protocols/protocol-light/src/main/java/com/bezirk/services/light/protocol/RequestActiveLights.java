package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class RequestActiveLights extends Event{
	public static final String TOPIC = RequestActiveLights.class.getSimpleName();
	
	private String requestedLocation;
	
	public RequestActiveLights() {
		super(Stripe.REQUEST, TOPIC);
	}

	public String getRequestedLocation() {
		return requestedLocation;
	}

	public void setRequestedLocation(String requestedLocation) {
		this.requestedLocation = requestedLocation;
	}
	

}
