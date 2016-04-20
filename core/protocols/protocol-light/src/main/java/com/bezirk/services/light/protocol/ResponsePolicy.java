
package com.bezirk.services.light.protocol;

import com.bezirk.middleware.messages.Event;

public class ResponsePolicy extends Event{
	public static final String TOPIC = ResponsePolicy.class.getSimpleName();
	private HueVocab.Policy policy;
	private String location;
	private Integer sensitivityToPresence = 40;
	private String king="No-King";

	/**
	 * This constructor is used for policy FCFS
	 * @param location
	 * @param policy
	 */
	public ResponsePolicy(String location, HueVocab.Policy policy) {
		super(Stripe.REPLY, TOPIC);
		this.location = location;
		this.policy = policy;
	}
	
	

	public Integer getSensitivityToPresence() {
		return sensitivityToPresence;
	}



	public void setSensitivityToPresence(Integer sensitivityToPresence) {
		this.sensitivityToPresence = sensitivityToPresence;
	}



	/**
	 * This constructor is used for policy KOH
	 * @param location
	 * @param policy
	 * @param king
	 */
	public ResponsePolicy(String location, HueVocab.Policy policy, String king) {
		super(Stripe.REPLY, TOPIC);
		this.location = location;
		this.policy = policy;
		this.king = king;
	}

	/**
	 * @return the king
	 */
	public String getKing() {
		return king;
	}

	/**
	 * @param king the king to set
	 */
	public void setKing(String king) {
		this.king = king;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the policy
	 */
	public HueVocab.Policy getPolicy() {
		return policy;
	}

	/**
	 * @param policy the policy to set
	 */
	public void setPolicy(HueVocab.Policy policy) {
		this.policy = policy;
	}

}
