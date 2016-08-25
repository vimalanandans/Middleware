package com.bezirk.middleware.core;

import com.bezirk.middleware.messages.IdentifiedEvent;

public class UpdateAcceptedEvent extends IdentifiedEvent {

    private final String sender;
    private final String testField;

    public UpdateAcceptedEvent(String sender, String testField) {
        this.sender = sender;
        this.testField = testField;
    }

    public String getTestField() {
        return testField;
    }

    @Override
    public String toString() {
        return "UpdateAcceptedEvent{" +
                "sender='" + sender + '\'' +
                ", testField='" + testField + '\'' +
                "} ";
    }
}
