package com.bezirk.protocols.dragonfly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 *	 This testcase verifies the EnvironmentObservation protocol by description, event topics and stream topics.
 * 
 * @author AJC6KOR
 *
 */
public class EnvironmentObservationRoleTest {


	@Test
	public void test() {

		EnvironmentObservationRole envObsrvRole = new EnvironmentObservationRole();
		assertEquals("Protocol Name is not equal to EnvironmentObservationRole.",EnvironmentObservationRole.class.getSimpleName(),envObsrvRole.getProtocolName());
		
		assertNull("Description is present for EnvironmnetObservationRole.",envObsrvRole.getDescription());
		assertNull("StreamTopics are present in EnvironmnetObservationRole.",envObsrvRole.getStreamTopics());
		List<String> evntTopicList = Arrays.asList(envObsrvRole.getEventTopics());
		assertTrue("EnvironmentObservation.topic not present in the event list of Environmnet Observation Role.",evntTopicList.contains(EnvironmentObservation.topic));
		
	}

}
