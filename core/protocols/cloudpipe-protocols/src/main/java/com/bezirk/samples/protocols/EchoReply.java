package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.Event;

public class EchoReply extends Event {

	private String text = null;

	public EchoReply() {
		super(Stripe.REPLY, EchoReply.class.getSimpleName());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static EchoReply deserialize(String serializedEvent) {
		return deserialize(serializedEvent, EchoReply.class);
	}

}
