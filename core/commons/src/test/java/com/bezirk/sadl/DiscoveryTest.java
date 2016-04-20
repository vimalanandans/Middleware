package com.bezirk.sadl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This Testcase consists of 3 tests to verify the behavior of discovery service in the
 * following scenarios.
 * 
 *  a) 	Discovery with no protocolRole
 * 	b)  Discovery with specific protocolRole and location
 *  c)  Discovery with no specific location.
 * 
 * @author AJC6KOR
 *
 */
public class DiscoveryTest {

	private final static Logger log = LoggerFactory
			.getLogger(DiscoveryTest.class);

	

	private static UhuServiceId uhuServiceAId = new UhuServiceId("ServiceA"), uhuServiceBId = new UhuServiceId("ServiceB");

	private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");

	private static UhuSadlManager uhuSadlManager=null;

	private static final MockProtocols mockService = new MockProtocols();
	private static final ProtocolRole streamlessPRole = mockService.new StreamlessProtocol();
	private static final SubscribedRole subscribedStreamlessPRole = new SubscribedRole(streamlessPRole);
	private static final MockSetUpUtility mockUtility = new MockSetUpUtility();

	private Set<UhuDiscoveredService> discoveredServiceSet;

	private Set<UhuServiceId> uhuServiceIdSet;

	private UhuServiceId dServiceId;



	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("############# Setting up DiscoveryTest TestCase ################");

		mockUtility.setUPTestEnv();
		uhuSadlManager = mockUtility.uhuSadlManager;

	}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("############ Shutting down DiscoveryTest Testcase ############");
		mockUtility.destroyTestSetUp();

	}
	
	@Test
	public void testDiscoverServices() {

		/*Test1 : SadlManager should return null discoveredServiceSet when protocolRole is null.*/
		discoveredServiceSet = uhuSadlManager.discoverServices(null, reception);
		assertNull("DiscoveredService set is not null when services not available.",discoveredServiceSet);
		
		/*No services are subscribed to streamlessProtocol yet. 
		 * DiscoveredServiceSet should be null.
		 * */
		discoveredServiceSet = uhuSadlManager.discoverServices(subscribedStreamlessPRole, reception);
		assertNull("DiscoveredService set is not null when services not available.",discoveredServiceSet);
		
		
		/* ServiceA and ServiceB are registered and subscribed to StreamlessProtocolRole.
		 * ServiceB has its location set to null */
		uhuSadlManager.registerService(uhuServiceAId);
		uhuSadlManager.subscribeService(uhuServiceAId, subscribedStreamlessPRole);
		uhuSadlManager.registerService(uhuServiceBId);
		uhuSadlManager.subscribeService(uhuServiceBId, subscribedStreamlessPRole);
		uhuSadlManager.setLocation(uhuServiceBId, new Location(null,null,null));			
		
		testDiscoveryWithSpecificLocation();
		testDiscoveryWithNullLocation();
	}


	/*Test2 : SadlManager is queried to discover services subscribed to StreamlessProtocolRole 
	 * 		  with specific location.
	 */ 
	 private void testDiscoveryWithSpecificLocation() {
		
		/*SadlManager is queried to discover services subscribed to StreamlessProtocolRole 
		 * near to reception.SadlManager should return null discoveredServiceSet */ 
		 discoveredServiceSet = uhuSadlManager.discoverServices(subscribedStreamlessPRole,
				reception);
		assertNull("DiscoveredService set is not null when services not available.",discoveredServiceSet);
		
		/*ServiceA is set to the location "OFFICE1/BLOCK1/RECEPTION"*/
		uhuSadlManager.setLocation(uhuServiceAId, reception);
		
		/*	SadlManager should return serviceA for the discovery now. */
		uhuServiceIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
				reception);
		assertNotNull("No services found in discovery.",uhuServiceIdSet);
		assertTrue("ServiceA was not discovered when queried for reception as location.",uhuServiceIdSet.contains(uhuServiceAId));
	}

	/*Test3 : SadlManager is queried to discover the services subscribed to StreamlessProtocolRole.
	 * 		  As there is no location mentioned in the discovery request it should return
	 *        both ServiceA and ServiceB.
	 */
	private void testDiscoveryWithNullLocation() {
		
		uhuServiceIdSet = discoverServicesUsingProtocolAndLocation(subscribedStreamlessPRole,
				null);
		assertNotNull("No services found in discovery.",uhuServiceIdSet);
		assertTrue("ServiceA was not discovered when no location in request.",uhuServiceIdSet.contains(uhuServiceAId));
		assertTrue("ServiceB was not discovered when no location in request.",uhuServiceIdSet.contains(uhuServiceBId));
	}	
	
	private Set<UhuServiceId> discoverServicesUsingProtocolAndLocation(
			ProtocolRole protocolRole, Location loc) {
		discoveredServiceSet = uhuSadlManager.discoverServices(protocolRole, loc);
		uhuServiceIdSet =getUhuServiceIdSetOfDiscoveredServices(discoveredServiceSet);
		return uhuServiceIdSet;
	}
	
	private Set<UhuServiceId> getUhuServiceIdSetOfDiscoveredServices(
			Set<UhuDiscoveredService> discoveredServiceSet) {
		UhuServiceEndPoint serviceEndPoint;
		uhuServiceIdSet	=null;
		
		if (discoveredServiceSet != null) {
			uhuServiceIdSet = new HashSet<>();

			for (UhuDiscoveredService discoveredService : discoveredServiceSet) {
				serviceEndPoint = (UhuServiceEndPoint) (discoveredService
						.getServiceEndPoint());
				dServiceId = serviceEndPoint.getUhuServiceId();
				uhuServiceIdSet.add(dServiceId);

			}
		}
		return uhuServiceIdSet;
	}
	
	

}
