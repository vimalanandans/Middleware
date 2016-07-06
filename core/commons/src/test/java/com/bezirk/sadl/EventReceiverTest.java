//package com.bosch.upa.uhu.sadl;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import java.util.HashSet;
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
//import BezirkDiscoveredZirk;
//import BezirkZirkEndPoint;
//import ZirkId;
//
//public class EventReceiverTest {
//	private static final Logger logger = LoggerFactory.getLogger(EventReceiverTest.class);
//	private TestingBezirkSadlRegistry sadlRegistry = null;
//	 
//	@BeforeClass
//	public static void setup(){
//		logger.info(" ***** STARTING EventReceiver TEST *****");
//	}
//	
//	@Before
//	public void beforeEachTest(){
//		logger.info("Before each Test");
//		sadlRegistry = new TestingBezirkSadlRegistry();
//	}
//	
//	@After
//	public void afterEachTest(){
//		logger.info("After each Test");
//		sadlRegistry.getEventsMap().clear();
//		sadlRegistry.getLocationMap().clear();
//		sadlRegistry.getProtocolMap().clear();
//		sadlRegistry.getSidMap().clear();
//		sadlRegistry = null;
//	}
//	
//	@AfterClass
//	public static void teardown(){
//		logger.info(" ***** BRINGING DOWN EventReceiver TEST *****");
//	}
//
//	@Test
//	public void testForUnicastEvent(){
//		ZirkId testService1 = new ZirkId("test-zirk-id-1");
//		ZirkId testService2 = new ZirkId("test-zirk-id-2");
//		
//		TestProtocolRoleA protocolRoleA = new TestProtocolRoleA();
//		SubscribedRole subscribedRoleA = new SubscribedRole(protocolRoleA);
//		
//		assertFalse(sadlRegistry.checkUnicastEvent(MockRequestEventA.topic, testService1));
//		sadlRegistry.registerZirk(testService1);
//		sadlRegistry.subscribeService(testService1, subscribedRoleA);
//		assertTrue(sadlRegistry.checkUnicastEvent(MockRequestEventA.topic, testService1));
//		assertFalse(sadlRegistry.checkUnicastEvent(MockRequestEventA.topic, testService2));
//		assertFalse(sadlRegistry.checkUnicastEvent(MockRequestEventD.topic, testService1));
//	}
//	
//	@Test
//	public void testForMulticastEvent(){
//		Location defaultLocation = new Location("Room", "Room", "Room");
//		UPADevice.setDeviceLocation(defaultLocation);
//		
//		ZirkId testService1 = new ZirkId("test-zirk-id-1");
//		ZirkId testService2 = new ZirkId("test-zirk-id-2");
//		ZirkId testService3 = new ZirkId("test-zirk-id-3");
//		ZirkId testService4 = new ZirkId("test-zirk-id-4");
//		
//		TestProtocolRoleA protocolRoleA = new TestProtocolRoleA();
//		SubscribedRole subscribedRoleA = new SubscribedRole(protocolRoleA);
//		TestProtocolRoleB protocolRoleB = new TestProtocolRoleB();
//		SubscribedRole subscribedRoleB = new SubscribedRole(protocolRoleB);
//		TestProtocolRoleC protocolRoleC = new TestProtocolRoleC();
//		SubscribedRole subscribedRoleC = new SubscribedRole(protocolRoleC);
//		
//		assertEquals(null,sadlRegistry.checkMulticastEvent(MockRequestEventA.topic, null));
//		sadlRegistry.registerZirk(testService1);
//		sadlRegistry.subscribeService(testService1, subscribedRoleA);
//		sadlRegistry.subscribeService(testService1, subscribedRoleB);
//		sadlRegistry.subscribeService(testService1, subscribedRoleC);
//		assertEquals(1,sadlRegistry.checkMulticastEvent(MockRequestEventA.topic, null).size());
//		sadlRegistry.registerZirk(testService2);
//		sadlRegistry.subscribeService(testService2, subscribedRoleB);
//		sadlRegistry.registerZirk(testService3);
//		sadlRegistry.subscribeService(testService3, subscribedRoleC);
//		assertEquals(3,sadlRegistry.checkMulticastEvent(MockRequestEventA.topic, null).size());
//		sadlRegistry.printContentsOfMaps();
//		assertEquals(2,sadlRegistry.checkMulticastEvent(MockRequestEventD.topic, null).size());
//		
//		Location newLocation = new Location("Garage", "Garage", "Garage");
//		sadlRegistry.setLocation(testService1, newLocation);
//		assertEquals(1,sadlRegistry.checkMulticastEvent(MockRequestEventA.topic, newLocation).size());
//		
//		sadlRegistry.setLocation(testService3, newLocation);
//		assertEquals(2,sadlRegistry.checkMulticastEvent(MockRequestEventA.topic, newLocation).size());
//		
//	}
//}
