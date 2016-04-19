package com.bezirk.services.light.protocol;

import java.util.List;

import com.bezirk.middleware.messages.Event;

public class DiscoverLocationResponse extends Event{
	
	public final static String TOPIC = DiscoverLocationResponse.class.getSimpleName();

	public DiscoverLocationResponse() {
		super(Stripe.REPLY, TOPIC);
	}
	
	private List<String> knownLocations;

	public List<String> getKnownLocations() {
		return knownLocations;
	}

	public void setKnownLocations(List<String> knownLocations) {
		this.knownLocations = knownLocations;
	}
	
	

}
