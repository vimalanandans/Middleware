package com.bezirk.sadl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.commons.UhuCompManager;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testcase verifies the setting and retrieval of service location.
 * 
 * @author AJC6KOR
 *
 */
public class LocationServicesTest {

	private final static Logger log = LoggerFactory
			.getLogger(LocationServicesTest.class);

	private static UhuSadlManager uhuSadlManager=null;

	private static final UhuServiceId uhuServiceAId = new UhuServiceId("ServiceA");
	private static final UhuServiceId uhuServiceCId = new UhuServiceId("ServiceC");
	private static final UhuServiceId dummyServiceId = new UhuServiceId("InvalidServiceForTest");

	private static final MockSetUpUtility mockUtility = new MockSetUpUtility();

	private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.info("***** Setting up LocationServicesTest TestCase *****");

		mockUtility.setUPTestEnv();
		
		uhuSadlManager = mockUtility.uhuSadlManager;
				
		}

	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down LocationServicesTest Testcase *****");
		mockUtility.destroyTestSetUp();
		
	}

	
	@Test
	public void testLocationServices() {

		log.info("***** testing invalid cases *****");
		testNegativeCases();
		
		log.info("***** testing LocationService *****");
		testPositiveCases();
		
		
	}

	private void testNegativeCases() {
		/*
		 * SadlManager is asked to set location for unregistered serviceId and should return false. 
		 * */
		UhuCompManager.setUpaDevice(null);
		boolean isLocationSet = uhuSadlManager.setLocation(dummyServiceId, reception);
		assertFalse(isLocationSet);

		/*
		 * SadlManager asked to get location of unregistered service. It should return null
		 * */
		Location nullLocation = uhuSadlManager.getLocationForService(dummyServiceId);
		assertNull(nullLocation);
		
		/*
		 * Dummy Service is registered with SadlManager. 
		 * SadlManager is queried for the service location of this dummy service.
		 *It should return null as neither the service nor upaDevice is set up yet.
		 **/
		uhuSadlManager.registerService(dummyServiceId);
		Location invalidLocation = uhuSadlManager.getLocationForService(dummyServiceId);

		assertNull("Location from registry : "+invalidLocation,invalidLocation);

		//Configure default upa device.
		UhuCompManager.setUpaDevice(mockUtility.upaDevice);
		/*
		 * Dummy Service is registered with SadlManager. 
		 * SadlManager is queried for the service location of this dummy service.
		 *It should return default device location as now the default upa device is configured.
		 **/
		uhuSadlManager.registerService(dummyServiceId);
		Location serviceLocation = uhuSadlManager.getLocationForService(dummyServiceId);

		assertEquals("Service location is not equal to the default device location when service location not set explicitly.",UhuCompManager.getUpaDevice().getDeviceLocation(),serviceLocation);
		uhuSadlManager.unregisterService(dummyServiceId);
		
	}


	private void testPositiveCases() {
	
		mockUtility.setupUpaDevice();
		
		/*
		 * ServiceA and ServiceC are registered and location set to "reception". 
		 * SadlManager is queried for location of ServiceA and should return "OFFICE1/BLOCk1/RECEPTION."
		 * */
		uhuSadlManager.registerService(uhuServiceAId);
		uhuSadlManager.setLocation(uhuServiceAId, reception);
		uhuSadlManager.registerService(uhuServiceCId);
		uhuSadlManager.setLocation(uhuServiceCId, reception);
		Location locationFromRegistry = uhuSadlManager
				.getLocationForService(uhuServiceAId);
		assertNotNull("SadlManager dint return correction for ServiceA.",locationFromRegistry);
		assertEquals("Location returned by SadlManager is not equal to the set location",reception, locationFromRegistry);
		
		assertEquals("SadlManager dont have location for two services.",2, uhuSadlManager.sadlRegistry.locationMap.size());
		
		

	}


}
