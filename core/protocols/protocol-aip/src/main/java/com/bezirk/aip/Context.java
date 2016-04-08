/**
 * Context
 *
 * Context specifies information related to the circumstances
 * in which the question was asked.
 *
 * @author Cory Henson
 * @modified 06/09/2014 
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-Context
 */
package com.bezirk.aip;

public class Context {

	/* Context Properties */
	
	/**
	 * aip_location
	 * 
	 */
	private String aip_location = null;
	
	/**
	 * aip_dateTime
	 * 
	 */
	private String aip_dateTime = null;
	
	/**
	 * aip_user
	 * 
	 */
	private String aip_user = null;
	
	
	/* Getter and setter methods */
	
	public void setLocation(String location) {
		this.aip_location = location;
	}
	public String getLocation() {
		return this.aip_location;
	}
	
	public void setDateTime(String dateTime) {
		this.aip_dateTime = dateTime;
	}
	public String getDateTime() {
		return this.aip_dateTime;
	}
	
	public void setUser(String user) {
		this.aip_user = user;
	}
	public String getUser() {
		return this.aip_user;
	}
}

