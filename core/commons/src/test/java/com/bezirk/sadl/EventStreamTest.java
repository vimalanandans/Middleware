package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the event and stream topics registered with SadlManager using zirk Ids.
 *
 * @author AJC6KOR
 */
public class EventStreamTest {

    private final static Logger log = LoggerFactory
            .getLogger(EventStreamTest.class);
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static final BezirkZirkId uhuServiceAId = new BezirkZirkId(
            "ServiceA");
    private static final BezirkZirkId uhuServiceBId = new BezirkZirkId(
            "ServiceB");
    private static final BezirkZirkId uhuServiceCId = new BezirkZirkId(
            "ServiceC");
    private static final MockProtocols mockService = new MockProtocols();
    private static final ProtocolRole streamlessPRole = mockService.new StreamlessProtocol();
    private static final ProtocolRole eventlessPRole = mockService.new EventlessProtocol();
    private static final SubscribedRole subscribedStreamlessPRole = new SubscribedRole(
            streamlessPRole);
    private static final SubscribedRole subscribedEventlessPRole = new SubscribedRole(
            eventlessPRole);
    private static final Location reception = new Location("OFFICE1", "BLOCK1",
            "RECEPTION");
    private static BezirkSadlManager bezirkSadlManager = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up EventStream TestCase *****");
        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;


    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        mockUtility.destroyTestSetUp();
        log.info("***** Shutting down EventStream Testcase *****");

    }

    @Test
    public void testEventsAndStreams() {

		
		/*
		 * ServiceA is registered and subscribed to EventlessProtocolRole and StreamlessProtocolRole. ServiceA is located at reception.
		 * ServiceB is registered and subscribed to StreamlessProtocolRole with defaultlocation.
		 * ServiceC is registered and subscribed to EventlessProtocolRole with defaultlocation.
		 * */
        bezirkSadlManager.registerService(uhuServiceAId);
        bezirkSadlManager.subscribeService(uhuServiceAId, subscribedStreamlessPRole);
        bezirkSadlManager.subscribeService(uhuServiceAId, subscribedEventlessPRole);
        bezirkSadlManager.setLocation(uhuServiceAId, reception);
        bezirkSadlManager.registerService(uhuServiceBId);
        bezirkSadlManager.subscribeService(uhuServiceBId, subscribedStreamlessPRole);
        bezirkSadlManager.registerService(uhuServiceCId);
        bezirkSadlManager.subscribeService(uhuServiceCId, subscribedEventlessPRole);


        log.info("********* testing IsStreamTopicRegistered ********");
        testIsStreamTopicRegistered();
        log.info("********* testing CheckUnicastEvent ********");
        testCheckUnicastEvent();
        log.info("********* testing CheckMulticastEvent ********");
        testCheckMulticastEvent();

    }

    /**
     * ServiceA is registered to all streams in EventlessProtocol. SadlManager is queried to check whether DummyStrean is registered.
     * It should return true. SadlManager is queried with an invalid stream topic and it should return false.
     */
    private void testIsStreamTopicRegistered() {

        boolean isStreamRegistered = false;

        String streamTopic = "DummyStream1";
        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                streamTopic, uhuServiceAId);
        assertTrue(" DummyStream1 not found in registered topics when queried with ServiceA Id", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(null,
                uhuServiceAId);
        assertFalse("ServiceA found when queried with null stream topic", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                streamTopic, null);
        assertFalse("SadlManager registered stream with no serviceID", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                "InvalidStreamTopic", uhuServiceAId);
        assertFalse("SadlManager returned true for invalid stream topic when queried with ServiceA id", isStreamRegistered);

    }

    /**
     * ServiceA and ServiceB are already registered and subscribed to StreamlessProtocol.
     * SadlManager is queried for event topic "MockEvent1" with ServiceA Id and ServiceB Id. Both should return true.
     */
    private void testCheckUnicastEvent() {

        String topic = "MockEvent1";
        boolean isUnicasteventFound = true;
        isUnicasteventFound = bezirkSadlManager.checkUnicastEvent(topic, null);
        assertFalse("SadlManager returned true when MockEvent1 queried with no zirk Id", isUnicasteventFound);
        isUnicasteventFound = bezirkSadlManager.checkUnicastEvent(null,
                uhuServiceAId);
        assertFalse("SadlManager returned true when checked for null event topic", isUnicasteventFound);
        boolean serviceAEventFound = bezirkSadlManager.checkUnicastEvent(topic,
                uhuServiceAId);
        assertTrue("ServiceA not subscribed to MockEvent1", serviceAEventFound);
        boolean serviceBEventFound = bezirkSadlManager.checkUnicastEvent(topic,
                uhuServiceBId);
        assertTrue("ServiceB not subscribed to MockEvent1", serviceBEventFound);

    }

    /**
     * ServiceA is already registered and subscribed to EventlessProtocol and StreamlessProtocolRole. SadlManager is queried for event topic "MockEvent1"
     * with location as null. It should return both ServiceAId and ServiceBId.
     * <p/>
     * SadlRegistry is queried for event topic "MockEvent1" with location "OFFICE1/BLOCK1/FLOOR1". It should return only ServiceA Id.
     * <p/>
     * SadlRegistry is queried for event topic "InvalidEvent" with location NULL. It should not return any zirkId.
     */
    private void testCheckMulticastEvent() {

        String topic = "MockEvent1";
        Set<BezirkZirkId> subscribedServiceSet;
        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(null,
                reception);
        assertNull("SadlManager returned non null subscribed servicelist for null event with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet);

        // Check with null lcoation
        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic, null);

        assertNotNull("SadlManager dint return any zirk id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet);
        assertEquals("SadlManager dint return two ids in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", 2, subscribedServiceSet.size());
        assertTrue("SadlManager dint return ServiceA id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet.contains(uhuServiceAId));
        assertTrue("SadlManager dint return ServiceB id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet.contains(uhuServiceBId));

        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic,
                reception);

        assertNotNull("SadlManager dint return subscribed zirk list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.", subscribedServiceSet);
        assertEquals("SadlManager returned more than 1 subscribed zirk for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION", 1, subscribedServiceSet.size());
        assertTrue("SadlManager dint return ServiceAId in subscribed zirk list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.", subscribedServiceSet.contains(uhuServiceAId));

        bezirkSadlManager.setLocation(uhuServiceAId, new Location(null));

        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic,
                reception);
        assertNull("SadlManager returned subscribedserviceset for invalid location.", subscribedServiceSet);

        // Check with invalid event
        topic = "InvalidEvent";
        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic,
                reception);
        assertNull("SadlManager returned subscribedserviceset for invalid event.", subscribedServiceSet);

    }
}

