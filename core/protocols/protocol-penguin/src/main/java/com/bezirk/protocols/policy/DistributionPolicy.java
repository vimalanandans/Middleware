package com.bezirk.protocols.policy;


public class DistributionPolicy {

	/**
	 * topic
	 */
	public static final String topic = DistributionPolicy.class.getSimpleName();


	private String service;
	private DistributionPolicyValue distributionPolicyValue;

	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public DistributionPolicyValue getDistributionPolicyValue() {
		return distributionPolicyValue;
	}
	
	public void setDistributionPolicyValue(
			DistributionPolicyValue distributionPolicyValue) {
		this.distributionPolicyValue = distributionPolicyValue;
	}
	
	
}
