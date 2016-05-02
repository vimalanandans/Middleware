/**
 * @author Cory Henson
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
     * Use instead of the generic Message.fromJson()
     * @param json
     * @return IncompleteQuestion
     */
    public static IncompleteQuestion deserialize(String json) {
        return Event.fromJson(json, IncompleteQuestion.class);
    }
}
