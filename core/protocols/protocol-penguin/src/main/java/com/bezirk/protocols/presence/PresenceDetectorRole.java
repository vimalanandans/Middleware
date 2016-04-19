/**
 * PresenceDetectorRole
 * 
 * @author Cory Henson
 * @modified 09/16/2014 
 */
package com.bezirk.protocols.presence;

import com.bezirk.middleware.messages.ProtocolRole;

public class PresenceDetectorRole extends ProtocolRole 
{	
	private String role = this.getClass().getSimpleName();
	
	private static final String[] events = {};

	@Override
	public String getProtocolName() { return role; }

	@Override
	public String getDescription() { return null; }

	@Override
	public String[] getEventTopics() { return events==null ?null:events.clone(); }

	@Override
	public String[] getStreamTopics() { return null; }
}