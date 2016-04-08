package com.bezirk.protocols.penguin.v01;

import com.bezirk.api.messages.Event;

public class InitUserProxyRemoteUI extends Event {
	
	/**
	 * topic
	 */
	public static final String topic = "init-user-proxy-remote-ui";
	
    /* properties */
	private String user = null;
	private String location = null;
	private String partOfDay = null;
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPartOfDay() {
		return partOfDay;
	}

	public void setPartOfDay(String partOfDay) {
		this.partOfDay = partOfDay;
	}

	public InitUserProxyRemoteUI()
	{
		super(Stripe.REQUEST, topic);
	}
	
	public static InitUserProxyRemoteUI deserialize(String json) {
		return Event.deserialize(json, InitUserProxyRemoteUI.class);
	}

}
