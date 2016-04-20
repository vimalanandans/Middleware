/**
 * Smart Service Role
 * 
 * @author Cory Henson
 * @modified 09/16/2014 
 */
package com.bezirk.protocols.smartservice;

import com.bezirk.protocols.dragonfly.Observation;
import com.bezirk.protocols.dragonfly.UserObservation;
import com.bezirk.middleware.messages.ProtocolRole;

public class SmartServiceRole extends ProtocolRole 
{
	private String role = this.getClass().getSimpleName();

	private static final String[] events = {
		//Preference.topic,
		Observation.topic,
		UserObservation.topic
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