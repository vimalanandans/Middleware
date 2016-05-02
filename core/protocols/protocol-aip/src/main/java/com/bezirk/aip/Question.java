/**
 * @author Cory Henson
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;

import java.util.List;

public class Question extends Event {

    /**
     * UhU topic: question
     */
    public static final String topic = "question";

	
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
     * Subtopic is used by receiving services to know how to cast the event.
     */
    private String aip_subTopic = null;
	
	
	/* Question Properties */

    /**
     * aip_question
     *
     * Question being asked, in plain text.
     */
    private String aip_question = null;

    /**
     * aip_answerFormat
     *
     * Data format of answer.
     */
    private List<String> aip_answerFormat = null;

    /**
     * aip_maxAnswers
     *
     * Maximum number of answers allowed in reply to the question.
     * [Default value: 10]
     */
    private int aip_maxAnswers = 10;

    /**
     * aip_about
     *
     * About specifies information related to the semantics of the question,
     * or what the question is about.
     */
    private List<String> aip_about = null;

    /**
     * aip_response
     *
     * List of allowed response messages.
     * Allowed values include: answer, single-answer, digest, disambiguation-questions, related-questions
     * If no value is provided then all are allowed by default.
     */
    private List<String> aip_response = null;
	
	
	/* Context Property */

    /**
     * aip_context
     *
     * Context specifies information related to the circumstances
     * in which the question was asked.
     */
    private Context aip_context = null;
	

	/* Constructor */

    public Question() {
        super(Flag.REQUEST, topic);
    }

    protected Question(String _topic) {
        super(Flag.REQUEST, _topic);
    }
	
	
	/* Getter and setter methods */

    /**
     * Use instead of the generic Message.fromJson()
     * @param json
     * @return Question
     */
    public static Question deserialize(String json) {
        return Event.fromJson(json, Question.class);
    }

    public String getQuestion() {
        return aip_question;
    }

    public void setQuestion(String question) {
        this.aip_question = question;
    }

    public String getSubTopic() {
        return aip_subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.aip_subTopic = subTopic;
    }

    public String getId() {
        return aip_id;
    }

    public void setId(String id) {
        this.aip_id = id;
    }

    public List<String> getAnswerFormat() {
        return aip_answerFormat;
    }

    public void setAnswerFormat(List<String> answerFormat) {
        this.aip_answerFormat = answerFormat;
    }

    public List<String> getResponse() {
        return aip_response;
    }

    public void setResponse(List<String> response) {
        this.aip_response = response;
    }

    public int getMaxAnswers() {
        return aip_maxAnswers;
    }

    public void setMaxAnswers(int maxAnswers) {
        this.aip_maxAnswers = maxAnswers;
    }

    public List<String> getAbout() {
        return aip_about;
    }

    public void setAbout(List<String> about) {
        this.aip_about = about;
    }

    public Context getContext() {
        return aip_context;
    }

    public void setContext(Context context) {
        this.aip_context = context;
    }
}
