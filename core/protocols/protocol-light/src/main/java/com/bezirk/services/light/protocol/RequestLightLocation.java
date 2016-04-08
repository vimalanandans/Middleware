package com.bezirk.services.light.protocol;

import com.bezirk.api.messages.Event;

public class RequestLightLocation extends Event{
	public static final String TOPIC = RequestLightLocation.class.getSimpleName();

	private Integer lightId;

	public RequestLightLocation(Integer id) {
		super(Stripe.REQUEST, TOPIC);
		this.lightId = id;
	}

	/**
	 * @return the lightId
	 */
	public Integer getLightId() {
		return lightId;
	}


}
