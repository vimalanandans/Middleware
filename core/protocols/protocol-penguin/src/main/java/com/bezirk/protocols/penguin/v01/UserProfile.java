package com.bezirk.protocols.penguin.v01;

import java.util.ArrayList;
import java.util.List;

import com.bezirk.api.messages.Event;

public class UserProfile extends Event
{
	/**
	 * topic
	 */
	public static final String topic = "user-profile";
	
	/* properties */
	
	private String id = null;
	private List<ConditionalProfileSubset> hasConditionalProfileSubset = null;
	private List<DefaultProfileSubset> hasDefaultProfileSubset = null;

	/* constructors */
	
	public UserProfile() 
	{
		super(Stripe.REPLY, topic);
		this.hasConditionalProfileSubset = new ArrayList<ConditionalProfileSubset>();
		this.hasDefaultProfileSubset = new ArrayList<DefaultProfileSubset>();
	} 
	
	/* getters and setters */
	
	// id
	public void setId (String _v) { this.id = _v; }
	public String getId () { return this.id; }
	
	// hasConditionalProfileSubset
	public void setConditionalProfileSubset (List<ConditionalProfileSubset> _v) { this.hasConditionalProfileSubset = _v; }
	public List<ConditionalProfileSubset> getConditionalProfileSubset () { return this.hasConditionalProfileSubset; }
	public void addConditionalProfileSubset (ConditionalProfileSubset _v) { this.hasConditionalProfileSubset.add(_v); }
	
	// hasDefaultProfileSubset
	public void setDefaultProfileSubset (List<DefaultProfileSubset> _v) { this.hasDefaultProfileSubset = _v; }
	public List<DefaultProfileSubset> getDefaultProfileSubset () { return this.hasDefaultProfileSubset; }
	public void addDefaultProfileSubset (DefaultProfileSubset _v) { this.hasDefaultProfileSubset.add(_v); }

	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return Profile
	 */
	public static UserProfile deserialize(String json) {
		return Event.deserialize(json, UserProfile.class);
	}

}
