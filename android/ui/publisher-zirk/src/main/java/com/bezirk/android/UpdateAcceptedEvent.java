package com.bezirk.android;

import com.bezirk.middleware.messages.Event;

/**
 * @author Rishabh Gulati
 */
public class UpdateAcceptedEvent extends Event {
    public static final String TOPIC = UpdateAcceptedEvent.class.getCanonicalName();
    private String testField;

    public UpdateAcceptedEvent(String testField) {
        super(Flag.REPLY, TOPIC);
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }
}
