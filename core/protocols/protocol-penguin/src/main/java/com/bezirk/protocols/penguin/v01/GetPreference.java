/**
 * GetPreference
 *
 * @author Cory Henson
 * @modified 09/16/2014 
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.protocols.context.Context;
import com.bezirk.api.messages.Event;

public class GetPreference extends Event 
{
	/**
	 * topic
	 */
	public static final String topic = "get-preference";
	
	/* properties */
	
	String type = null; 		
	String user = null; 		
	Context context = null;
	String service = null;
	
	/*
	 * @Himadri Sikhar Khargharia
	 * This overloaded constructor is added for allowing GetPreferenceTest to extend GetPreference
	 * 
	 */
	public GetPreference (String topic) 
	{
		super(Stripe.REQUEST, topic);
	}
	
	public GetPreference () 
	{
		super(Stripe.REQUEST, topic);
	}
	
	public GetPreference ( 	String _user, 
							String _type, 
							Context _context ) 
	{
		super(Stripe.REQUEST, topic);
		this.setUser(_user);
		this.setType(_type);
		this.setContext(_context);
	}
	
	
	/* getters and setters */
	
	// type
	public void setType (String _v) { this.type = _v; }
	public String getType () { return this.type; }
	
	// user
	public void setUser (String _v) { this.user = _v; }
	public String getUser () { return this.user; }
	
	// context
	public void setContext (Context _v) { this.context = _v; }
	public Context getContext () { return this.context; }
		
	// service
	public void setService (String _v) { this.service = _v; }
	public String getService () { return this.service; }
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return GetPreference
	 */
	public static GetPreference deserialize(String json) {
		return Event.deserialize(json, GetPreference.class);
	}
}