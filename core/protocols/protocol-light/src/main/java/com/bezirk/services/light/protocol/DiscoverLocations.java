package com.bezirk.services.light.protocol;

import com.bezirk.api.messages.Event;

public class DiscoverLocations extends Event{
	public final static String TOPIC = DiscoverLocations.class.getSimpleName();

	public DiscoverLocations() {
		super(Stripe.NOTICE, TOPIC);
	}

}
