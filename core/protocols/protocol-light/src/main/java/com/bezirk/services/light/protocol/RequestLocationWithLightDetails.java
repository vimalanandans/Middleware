package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class RequestLocationWithLightDetails  extends Event{
	
	public static final String TOPIC = RequestLocationWithLightDetails.class.getSimpleName();

	public RequestLocationWithLightDetails() {
		super(Stripe.REQUEST, TOPIC);
	}

}
