package com.bezirk.actions;

import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class EventAction extends ZirkAction {
    private final String serializedEvent;
    private final String messageId;
    private final String eventName;

    public EventAction(ZirkId zirkId, Event event) {
        super(zirkId);

        if (event == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        eventName = event.getClass().getName();
        serializedEvent = event.toJson();
        this.messageId = event.toString();
    }

    public EventAction(ZirkId zirkId, String serializedEvent, String msgId, String eventName) {
        super(zirkId);

        if (serializedEvent == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        this.serializedEvent = serializedEvent;
        this.messageId = msgId;
        this.eventName = eventName;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getEventName() {
        return eventName;
    }
}
