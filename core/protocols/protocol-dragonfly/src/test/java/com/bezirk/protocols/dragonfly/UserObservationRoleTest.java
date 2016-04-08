package com.bezirk.protocols.dragonfly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies the UserObservationRole by checking the eventtopic list.
 * 
 * @author AJC6KOR
 *
 */
public class UserObservationRoleTest {

	@Test
	public void test() {
		
	UserObservationRole usrObsrvRole = new UserObservationRole();
	assertEquals("Protocol Name is not equal to UserObservationRole.",UserObservationRole.class.getSimpleName(),usrObsrvRole.getProtocolName());
	
	assertNull("Description is present for UserObservationRole.",usrObsrvRole.getDescription());
	assertNull("StreamTopics are present in UserObservationRole.",usrObsrvRole.getStreamTopics());
	List<String> evntTopicList = Arrays.asList(usrObsrvRole.getEventTopics());
	assertTrue("EnvironmentObservation.topic not present in the event list of UserObservationRole.",evntTopicList.contains(UserObservation.topic));
	}

}
