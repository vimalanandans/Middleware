package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;
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
 */
public class EventStreamTest {
    private final static Logger logger = LoggerFactory.getLogger(EventStreamTest.class);

    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static final ZirkId bezirkZirkAId = new ZirkId(
            "ServiceA");
    private static final ZirkId bezirkZirkBId = new ZirkId(
            "ServiceB");
    private static final ZirkId bezirkServiceCId = new ZirkId(
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

        logger.info("***** Setting up EventStream TestCase *****");
        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;


    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        mockUtility.destroyTestSetUp();
        logger.info("***** Shutting down EventStream Testcase *****");

    }

    @Test
    public void testEventsAndStreams() {

		
		/*
         * ServiceA is registered and subscribed to EventlessProtocolRole and StreamlessProtocolRole. ServiceA is located at reception.
		 * ServiceB is registered and subscribed to StreamlessProtocolRole with defaultlocation.
		 * ServiceC is registered and subscribed to EventlessProtocolRole with defaultlocation.
		 * */
        bezirkSadlManager.registerService(bezirkZirkAId);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedStreamlessPRole);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedEventlessPRole);
        bezirkSadlManager.setLocation(bezirkZirkAId, reception);
        bezirkSadlManager.registerService(bezirkZirkBId);
        bezirkSadlManager.subscribeService(bezirkZirkBId, subscribedStreamlessPRole);
        bezirkSadlManager.registerService(bezirkServiceCId);
        bezirkSadlManager.subscribeService(bezirkServiceCId, subscribedEventlessPRole);


        logger.info("********* testing IsStreamTopicRegistered ********");
        testIsStreamTopicRegistered();
        logger.info("********* testing CheckUnicastEvent ********");
        testCheckUnicastEvent();
        logger.info("********* testing CheckMulticastEvent ********");
        testCheckMulticastEvent();

    }

    /**
     * ServiceA is registered to all streams in EventlessProtocol. SadlManager is queried to check whether DummyStrean is registered.
     * It should return true. SadlManager is queried with an invalid stream topic and it should return false.
     */
    private void testIsStreamTopicRegistered() {
        String streamTopic = "DummyStream1";
        boolean isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                streamTopic, bezirkZirkAId);
        assertTrue(" DummyStream1 not found in registered topics when queried with ServiceA Id", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(null,
                bezirkZirkAId);
        assertFalse("ServiceA found when queried with null stream topic", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                streamTopic, null);
        assertFalse("SadlManager registered stream with no serviceID", isStreamRegistered);

        isStreamRegistered = bezirkSadlManager.isStreamTopicRegistered(
                "InvalidStreamTopic", bezirkZirkAId);
        assertFalse("SadlManager returned true for invalid stream topic when queried with ServiceA id", isStreamRegistered);

    }

    /**
     * ServiceA and ServiceB are already registered and subscribed to StreamlessProtocol.
     * SadlManager is queried for event topic "MockEvent1" with ServiceA Id and ServiceB Id. Both should return true.
     */
    private void testCheckUnicastEvent() {
        String topic = "MockEvent1";
        boolean isUnicasteventFound = bezirkSadlManager.checkUnicastEvent(topic, null);
        assertFalse("SadlManager returned true when MockEvent1 queried with no zirk Id", isUnicasteventFound);
        isUnicasteventFound = bezirkSadlManager.checkUnicastEvent(null,
                bezirkZirkAId);
        assertFalse("SadlManager returned true when checked for null event topic", isUnicasteventFound);
        boolean zirkAEventFound = bezirkSadlManager.checkUnicastEvent(topic,
                bezirkZirkAId);
        assertTrue("ZirkA not subscribed to MockEvent1", zirkAEventFound);
        boolean zirkBEventFound = bezirkSadlManager.checkUnicastEvent(topic,
                bezirkZirkBId);
        assertTrue("ZirkB not subscribed to MockEvent1", zirkBEventFound);

    }

    /**
     * ServiceA is already registered and subscribed to EventlessProtocol and StreamlessProtocolRole. SadlManager is queried for event topic "MockEvent1"
     * with location as null. It should return both ServiceAId and ServiceBId.
     * <p>
     * SadlRegistry is queried for event topic "MockEvent1" with location "OFFICE1/BLOCK1/FLOOR1". It should return only ServiceA Id.
     * </p><p>
     * SadlRegistry is queried for event topic "InvalidEvent" with location NULL. It should not return any zirkId.
     * </p>
     */
    private void testCheckMulticastEvent() {

        String topic = "MockEvent1";
        Set<ZirkId> subscribedServiceSet;
        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(null,
                reception);
        assertNull("SadlManager returned non null subscribed servicelist for null event with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet);

        // Check with null lcoation
        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic, null);

        assertNotNull("SadlManager dint return any zirk id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet);
        assertEquals("SadlManager dint return two ids in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", 2, subscribedServiceSet.size());
        assertTrue("SadlManager dint return ServiceA id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet.contains(bezirkZirkAId));
        assertTrue("SadlManager dint return ServiceB id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ", subscribedServiceSet.contains(bezirkZirkBId));

        subscribedServiceSet = bezirkSadlManager.checkMulticastEvent(topic,
                reception);

        assertNotNull("SadlManager dint return subscribed zirk list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.", subscribedServiceSet);
        assertEquals("SadlManager returned more than 1 subscribed zirk for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION", 1, subscribedServiceSet.size());
        assertTrue("SadlManager dint return ServiceAId in subscribed zirk list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.", subscribedServiceSet.contains(bezirkZirkAId));

        bezirkSadlManager.setLocation(bezirkZirkAId, new Location(null));

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

