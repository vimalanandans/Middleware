/**
 * RelatedQuestions UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-RelatedQuestions 
 */
package com.bezirk.aip;

import java.util.List;

import com.bezirk.api.messages.Event;

public class RelatedQuestions extends Event {

	/**
	 * UhU topic: related-questions
	 */
	public static final String topic = "related-questions";
	
	
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
	
	
	/* RelatedQuestions Properties */
	
	/**
	 * aip_relatedQuestions
	 * 
	 * List of related questions.
	 */	
	private List<String> aip_relatedQuestions = null;
		
	
	/* Constructor */
	
	public RelatedQuestions () {
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
	
	public void setRelatedQuestions(List<String> relatedQuestions) {
		this.aip_relatedQuestions = relatedQuestions;
	}
	public List<String> getRelatedQuestions() {
		return aip_relatedQuestions;
	}
	
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return RelatedQuestions
	 */
	public static RelatedQuestions deserialize(String json) {
		return Event.deserialize(json, RelatedQuestions.class);
	}	
}
