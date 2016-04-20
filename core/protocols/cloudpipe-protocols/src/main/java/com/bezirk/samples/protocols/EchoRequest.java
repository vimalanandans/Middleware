package com.bezirk.samples.protocols;

import com.bezirk.middleware.messages.Event;

public class EchoRequest extends Event {
	
	private String text = null;

	public EchoRequest() {
		super(Stripe.REQUEST, EchoRequest.class.getSimpleName());
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public static EchoRequest deserialize(String serializedEvent) {
		return deserialize(serializedEvent, EchoRequest.class);
	}
}
