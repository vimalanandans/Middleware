/**
 * DisambiguationQuestions UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014 
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-DisambiguationQuestions
 */
package com.bezirk.aip;

import java.util.List;

import com.bezirk.middleware.messages.Event;

public class DisambiguationQuestions extends Event {

	/**
	 * UhU topic: disambiguation-questions
	 */
	public static final String topic = "disambiguation-questions";
	
	
	/* Core Properties */
	
	/**
	 * aip_version
	 * 
	 * AiProtocol version: v0.1
	 */
	public final String aip_version = "v0.1";
	
	/**
	 * aip_id
	 * 
	 * ID for the question. Used to map an answer with a question.
	 */
	private String aip_id = null;

	/**
	 * aip_subtopic
	 * 
	 * Subtopic used by receiving services to know how to cast the event.
	 */
	private String aip_subTopic = null;	
	
	
	/* DisambiguationQuestions Properties */
	
	/**
	 * aip_disambiguationQuestions
	 * 
	 * List of disambiguation questions.
	 */	
	private List<String> aip_disambiguationQuestions = null;
		
	
	/* Constructor */
	
	public DisambiguationQuestions () {
		super (Stripe.REPLY, topic);
	}
	
	
	/* Getter and setter methods */
	
	public void setId(String id) {
		this.aip_id = id;
	}
	public String getId() {
		return aip_id;
	}
	
	public void setSubTopic(String subTopic) {
		this.aip_subTopic = subTopic;
	}
	public String getSubTopic() {
		return aip_subTopic;
	}
	
	public void setDisambiguationQuestions(List<String> disambiguationQuestions) {
		this.aip_disambiguationQuestions = disambiguationQuestions;
	}
	public List<String> getDisambiguationQuestions() {
		return aip_disambiguationQuestions;
	}
	
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return DisambiguationQuestion
	 */
	public static DisambiguationQuestions deserialize(String json) {
		return Event.deserialize(json, DisambiguationQuestions.class);
	}	
}
