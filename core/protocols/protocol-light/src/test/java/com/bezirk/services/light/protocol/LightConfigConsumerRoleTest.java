package com.bezirk.services.light.protocol;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This testcase verifies the LightConfigConsumerRole by checking the event and stream topics list.
 */
public class LightConfigConsumerRoleTest {

    @Test
    public void test() {
        final String evts[] =
                {ResponseActiveLights.TOPIC, ResponseLightLocation.TOPIC, ResponseKing.TOPIC, MakeKing.TOPIC, ResponsePolicy.TOPIC};
        LightConfigConsumerRole lightCfgConsumerRole = new LightConfigConsumerRole();

        assertArrayEquals("Unable to retrieve event topics from LightConfigConsumerRole.", evts, lightCfgConsumerRole.getEventTopics());

        assertNull("Stream topics are found for LightConfigConsumerRole.", lightCfgConsumerRole.getStreamTopics());

        assertEquals("Protocol description is different for the LightConfigConsumerRole.", LightConfigConsumerRole.description, lightCfgConsumerRole.getDescription());

        assertEquals("Protocol Name is different for the LightConfigConsumerRole.", LightConfigConsumerRole.class.getSimpleName(), lightCfgConsumerRole.getProtocolName());

    }

}
