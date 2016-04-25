/**
 * Answer UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-Answer
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

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

    public Answer() {
        super(Flag.REPLY, topic);
    }
	
	
	/* Getter and setter methods */

    /**
     * Use instead of the generic UhuMessage.fromJSON()
     * @param json
     * @return Answer
     */
    public static Answer<?> deserialize(String json) {
        return Event.fromJSON(json, Answer.class);
    }

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
        final JsonObject jsonObj = new JsonParser().parse(json).getAsJsonObject();
        final JsonArray jsonObjAnswers = jsonObj.get("aip_answers").getAsJsonArray();
        final List<I> answers = new ArrayList<I>();

        for (int i = 0; i < jsonObjAnswers.size(); i++) {
            JsonObject jsonObjAnswer = jsonObjAnswers.get(i).getAsJsonObject();
            I answerItem = (I) gson.fromJson(jsonObjAnswer.toString(), iC);
            answers.add(answerItem);
        }

        answer.setAnswers(answers);
        return answer;
    }

    public String getId() {
        return aip_id;
    }

    public void setId(String id) {
        this.aip_id = id;
    }

    public List<A> getAnswers() {
        return aip_answers;
    }

    public void setAnswers(List<A> answers) {
        this.aip_answers = answers;
    }

    public String getSubTopic() {
        return aip_subTopic;
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

    public void setSubTopic(String subTopic) {
        this.aip_subTopic = subTopic;
    }
}
