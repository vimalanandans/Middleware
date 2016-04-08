package com.bezirk.services.light.protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import com.bezirk.api.messages.Event;

public class TestLocationAndLightSetUpResponse extends Event{
	
	public static final String TOPIC = TestLocationAndLightSetUpResponse.class.getSimpleName();
	
	private Map<String, HashSet<String>> locToIds = new HashMap<String, HashSet<String>>();
	private HashMap<String, LinkedHashSet<String>> locToUsers = new HashMap<String, LinkedHashSet<String>>();

	public TestLocationAndLightSetUpResponse() {
		super(Stripe.REPLY, TOPIC);
		// TODO Auto-generated constructor stub
	}

	public Map<String, HashSet<String>> getLocToIds() {
		return locToIds;
	}

	public void setLocToIds(Map<String, HashSet<String>> locToIds) {
		this.locToIds = locToIds;
	}

	public HashMap<String, LinkedHashSet<String>> getLocToUsers() {
		return locToUsers;
	}

	public void setLocToUsers(HashMap<String, LinkedHashSet<String>> locToUsers) {
		this.locToUsers = locToUsers;
	}

}
