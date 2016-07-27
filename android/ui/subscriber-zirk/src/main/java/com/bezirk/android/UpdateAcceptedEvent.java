package com.bezirk.android;

import com.bezirk.middleware.messages.Event;

/**
 * @author Rishabh Gulati
 */
public class UpdateAcceptedEvent extends Event {
    private final String testField;

    public UpdateAcceptedEvent(String testField) {
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }
}
