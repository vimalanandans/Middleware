package com.bezirk.services.light.protocol;

import java.util.Set;

import com.bezirk.api.addressing.Location;
import com.bezirk.api.messages.Event;

public class ResponseActiveLights extends Event {

	public static final String TOPIC = ResponseActiveLights.class.getSimpleName();
	
	private Set<LightDetails> lightIdDetails;
	private Set<Integer> lightIds;
	private Location location;
	
	public ResponseActiveLights() {
		super(Stripe.REPLY, TOPIC);
	}
	
	public Set<LightDetails> getLightIdDetails() {
		return lightIdDetails;
	}

	public void setLightIdDetails(Set<LightDetails> lightIdDetails) {
		this.lightIdDetails = lightIdDetails;
	}

	public Set<Integer> getLightIds() {
		return lightIds;
	}

	public void setLightIds(Set<Integer> lightIds) {
		this.lightIds = lightIds;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	
	
}
