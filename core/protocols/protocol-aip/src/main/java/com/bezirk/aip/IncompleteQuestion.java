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

    public static IncompleteQuestion fromJson(String json) {
        return Event.fromJson(json, IncompleteQuestion.class);
    }
}
