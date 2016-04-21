package com.bezirk.protocols.penguin.v01;

import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.protocols.callback.TrackableEvent;
import com.bezirk.protocols.presence.PresenceDetectorRole;
import com.bezirk.protocols.smartservice.SmartServiceRole;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This testcase verifies the PresenceDetectorRole,SmartServiceRole and UserProxyRole
 * by checking the event and stream topics.
 *
 * @author RHR8KOR
 */
public class ProtocolsTest {

    @Before
    public void setUp() throws Exception {

        TrackableEvent tEvents = new TrackableEvent(Flag.REQUEST, "TTS");

        String requestID = tEvents.getRequestID();

        tEvents = new TrackableEvent(Flag.REQUEST, "TTS", requestID);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {

        /**
         *  Junit Started for the PresenceDetectorRole
         *
         */

        PresenceDetectorRole pDRole = new PresenceDetectorRole();

        assertNull("Protocol Role Description is not null.", pDRole.getDescription());

        assertEquals("PresenceDetectorRole", pDRole.getProtocolName());

        assertNotNull("Protocol Event Topics is null", pDRole.getEventTopics());

        assertNull("Protocol Stream Topics is null", pDRole.getStreamTopics());


        /**
         *  Junit Started for SmartServiceRole
         *
         */

        SmartServiceRole sProle = new SmartServiceRole();

        assertNull("SmartServiceRole protocol Description is not null", sProle.getDescription());

        assertEquals("SmartServiceRole", sProle.getProtocolName());

        assertNotNull("SmartServiceRole protocol Events Topics is null", sProle.getEventTopics());

        assertNull("SmartServiceRole stream topic is null", sProle.getStreamTopics());


        /**
         *  Junit Started For the UserProxyRole
         *
         */


        UserProxyRole usrPRole = new UserProxyRole();

        assertNull("UserProxyRole protocol Description is null", usrPRole.getDescription());

        assertEquals("UserProxyRole", usrPRole.getProtocolName());

        assertNotNull("UserProxyRole protocol Event Topics is null", usrPRole.getEventTopics());

        assertNull("UserProxyRole Stream topic is null", usrPRole.getStreamTopics());


    }

}
