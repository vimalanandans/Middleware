/**
 * UserProxyRole
 * 
 * @author Cory Henson
 * @modified 09/16/2014 
 */
package com.bezirk.protocols.penguin.v01;

import com.bezirk.protocols.dragonfly.Observation;
import com.bezirk.protocols.dragonfly.UserObservation;
import com.bezirk.protocols.dragonfly.test.ObservationTest;
import com.bezirk.protocols.penguin.v01.test.GetPreferenceTest;
import com.bezirk.protocols.penguin.v01.test.GetUserProfileTest;
import com.bezirk.protocols.specific.userproxy.ClearData;
import com.bezirk.api.messages.ProtocolRole;

public class UserProxyRole extends ProtocolRole 
{
	private String role = this.getClass().getSimpleName();

	private static final String[] events = {
		GetPreference.topic,
		GetPreferenceTest.topic,
		Observation.topic,
		ObservationTest.topic,
		GetUserProfile.topic,
		GetUserProfileTest.topic,
		InitUserProxyUI.topic,
		InitUserProxyRemoteUI.topic,
		UserObservation.topic,
		ClearData.topic
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