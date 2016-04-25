/**
 * InspireMe UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-InspireMe
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;

public class InspireMe extends Event {

    /**
     * UhU topic: inspire-me
     */
    public static final String topic = "inspire-me";

	
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
	
	
	/* InspireMe Properties */

    /**
     * aip_maxInspirations
     *
     * Maximum number of inspirations allowed in reply.
     * [Default value: 10]
     */
    private int aip_maxInspirations = 10;
	
	
	/* Context Property */

    /**
     * aip_context
     *
     * Context specifies information related to the circumstances
     * in which the question was asked.
     * [OPTIONAL]
     */
    private Context aip_context = null;
	
	
	/* Constructor */

    public InspireMe() {
        super(Flag.REQUEST, topic);
    }

	
	/* Getter and setter methods */

    /**
     * Use instead of the generic UhuMessage.fromJson()
     * @param json
     * @return Question
     */
    public static Question deserialize(String json) {
        return Event.fromJson(json, Question.class);
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

    public int getMaxInspirations() {
        return aip_maxInspirations;
    }

    public void setMaxInspirations(int maxInspirations) {
        this.aip_maxInspirations = maxInspirations;
    }

    public Context getContext() {
        return aip_context;
    }

    public void setContext(Context context) {
        this.aip_context = context;
    }
}