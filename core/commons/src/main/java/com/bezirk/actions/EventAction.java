package com.bezirk.actions;

import com.bezirk.middleware.messages.Event;
import com.bezirk.proxy.api.impl.ZirkId;

public abstract class EventAction extends ZirkAction {
    private final String topic;
    private final String serializedEvent;
    private final String messageId;

    public EventAction(ZirkId zirkId, Event event) {
        super(zirkId);

        if (event == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        topic = event.topic;
        serializedEvent = event.toJson();
        this.messageId = event.msgId;
    }

    public EventAction(ZirkId zirkId, String topic, String serializedEvent, String msgId) {
        super(zirkId);

        if (topic == null || serializedEvent == null) {
            throw new IllegalArgumentException("event must be set to a non-null value");
        }

        this.topic = topic;
        this.serializedEvent = serializedEvent;
        this.messageId = msgId;
    }

    public String getTopic() {
        return topic;
    }

    public String getSerializedEvent() {
        return serializedEvent;
    }

    public String getMessageId() { return messageId; }
}
