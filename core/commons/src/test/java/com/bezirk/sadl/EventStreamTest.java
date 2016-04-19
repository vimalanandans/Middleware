package com.bezirk.sadl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testcase verifies the event and stream topics registered with SadlManager using service Ids.
 * 
 * @author AJC6KOR
 *
 */
public class EventStreamTest {

	private final static Logger log = LoggerFactory
			.getLogger(EventStreamTest.class);

	private static UhuSadlManager uhuSadlManager = null;

	private static final MockSetUpUtility mockUtility = new MockSetUpUtility();

	private static final UhuServiceId uhuServiceAId = new UhuServiceId(
			"ServiceA");
	private static final UhuServiceId uhuServiceBId = new UhuServiceId(
			"ServiceB");
	private static final UhuServiceId uhuServiceCId = new UhuServiceId(
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

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.info("***** Setting up EventStream TestCase *****");
		mockUtility.setUPTestEnv();
		uhuSadlManager = mockUtility.uhuSadlManager;

		
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
		uhuSadlManager.registerService(uhuServiceAId);
		uhuSadlManager.subscribeService(uhuServiceAId, subscribedStreamlessPRole);
		uhuSadlManager.subscribeService(uhuServiceAId, subscribedEventlessPRole);
		uhuSadlManager.setLocation(uhuServiceAId, reception);
		uhuSadlManager.registerService(uhuServiceBId);
		uhuSadlManager.subscribeService(uhuServiceBId, subscribedStreamlessPRole);
		uhuSadlManager.registerService(uhuServiceCId);
		uhuSadlManager.subscribeService(uhuServiceCId, subscribedEventlessPRole);


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
	 * 
	 */
	private void testIsStreamTopicRegistered() {

		boolean isStreamRegistered = false;

		String streamTopic = "DummyStream1";
		isStreamRegistered = uhuSadlManager.isStreamTopicRegistered(
				streamTopic, uhuServiceAId);
		assertTrue(" DummyStream1 not found in registered topics when queried with ServiceA Id", isStreamRegistered);

		isStreamRegistered = uhuSadlManager.isStreamTopicRegistered(null,
				uhuServiceAId);
		assertFalse("ServiceA found when queried with null stream topic",isStreamRegistered);

		isStreamRegistered = uhuSadlManager.isStreamTopicRegistered(
				streamTopic, null);
		assertFalse("SadlManager registered stream with no serviceID", isStreamRegistered);

		isStreamRegistered = uhuSadlManager.isStreamTopicRegistered(
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
		isUnicasteventFound = uhuSadlManager.checkUnicastEvent(topic, null);
		assertFalse("SadlManager returned true when MockEvent1 queried with no service Id",isUnicasteventFound);
		isUnicasteventFound = uhuSadlManager.checkUnicastEvent(null,
				uhuServiceAId);
		assertFalse("SadlManager returned true when checked for null event topic",isUnicasteventFound);
		boolean serviceAEventFound = uhuSadlManager.checkUnicastEvent(topic,
				uhuServiceAId);
		assertTrue("ServiceA not subscribed to MockEvent1", serviceAEventFound);
		boolean serviceBEventFound = uhuSadlManager.checkUnicastEvent(topic,
				uhuServiceBId);
		assertTrue("ServiceB not subscribed to MockEvent1", serviceBEventFound);

	}

	/**
	 * ServiceA is already registered and subscribed to EventlessProtocol and StreamlessProtocolRole. SadlManager is queried for event topic "MockEvent1"
	 * with location as null. It should return both ServiceAId and ServiceBId.
	 * 
	 * SadlRegistry is queried for event topic "MockEvent1" with location "OFFICE1/BLOCK1/FLOOR1". It should return only ServiceA Id.
	 * 
	 * SadlRegistry is queried for event topic "InvalidEvent" with location NULL. It should not return any serviceId.
	 * 
	 */
	private void testCheckMulticastEvent() {

		String topic = "MockEvent1";
		Set<UhuServiceId> subscribedServiceSet;
		subscribedServiceSet = uhuSadlManager.checkMulticastEvent(null,
				reception);
		assertNull("SadlManager returned non null subscribed servicelist for null event with location : OFFICE1/BLOCK1/RECEPTION ",subscribedServiceSet);

		// Check with null lcoation
		subscribedServiceSet = uhuSadlManager.checkMulticastEvent(topic, null);

		assertNotNull("SadlManager dint return any service id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ",subscribedServiceSet);
		assertEquals("SadlManager dint return two ids in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ",2, subscribedServiceSet.size());
		assertTrue("SadlManager dint return ServiceA id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ",subscribedServiceSet.contains(uhuServiceAId));
		assertTrue("SadlManager dint return ServiceB id in subscribed servicelist for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION ",subscribedServiceSet.contains(uhuServiceBId));

		subscribedServiceSet = uhuSadlManager.checkMulticastEvent(topic,
				reception);

		assertNotNull("SadlManager dint return subscribed service list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.",subscribedServiceSet);
		assertEquals("SadlManager returned more than 1 subscribed service for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION",1, subscribedServiceSet.size());
		assertTrue("SadlManager dint return ServiceAId in subscribed service list for MockEvent1 with location : OFFICE1/BLOCK1/RECEPTION.",subscribedServiceSet.contains(uhuServiceAId));

		uhuSadlManager.setLocation(uhuServiceAId, new Location(null));

		subscribedServiceSet = uhuSadlManager.checkMulticastEvent(topic,
				reception);
		assertNull("SadlManager returned subscribedserviceset for invalid location.",subscribedServiceSet);

		// Check with invalid event
		topic = "InvalidEvent";
		subscribedServiceSet = uhuSadlManager.checkMulticastEvent(topic,
				reception);
		assertNull("SadlManager returned subscribedserviceset for invalid event.", subscribedServiceSet);

	}
}

