package com.bezirk.actions;

import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class EventAction extends ZirkAction {
    private final String topic;
    private final String serializedEvent;

    public EventAction(ZirkId zirkId, Event event) {
        super(zirkId);

        if (event == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        topic = event.topic;
        serializedEvent = event.toJson();
    }

    public String getTopic() {
        return topic;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }
}
