package com.bezirk.proxy.pc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;
import com.bezirk.discovery.DiscoveryLabel;
import com.bezirk.discovery.DiscoveryProcessor;
import com.bezirk.discovery.DiscoveryRecord;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.util.MockProtocolsForUhuPC;
import com.bezirk.util.MockSetUpUtilityForUhuPC;

/**
 *  This testcase verfies the methods in ProxyForServices class.
 *  
 * @author AJC6KOR
 *
 */
public class ProxyForServicesTest {

	private static final MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();
	private static UhuSadlManager sadlManager;

	private final String serviceName = "MockServiceA";
	private final String serviceAId = "MockServiceAId";
	private final UhuServiceId uhuServiceAId = new UhuServiceId(serviceAId);

	private final MockProtocolsForUhuPC.DummyProtocol dummyProtocol = new MockProtocolsForUhuPC().new DummyProtocol();
	private final SubscribedRole pRole = new SubscribedRole(dummyProtocol);

	private static final Logger LOGGER = LoggerFactory.getLogger(ProxyForServicesTest.class);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		LOGGER.info("********** Setting up ProxyForServicesTest Testcase **********");

		mockSetUP.setUPTestEnv();

		try {
			sadlManager = mockSetUP.getUhuSadlManager();
		} catch (UnknownHostException e) {
			fail("Unable to set up test environment.");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		LOGGER.info("********** Shutting down ProxyForServicesTest Testcase **********");

		mockSetUP.destroyTestSetUp();

	}

	@Test
	public void test() {

		testRegisterService();

		testSubscribeService();

		testUnSubscribeService();

		testUnregisterService();

		testDiscover();

		testSetLocation();
	}

	private void testRegisterService() {
		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);

		proxyforServices.registerService(uhuServiceAId, serviceName);

		assertTrue("Proxy is unable to register service. ",
				sadlManager.isServiceRegisterd(uhuServiceAId));

		proxyforServices.unregister(uhuServiceAId);
	}

	private void testSubscribeService() {
		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);

		proxyforServices.registerService(uhuServiceAId, serviceName);
		proxyforServices.subscribeService(uhuServiceAId, pRole);

		assertTrue(
				"Proxy is allowing duplicate subscription. ",
				sadlManager.isStreamTopicRegistered(
						dummyProtocol.getStreamTopics()[0], uhuServiceAId));

		proxyforServices.unregister(uhuServiceAId);

	}

	private void testUnregisterService() {
		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);

		proxyforServices.registerService(uhuServiceAId, serviceName);

		proxyforServices.unregister(uhuServiceAId);

		assertFalse("Proxy is unable to perform unregistration ",
				sadlManager.isServiceRegisterd(uhuServiceAId));
	}

	private void testUnSubscribeService() {
		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);

		proxyforServices.registerService(uhuServiceAId, serviceName);
		proxyforServices.subscribeService(uhuServiceAId, pRole);
		proxyforServices.unsubscribe(uhuServiceAId, pRole);
		assertFalse(
				"Proxy is unable to perform unsubscription. ",
				sadlManager.isStreamTopicRegistered(
						dummyProtocol.getStreamTopics()[0], uhuServiceAId));

		proxyforServices.unregister(uhuServiceAId);

	}

	private void testDiscover() {

		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);

		String serviceId = "ServiceB";
		UhuServiceId uhuServiceBId = new UhuServiceId(serviceId);

		proxyforServices.registerService(uhuServiceBId, serviceId);
		proxyforServices.subscribeService(uhuServiceBId, pRole);

		proxyforServices.discover(uhuServiceAId, null, pRole, 3, 10000, 1);
		ConcurrentHashMap<DiscoveryLabel, DiscoveryRecord> discoveredMap = new ConcurrentHashMap<>();
		try {
			discoveredMap = DiscoveryProcessor.getDiscovery()
					.getDiscoveredMap();
		} catch (InterruptedException e) {

			fail("Unable to fetch discovered map. " + e.getMessage());
		}
		assertEquals("Proxy is unable to perform discovery. ",
				discoveredMap.size(), 1);

		DiscoveryLabel discLabel = discoveredMap.entrySet().iterator().next()
				.getKey();
		assertEquals(
				"DiscoveryId is not matching with the id in the discovery request.",
				discLabel.getDiscoveryId(), 3);

		proxyforServices.unregister(uhuServiceBId);
	}

	private void testSetLocation() {

		com.bezirk.proxy.pc.ProxyforServices proxyforServices = new com.bezirk.proxy.pc.ProxyforServices();
		proxyforServices.setSadlRegistry(sadlManager);
		proxyforServices.registerService(uhuServiceAId, serviceName);

		Location location = new Location("OFFICE1/BLOCK1/FLOOR1");
		proxyforServices.setLocation(uhuServiceAId, location);

		Location locInRegistry = ((UhuSadlManager) proxyforServices
				.getSadlRegistry()).getLocationForService(uhuServiceAId);
		assertEquals(
				"Location for mockservice is not matching the location set via proxy.",
				location, locInRegistry);
	}
}