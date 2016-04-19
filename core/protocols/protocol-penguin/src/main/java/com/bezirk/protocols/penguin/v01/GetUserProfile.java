package com.bezirk.protocols.penguin.v01;

import java.util.ArrayList;
import java.util.List;

import com.bezirk.middleware.messages.Event;

public class GetUserProfile extends Event
{
	/**
	 * topic
	 */
	public static final String topic = "get-user-profile";

	/* properties */
	
	private String user = null;	
	private String service = null;
	//private Context context = null;
	private List<ContextValue> context = null;
	
	/* constructors */
	
	public GetUserProfile() 
	{
		super(Stripe.REQUEST, topic);
		context = new ArrayList<ContextValue>();
	}
	
	public GetUserProfile(String topic) 
	{
		super(Stripe.REQUEST, topic);
	}
	
	/* getters and setters */
	
	// user
	public void setUser (String _v) { this.user = _v; }
	public String getUser () { return this.user; }
	
	// service
	public void setService (String _v) { this.service = _v; }
	public String getService () { return this.service; }
	
	// context
	//public void setContext (Context _v) { this.context = _v; }
	//public Context getContext () { return this.context; }
	public void setContext (List<ContextValue> _v) { this.context = _v; }
	public List<ContextValue> getContext () { return this.context; }
	public void addContext (ContextValue _v) { this.context.add(_v); }
	
	public static GetUserProfile deserialize(String json) {
		return Event.deserialize(json, GetUserProfile.class);
	}

}
