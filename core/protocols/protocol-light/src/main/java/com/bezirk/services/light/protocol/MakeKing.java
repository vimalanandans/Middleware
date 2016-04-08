package com.bezirk.services.light.protocol;

import com.bezirk.api.messages.Event;

public class MakeKing extends Event {
	public static final String TOPIC = MakeKing.class.getSimpleName();
	private String location;
	private String king;
	
	public MakeKing(String location, String king) {
		super(Stripe.NOTICE, TOPIC);
		this.location = location;
		this.king = king;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @return the king
	 */
	public String getKing() {
		return king;
	}

}
