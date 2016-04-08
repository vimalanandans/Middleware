/**
 * UserProxyRole
 * 
 * @author Cory Henson
 * @modified 10/28/2014 
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.api.messages.ProtocolRole;

public class QueryUserProxyRole extends ProtocolRole
{
	private String role = this.getClass().getSimpleName();

	private static final String[] events = {
		//Preference.topic,
		UserProfile.topic
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
