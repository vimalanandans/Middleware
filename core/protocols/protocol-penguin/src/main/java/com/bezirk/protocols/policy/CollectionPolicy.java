package com.bezirk.protocols.policy;


public class CollectionPolicy{
	
	/**
	 * topic
	 */
	public static final String topic = CollectionPolicy.class.getSimpleName();
	
	private String data;
	private boolean policyValue;

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public boolean getPolicyValue() {
		return policyValue;
	}
	public void setPolicyValue(boolean policyValue) {
		this.policyValue = policyValue;
	}

	
}
