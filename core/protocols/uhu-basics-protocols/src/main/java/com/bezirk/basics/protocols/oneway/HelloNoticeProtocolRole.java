package com.bezirk.UhUbasics.protocols.oneway;

import com.bezirk.Proxy.ProtocolRole;

public class HelloNoticeProtocolRole implements ProtocolRole {
	
	private String ROLE = this.getClass().getSimpleName();
	
	private static final String[] EVENTS = {
		HelloNoticeEvent.TOPIC
	};

	/** Unique identifier for our protocol role */
	@Override
	public String getProtocol() {
		return ROLE;
	}

	/** The set of Events specified by this protocol role */
	@Override
	public String[] getEvents() {
		return EVENTS;
	}

	@Override
	public String[] getStreams() {
		return null;
	}

}
