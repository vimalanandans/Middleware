package com.bezirk.test;

import com.bezirk.middleware.messages.IdentifiedEvent;

/**
 * @author Rishabh Gulati
 */
public class UpdateAcceptedEvent extends IdentifiedEvent {
    private final String testField;

    public UpdateAcceptedEvent(String testField) {
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }
}
