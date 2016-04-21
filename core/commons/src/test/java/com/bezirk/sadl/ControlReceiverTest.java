//package com.bosch.upa.uhu.sadl;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.util.HashSet;
//import java.util.Locale;
//
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bosch.upa.devices.UPADevice;
//import Location;
//import SubscribedRole;
//import UhuDiscoveredService;
//import UhuServiceId;
//
//public class ControlReceiverTest {
//	private static final Logger logger = LoggerFactory.getLogger(ControlReceiverTest.class);
//	private TestingUhuSadlRegistry sadlRegistry = null;
//	 
//	@BeforeClass
//	public static void setup(){
//		log.info(" ***** STARTING ControlReceiver TEST *****");
//	}
//	
//	@Before
//	public void beforeEachTest(){
//		log.info("Before each Test");
//		sadlRegistry = new TestingUhuSadlRegistry();
//	}
//	
//	@After
//	public void afterEachTest(){
//		log.info("After each Test");
//		sadlRegistry.getEventsMap().clear();
//		sadlRegistry.getLocationMap().clear();
//		sadlRegistry.getProtocolMap().clear();
//		sadlRegistry.getSidMap().clear();
//		sadlRegistry = null;
//	}
//	
//	@AfterClass
//	public static void teardown(){
//		log.info(" ***** BRINGING DOWN ControlReceiver TEST *****");
//	}
//
//	@Test
//	@Ignore
//	public void testForStreamRegistration(){
//		UhuServiceId testService1 = new UhuServiceId("test-service-id-1");
//		UhuServiceId testService2 = new UhuServiceId("test-service-id-2");
//		UhuServiceId testService3 = new UhuServiceId("test-service-id-3");
//		UhuServiceId testService4 = new UhuServiceId("test-service-id-4");
//		
//		TestProtocolRoleA protocolRoleA = new TestProtocolRoleA();
//		SubscribedRole subscribedRoleA = new SubscribedRole(protocolRoleA);
//		TestProtocolRoleB protocolRoleB = new TestProtocolRoleB();
//		SubscribedRole subscribedRoleB = new SubscribedRole(protocolRoleB);
//		TestProtocolRoleC protocolRoleC = new TestProtocolRoleC();
//		SubscribedRole subscribedRoleC = new SubscribedRole(protocolRoleC);
//		// SERVICE - 1
//		sadlRegistry.registerService(testService1);
//		sadlRegistry.subscribeService(testService1, subscribedRoleA);
//		sadlRegistry.subscribeService(testService1, subscribedRoleB);
//		// Service - 2
//		sadlRegistry.registerService(testService2);
//		sadlRegistry.subscribeService(testService2, subscribedRoleC);
//		// Service - 3
//		sadlRegistry.registerService(testService3);
//		sadlRegistry.subscribeService(testService3, subscribedRoleC);
//		
//		sadlRegistry.printContentsOfMaps();
//		//FOR NULL Check
//		assertFalse(sadlRegistry.isStreamTopicRegistered(null, testService1));
//		assertFalse(sadlRegistry.isStreamTopicRegistered(null, null));
//		// Check
//		assertFalse(sadlRegistry.isStreamTopicRegistered("DUMMY STREAM", testService4));
//		
//		assertFalse(sadlRegistry.isStreamTopicRegistered(MockRequestStreamA.topic, testService2));
//		assertTrue(sadlRegistry.isStreamTopicRegistered(MockRequestStreamA.topic, testService1));
//		assertTrue(sadlRegistry.isStreamTopicRegistered(MockRequestStreamB.topic, testService1));
//		assertFalse(sadlRegistry.isStreamTopicRegistered(MockRequestStreamA.topic, testService2));
//	}
//	
//	@Test
//	public void testForDiscovery(){
//		Location newLocation = new Location("Room", "Room", "Room");
//		UPADevice.setDeviceLocation(newLocation);
//		
//		UhuServiceId testService1 = new UhuServiceId("test-service-id-1");
//		UhuServiceId testService2 = new UhuServiceId("test-service-id-2");
//		UhuServiceId testService3 = new UhuServiceId("test-service-id-3");
//		UhuServiceId testService4 = new UhuServiceId("test-service-id-4");
//		
//		TestProtocolRoleA protocolRoleA = new TestProtocolRoleA();
//		SubscribedRole subscribedRoleA = new SubscribedRole(protocolRoleA);
//		TestProtocolRoleB protocolRoleB = new TestProtocolRoleB();
//		SubscribedRole subscribedRoleB = new SubscribedRole(protocolRoleB);
//		TestProtocolRoleC protocolRoleC = new TestProtocolRoleC();
//		SubscribedRole subscribedRoleC = new SubscribedRole(protocolRoleC);
//		
//		sadlRegistry.registerService(testService1);
//		sadlRegistry.registerService(testService2);
//		sadlRegistry.registerService(testService3);
//		sadlRegistry.subscribeService(testService1, subscribedRoleA);
//		sadlRegistry.subscribeService(testService2, subscribedRoleB);
//		sadlRegistry.subscribeService(testService2, subscribedRoleA);
//		sadlRegistry.subscribeService(testService3, subscribedRoleA);
//		
//		sadlRegistry.printContentsOfMaps();
//		HashSet<UhuDiscoveredService> discoveredServices = null;
//		// TEST FOR DIFFERENT PROTOCOL ROLE;
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleC, null);
//		assertEquals(null, discoveredServices);
//		// TEST WITH NULL LOCATION
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleA, null);
//		assertEquals(3,discoveredServices.size());
//		// TEST WITH Location
//		
//		Location newLoc = new Location("IfRoom", "IfRoom", "IfRoom");
//		sadlRegistry.setLocation(testService1, newLoc);
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleA, newLoc);
//		assertEquals(1,discoveredServices.size());
//		sadlRegistry.setLocation(testService3, newLoc);
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleC, newLoc);
//		assertEquals(null,discoveredServices);
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleB, null);
//		assertEquals(1,discoveredServices.size());
//		discoveredServices = (HashSet<UhuDiscoveredService>) sadlRegistry.discoverServices(subscribedRoleA, newLoc);
//		assertEquals(2,discoveredServices.size());
//	}
//}
