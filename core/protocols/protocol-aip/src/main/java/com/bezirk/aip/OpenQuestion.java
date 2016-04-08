/**
 * OpenQuestion UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-OpenQuestion 
 */
package com.bezirk.aip;

import com.bezirk.api.messages.Event;

public class OpenQuestion extends Question {

	/**
	 * UhU topic: open-question
	 */
	public static final String topic = "open-question";
	
	
	/* OpenQuestion Properties */
	
	/**
	 * aip_until
	 * 
	 * Value should adhere to xsd:dateTime format.
	 */
	public String aip_until = null;
	
	
	/* Constructor */
	
	public OpenQuestion() {
		super(topic);
	}
	
	
	/* Getter and setter methods */
	
	public void setUntil(String until) {
		this.aip_until = until;
	}
	public String getUntil() {
		return aip_until;
	}

	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return OpenQuestion
	 */
	public static OpenQuestion deserialize(String json) {
		return Event.deserialize(json, OpenQuestion.class);
	}	
}
