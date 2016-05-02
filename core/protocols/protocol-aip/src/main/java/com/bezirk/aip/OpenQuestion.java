/**
 * @author Cory Henson
 */
package com.bezirk.aip;

import com.bezirk.middleware.messages.Event;

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

    /**
     * Use instead of the generic Message.fromJson()
     * @param json
     * @return OpenQuestion
     */
    public static OpenQuestion deserialize(String json) {
        return Event.fromJson(json, OpenQuestion.class);
    }

    public String getUntil() {
        return aip_until;
    }

    public void setUntil(String until) {
        this.aip_until = until;
    }
}
