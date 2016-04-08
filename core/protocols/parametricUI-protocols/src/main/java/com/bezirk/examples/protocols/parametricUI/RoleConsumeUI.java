/*
 * @author: Joao de Sousa (CR/RTC3-NA)
 */
package com.bezirk.examples.protocols.parametricUI;

import com.bezirk.api.messages.ProtocolRole;

/**
 * Role of a consumer service interacting with a parametric UI
 */
public class RoleConsumeUI extends ProtocolRole {
	private static final String evts[] =
		{ReplyUIchoices.TOPIC,
		 ReplyUIvalues.TOPIC };

	@Override
	public String getProtocolName() {
		return RoleConsumeUI.class.getSimpleName();
	}

	@Override
	public String getDescription() {
		return "Receiving user input on values and choices";
	}

	@Override
	public String[] getEventTopics() {
		return evts==null ?null:evts.clone();
	}

	@Override
	public String[] getStreamTopics() {
		return null;
	}
}
