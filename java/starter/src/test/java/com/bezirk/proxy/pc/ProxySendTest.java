package com.bezirk.proxy.pc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bezirk.api.addressing.ServiceEndPoint;
import com.bezirk.api.messages.Message.Stripe;
import com.bezirk.api.messages.UnicastStream;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.util.MockComms;
import com.bezirk.util.MockProtocolsForUhuPC;
import com.bezirk.util.MockSetUpUtilityForUhuPC;

/**
 * This testcase verifies the methods in send apis in ProxyForServices class.
 * 
 * @author AJC6KOR
 *
 */
public class ProxySendTest {

	private static final MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();
	private static UhuSadlManager sadlManager;

	private final String serviceName="MockServiceA";
	private final String serviceAId="MockServiceAId";
	private final UhuServiceId senderId = new UhuServiceId(serviceAId);
	
	private final String serviceBId="MockServiceBId";
	private final UhuServiceId receiverId = new UhuServiceId(serviceBId);
	private final UhuServiceEndPoint receiver = new UhuServiceEndPoint(receiverId);

	private final String sendfilePath = com.bezirk.proxy.pc.ProxyforServices.class.getClassLoader().getResource("streamingTest.txt").getPath();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ProxySendTest.class);

		
		@BeforeClass
		public static void setUpBeforeClass() throws Exception {
			
			LOGGER.info("********** Setting up ProxySendTest Testcase **********");
			
			System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
			mockSetUP.setUPTestEnv();

			try {
				sadlManager = mockSetUP.getUhuSadlManager();
			} catch (UnknownHostException e) {
				fail("Unable to set up test environment.");
			}
		}
		
		@AfterClass
		public static void tearDownAfterClass() throws Exception {

			LOGGER.info("********** Shutting down ProxySendTest Testcase **********");
			
			System.clearProperty("InterfaceName");

			mockSetUP.destroyTestSetUp();

		}
		
		/**
		 * Stream Descriptor
		 */
		class MockRequestStream extends UnicastStream{
			public String sampleValue = "TestValue";
			
			public MockRequestStream(Stripe stripe, String topic,
					ServiceEndPoint recipient) {
				super(stripe, topic, recipient);
			}	
			
			
		}
		
	@Test
	public void test() {

	testSendStream();
	
	testSendUnicastEvent();
	
	testSendMulticastEvent();
	
	}

	private void testSendStream() {
		com.bezirk.proxy.pc.ProxyforServices proxyForServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyForServices.setSadlRegistry(sadlManager);
		MockComms mockComms = (MockComms)mockSetUP.getUhuComms();
		proxyForServices.setCommsManager(mockComms);
		
		proxyForServices.registerService(senderId, serviceName);
		String serializedStream = new MockRequestStream(Stripe.REQUEST, "MockStream", receiver).serialize();
		receiver.device ="DeviceB";
		short streamId = proxyForServices.sendStream(senderId, receiver, serializedStream, sendfilePath, (short)5);
		// checking the stream id is not enough	
		assertEquals("Proxy is unable to send stream. ",1,streamId);
		
		assertEquals("Proxy is unable to add stream request to the comms queue.",1, mockComms.getStreamList().size());
		mockComms.clearQueues();
	}
	
	private void testSendUnicastEvent() {
		try {
			com.bezirk.proxy.pc.ProxyforServices proxyForServices = new com.bezirk.proxy.pc.ProxyforServices();
			proxyForServices.setSadlRegistry(sadlManager);
			MockComms mockComms = (MockComms)mockSetUP.getUhuComms();
			proxyForServices.setCommsManager(mockComms);
			
			String serializedEventMsg = new MockProtocolsForUhuPC().new MockEvent1(Stripe.REQUEST, "MockEvent").serialize();
			receiver.device ="DeviceB";
			proxyForServices.sendUnicastEvent(senderId, receiver, serializedEventMsg);
			
			assertEquals("Proxy is unable to add unicast event message to the comms queue.",1, mockComms.getEventList().size());

			mockComms.clearQueues();
		} catch (Exception e) {

			fail("Proxy is unable to send unicast events.");
		
		}
	}
	
	private void testSendMulticastEvent() {
		try {
			com.bezirk.proxy.pc.ProxyforServices proxyForServices = new com.bezirk.proxy.pc.ProxyforServices();
			proxyForServices.setSadlRegistry(sadlManager);
			MockComms mockComms = (MockComms)mockSetUP.getUhuComms();
			proxyForServices.setCommsManager(mockComms);
			
			String serializedEventMsg = new MockProtocolsForUhuPC().new MockEvent1(Stripe.REQUEST, "MockEvent").serialize();
			Address address = new Address(new Location("FLOOR1/BLOCk1/ROOM1"));
			proxyForServices.sendMulticastEvent(senderId, address, serializedEventMsg);
			assertEquals("Proxy is unable to add multicast event message to the comms queue.",1, mockComms.getEventList().size());
	
			proxyForServices.sendMulticastEvent(senderId, null, serializedEventMsg);
			assertEquals("Proxy is unable to add multicast event message to the comms queue.",2, mockComms.getEventList().size());
			mockComms.clearQueues();
		} catch (Exception e) {

			fail("Proxy is unable to send unicast events.");
		
		}
	}

}