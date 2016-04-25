package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.Event;

public class EchoRequest extends Event {

    private String text = null;

    public EchoRequest() {
        super(Flag.REQUEST, EchoRequest.class.getSimpleName());
    }

    public static EchoRequest deserialize(String serializedEvent) {
        return fromJson(serializedEvent, EchoRequest.class);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
