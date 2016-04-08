package com.bezirk.services.light.protocol;

import com.bezirk.api.messages.Event;

public class ClearUserDetails extends Event {
	
	public final static String TOPIC = ClearUserDetails.class.getSimpleName();
	private String userName;

	public ClearUserDetails() {
		super(Stripe.NOTICE, TOPIC);
		// TODO Auto-generated constructor stub
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	

}
