/**
 * IncompleteQuestion UhU Event
 *
 * @author Cory Henson
 * @modified 06/09/2014
 * @specification https://fe0vmc0345.de.bosch.com/wiki/pages/viewpage.action?pageId=24117500#AiPQ&ASpecification-IncompleteQuestion
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;

public class IncompleteQuestion extends Question {

    /**
     * UhU topic: question
     */
    public static final String topic = "incomplete-question";

	
	/* Constructor */

    public IncompleteQuestion() {
        super(topic);
    }


    /**
     * Use instead of the generic UhuMessage.fromJson()
     * @param json
     * @return IncompleteQuestion
     */
    public static IncompleteQuestion deserialize(String json) {
        return Event.fromJson(json, IncompleteQuestion.class);
    }
}
