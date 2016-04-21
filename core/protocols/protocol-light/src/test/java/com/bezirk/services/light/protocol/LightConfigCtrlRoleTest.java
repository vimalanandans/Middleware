package com.bezirk.services.light.protocol;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This testcase verifies the LightConfigCtrlRole by checking the event topics and stream topics.
 *
 * @author RHR8KOR
 */
public class LightConfigCtrlRoleTest {


    @Test
    public void test() {

        /**
         * Junit For The LightConfigCtrlRole.java
         */

        String description = "This the protocol role that the light service must subscribe to";

        LightConfigCtrlRole lightCtrlR = new LightConfigCtrlRole();

        assertEquals(description, lightCtrlR.getDescription());
        //assertEquals(17, lightCtrlR.getEventTopics().length);
        assertEquals("LightConfigCtrlRole", lightCtrlR.getProtocolName());
        assertNull("LightConfigCtrlRole stream topic is not null", lightCtrlR.getStreamTopics());

        /**
         * Junit test for LightConfigConsumerRole.java
         */


        LightConfigConsumerRole loghtCtrlCR = new LightConfigConsumerRole();

        description = "This the protocol role that the light ui must subscribe to";

        assertEquals(description, loghtCtrlCR.getDescription());
        assertEquals("LightConfigConsumerRole", loghtCtrlCR.getProtocolName());
        assertEquals(5, loghtCtrlCR.getEventTopics().length);
        assertNull("LightConfigConsumerRole stream topic is not null", loghtCtrlCR.getStreamTopics());

    }

}
