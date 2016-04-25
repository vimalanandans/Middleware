package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.Event;

public class EchoReply extends Event {

    private String text = null;

    public EchoReply() {
        super(Flag.REPLY, EchoReply.class.getSimpleName());
    }

    public static EchoReply deserialize(String serializedEvent) {
        return fromJson(serializedEvent, EchoReply.class);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
