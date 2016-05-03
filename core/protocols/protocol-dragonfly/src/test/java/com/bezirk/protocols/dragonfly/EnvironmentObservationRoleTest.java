package com.bezirk.protocols.dragonfly;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the EnvironmentObservation protocol by description, event topics and stream topics.
 *
 * @author AJC6KOR
 */
public class EnvironmentObservationRoleTest {


    @Test
    public void test() {

        EnvironmentObservationRole envObsrvRole = new EnvironmentObservationRole();
        assertEquals("Protocol Name is not equal to EnvironmentObservationRole.", EnvironmentObservationRole.class.getSimpleName(), envObsrvRole.getRoleName());

        assertNull("Description is present for EnvironmnetObservationRole.", envObsrvRole.getDescription());
        assertNull("StreamTopics are present in EnvironmnetObservationRole.", envObsrvRole.getStreamTopics());
        List<String> evntTopicList = Arrays.asList(envObsrvRole.getEventTopics());
        assertTrue("EnvironmentObservation.topic not present in the event list of Environmnet Observation Role.", evntTopicList.contains(EnvironmentObservation.topic));

    }

}
