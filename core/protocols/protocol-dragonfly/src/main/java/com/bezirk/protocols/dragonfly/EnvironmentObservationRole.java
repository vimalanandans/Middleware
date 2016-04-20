/**
 * EnvironmentObservationRole
 *
 * @author Cory Henson
 * @modified 10/27/2014 
 */
package com.bezirk.protocols.dragonfly;

import com.bezirk.middleware.messages.ProtocolRole;

public class EnvironmentObservationRole extends ProtocolRole
{
	private String role = this.getClass().getSimpleName();

	private static final String[] events = {
		EnvironmentObservation.topic
	};

	@Override
	public String getProtocolName() { return role; }

	@Override
	public String getDescription() { return null; }

	@Override
	public String[] getEventTopics() { return events==null ?null:events.clone(); }

	@Override
	public String[] getStreamTopics() { return null; }
}
