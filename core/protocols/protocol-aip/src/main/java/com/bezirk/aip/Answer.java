/**
 * Answer UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014 
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-Answer
 */
package com.bezirk.aip;

import java.util.ArrayList;
import java.util.List;

import com.bezirk.api.messages.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Answer<A> extends Event {

	/**
	 * UhU topic: answer
	 */
	public static final String topic = "answer";


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
	
	
	/* Answer Properties */
	
	/**
	 * aip_answers
	 * 
	 * List of answers (of type AnswerItem).
	 * NOTE: <A> represents AnswerItem or a subclass that extends <A>.
	 */	
	private List<A> aip_answers = null;
	
	
	/* Constructor */
	
	public Answer () {
		super (Stripe.REPLY, topic);
	}
	
	
	/* Getter and setter methods */
	
	public void setId(String id) {
		this.aip_id = id;
	}
	public String getId() {
		return aip_id;
	}
	
	public void setAnswers(List<A> answers) {
		this.aip_answers = answers;
	}
	public List<A> getAnswers() {
		return aip_answers;
	}
	
	public void setSubTopic(String subTopic) {
		this.aip_subTopic = subTopic;
	}
	public String getSubTopic() {
		return aip_subTopic;
	}
	
	/**
	 * Use instead of the generic UhuMessage.deserialize()
	 * @param json
	 * @return Answer
	 */
	public static Answer<?> deserialize(String json) {
		return Event.deserialize(json, Answer.class);
	}
	
	/*
	public static <A> Answer<A> deserializeWithAnswerItem(String json, Class<A> aC) {
		Gson gson = new Gson();
		
		//******* UPDATE with the appropriate Answer class *******
		Answer<A> answer = gson.fromJson(json, Answer.class);
		//********************************************************
		
		// (1) Extract AnswerItems from JSON
		// (2) Deserialize each AnswerItem individually
		// (3) Add deserialized AnswerItems to Answer
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		JsonArray jsonObjAnswers = jsonObj.get("aip_answers").getAsJsonArray();
		List<A> answers = new ArrayList<A>();
		JsonObject jsonObjAnswer = null;
		A answerItem = null;
		for (int i=0; i < jsonObjAnswers.size(); i++) {
			jsonObjAnswer = jsonObjAnswers.get(i).getAsJsonObject();
			answerItem = (A) gson.fromJson(jsonObjAnswer.toString(), aC);
			answers.add(answerItem);
		}
		answer.setAnswers(answers);
		return answer;
	}
	*/
	
	/** TODO: test */
	public static <A, I> Answer<I> deserialize(String json, Class<A> aC, Class<I> iC) {
		Gson gson = new Gson();
		
		//******* UPDATE with the appropriate Answer class *******/
		@SuppressWarnings("unchecked") // don't warn on this generic cast
		Answer<I> answer = (Answer<I>) gson.fromJson(json, aC);
		//********************************************************/
		
		// (1) Extract DigestItems from JSON
		// (2) Deserialize each DigestItem individually
		// (3) Add deserialized DigestItems to Digest
		JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
		JsonArray jsonObjAnswers = jsonObj.get("aip_answers").getAsJsonArray();
		List<I> answers = new ArrayList<I>();
		JsonObject jsonObjAnswer = null;
		I answerItem = null;
		for (int i=0; i < jsonObjAnswers.size(); i++) {
			jsonObjAnswer = jsonObjAnswers.get(i).getAsJsonObject();
			answerItem = (I) gson.fromJson(jsonObjAnswer.toString(), iC);
			answers.add(answerItem);
		}
		answer.setAnswers(answers);
		return answer;
	}
}
