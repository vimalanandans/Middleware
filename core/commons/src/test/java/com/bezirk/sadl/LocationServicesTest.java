package com.bezirk.sadl;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This testcase verifies the setting and retrieval of zirk location.
 *
 * @author AJC6KOR
 */
public class LocationServicesTest {

    private final static Logger log = LoggerFactory
            .getLogger(LocationServicesTest.class);
    private static final BezirkZirkId uhuServiceAId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkId uhuServiceCId = new BezirkZirkId("ServiceC");
    private static final BezirkZirkId dummyServiceId = new BezirkZirkId("InvalidServiceForTest");
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static BezirkSadlManager bezirkSadlManager = null;
    private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up LocationServicesTest TestCase *****");

        mockUtility.setUPTestEnv();

        bezirkSadlManager = mockUtility.bezirkSadlManager;

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
		 * SadlManager is asked to set location for unregistered zirkId and should return false.
		 * */
        BezirkCompManager.setUpaDevice(null);
        boolean isLocationSet = bezirkSadlManager.setLocation(dummyServiceId, reception);
        assertFalse(isLocationSet);

		/*
		 * SadlManager asked to get location of unregistered zirk. It should return null
		 * */
        Location nullLocation = bezirkSadlManager.getLocationForService(dummyServiceId);
        assertNull(nullLocation);
		
		/*
		 * Dummy Zirk is registered with SadlManager.
		 * SadlManager is queried for the zirk location of this dummy zirk.
		 *It should return null as neither the zirk nor upaDevice is set up yet.
		 **/
        bezirkSadlManager.registerService(dummyServiceId);
        Location invalidLocation = bezirkSadlManager.getLocationForService(dummyServiceId);

        assertNull("Location from registry : " + invalidLocation, invalidLocation);

        //Configure default upa device.
        BezirkCompManager.setUpaDevice(mockUtility.upaDevice);
		/*
		 * Dummy Zirk is registered with SadlManager.
		 * SadlManager is queried for the zirk location of this dummy zirk.
		 *It should return default device location as now the default upa device is configured.
		 **/
        bezirkSadlManager.registerService(dummyServiceId);
        Location serviceLocation = bezirkSadlManager.getLocationForService(dummyServiceId);

        assertEquals("Zirk location is not equal to the default device location when zirk location not set explicitly.", BezirkCompManager.getUpaDevice().getDeviceLocation(), serviceLocation);
        bezirkSadlManager.unregisterService(dummyServiceId);

    }


    private void testPositiveCases() {

        mockUtility.setupUpaDevice();
		
		/*
		 * ServiceA and ServiceC are registered and location set to "reception". 
		 * SadlManager is queried for location of ServiceA and should return "OFFICE1/BLOCk1/RECEPTION."
		 * */
        bezirkSadlManager.registerService(uhuServiceAId);
        bezirkSadlManager.setLocation(uhuServiceAId, reception);
        bezirkSadlManager.registerService(uhuServiceCId);
        bezirkSadlManager.setLocation(uhuServiceCId, reception);
        Location locationFromRegistry = bezirkSadlManager
                .getLocationForService(uhuServiceAId);
        assertNotNull("SadlManager dint return correction for ServiceA.", locationFromRegistry);
        assertEquals("Location returned by SadlManager is not equal to the set location", reception, locationFromRegistry);

        assertEquals("SadlManager dont have location for two services.", 2, bezirkSadlManager.sadlRegistry.locationMap.size());


    }


}
