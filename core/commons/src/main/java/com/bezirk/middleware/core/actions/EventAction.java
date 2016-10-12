package com.bezirk.middleware.core.actions;

import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

public abstract class EventAction extends ZirkAction {

    private static final long serialVersionUID = 7767357774933115392L;
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
