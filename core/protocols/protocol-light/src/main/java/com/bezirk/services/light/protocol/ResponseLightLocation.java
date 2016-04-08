package com.bezirk.services.light.protocol;

import com.bezirk.api.addressing.Location;
import com.bezirk.api.messages.Event;

public class ResponseLightLocation extends Event {
	public static final String TOPIC = ResponseLightLocation.class.getSimpleName();
	private Integer lightId;
	private Location location;
	
	public ResponseLightLocation(Integer lightId, Location location) {
		super(Stripe.REPLY, TOPIC);
		this.lightId = lightId;
		this.location = location;
	}
	/**
	 * @return the lightId
	 */
	public Integer getLightId() {
		return lightId;
	}
	
	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}
	
	

}
