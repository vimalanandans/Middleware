package com.bezirk.protocols.penguin.v01;

import com.bezirk.api.messages.Event;

public class InitUserProxyUI extends Event
{
	/**
	 * topic
	 */
	public static final String topic = "init-user-proxy-ui";

	/* properties */
	
	private String user = null;	
	
	/* constructors */
	
	public InitUserProxyUI() 
	{
		super(Stripe.REQUEST, topic);
	}
	
	/* getters and setters */
	
	// user
	public void setUser (String _v) { this.user = _v; }
	public String getUser () { return this.user; }
	
	public static InitUserProxyUI deserialize(String json) {
		return Event.deserialize(json, InitUserProxyUI.class);
	}
	
}
