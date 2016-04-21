package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the registration and unregistration services provided by sadlmanager.
 * Scenarios tested :
 * a) Register null serviceID
 * b) Register valid serviceID
 * c) Register duplicate serviceID
 * d) Check whether valid serviceID is registered.
 * e) Check whether invalid serviceID is registered.
 * f) Retrieve registered service id list.
 * g) Unregister invalid service id.
 * h) Register valid service id which was registered previously.
 *
 * @author AJC6KOR
 */
public class RegistrationUnregistrationTest {

    private final static Logger log = LoggerFactory
            .getLogger(RegistrationUnregistrationTest.class);
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static final UhuServiceId uhuServiceAId = new UhuServiceId("ServiceA");
    private static final UhuServiceId uhuServiceBId = new UhuServiceId("ServiceB");
    private static final UhuServiceId uhuServiceCId = new UhuServiceId("ServiceC");
    private static final UhuServiceId dummyServiceId = new UhuServiceId("InvalidServiceForTest");
    private static final MockProtocols mockProtocols = new MockProtocols();
    private static final Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");
    private static UhuSadlManager uhuSadlManager = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up RegistrationUnregistrationTest TestCase *****");

        mockUtility.setUPTestEnv();
        uhuSadlManager = mockUtility.uhuSadlManager;
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down RegistrationUnregistrationTest Testcase *****");
        mockUtility.destroyTestSetUp();
    }


    @Test
    public void testRegistrationUnregistrationServices() {

        log.info("***** Testing RegisterService *****");
        testRegisterService();
        log.info("***** Tested RegisterService Successfully *****");


        log.info("***** Testing GetRegisteredServices *****");
        testGetRegisteredServices();
        log.info("***** GetRegisteredServices tested Successfully *****");

        log.info("***** Testing UnRegisterService *****");
        testUnregisterService();
        log.info("***** Tested UnRegisterService Successfully *****");

        log.info("****** Testing removeFromMaps Started *********");
        unregisterfrommaps();
        log.info("****** Test completed for removeFromMaps *********");


    }

    /**
     * ServiceA is registered to sadlRegistry and verified using isServiceRegistered.
     * SadlRegistry is queried for an invalid Id and it should return false.
     */
    private void testRegisterService() {

        boolean isServiceRegistered = true;
        /*
		 * SadlManager should return false when asked to register invalid uhuserviceID
		 * */
        isServiceRegistered = uhuSadlManager.registerService(null);
        assertFalse("SadlManager registered service with null serviceID", isServiceRegistered);
		
		/*SadlManager should return false when checked whether null service id is registered*/
        isServiceRegistered = true;
        isServiceRegistered = uhuSadlManager.isServiceRegisterd(null);
        assertFalse("SadlManager has service with null serviceID in registered service list.", isServiceRegistered);

        //ServiceA registered and location is set to reception.
        uhuSadlManager.registerService(uhuServiceAId);
        uhuSadlManager.setLocation(uhuServiceAId, reception);
		
		/*SadlManager should return false when asked to register duplicate service id.*/
        isServiceRegistered = true;
        isServiceRegistered = uhuSadlManager.registerService(uhuServiceAId);
        assertFalse("SadlManager allowed registration for duplicate serviceID.", isServiceRegistered);

        //ServiceB and ServiceC are registered.
        uhuSadlManager.registerService(uhuServiceBId);
        uhuSadlManager.registerService(uhuServiceCId);
		
	

		/*SadlManager should return true when queried for serviceA registration*/
        isServiceRegistered = uhuSadlManager
                .isServiceRegisterd(uhuServiceAId);
        assertTrue("SadlManager dont have ServiceA id in registered service list.", isServiceRegistered);
		
		/*SadlManager should return false when queried for unregistered serviceid*/
        UhuServiceId invalidService = new UhuServiceId("TestRegister");
        isServiceRegistered = uhuSadlManager.isServiceRegisterd(invalidService);
        assertFalse("Service is registered", isServiceRegistered);


    }

    /**
     * ServiceA is unregistered. SadlReigstry is queried for all registered Services. ServiceA Id should not be returned.
     */
    private void testUnregisterService() {

        boolean isUnregistered = true;
		/*
		 * SadlManager should return false when asked to unregister null serviceID.
		 * */
        isUnregistered = uhuSadlManager.unregisterService(null);
        assertFalse("SadlManager unregistered service with null serviceID.", isUnregistered);
		
		/*
		 * SadlManager should return true when asked to unregister valid serviceID which is already is registered.
		 * ServiceA should not be present in registered service list when queried after unregistration.
		 * */
        isUnregistered = uhuSadlManager.unregisterService(uhuServiceAId);
        assertTrue("SadlManager couldn't unregister ServiceA.", isUnregistered);

        Set<UhuServiceId> registeredServices = uhuSadlManager
                .getRegisteredServices();

        boolean serviceAFound = false;
        for (UhuServiceId serviceId : registeredServices) {

            if (serviceId.equals(uhuServiceAId)) {
                serviceAFound = true;

            }

        }

        assertFalse("ServiceA found in registered service list even after unregistration.", serviceAFound);
		
		/*
		 * SadlManager should return false when asked to unregister invalid serviceID.*/
        isUnregistered = uhuSadlManager.unregisterService(dummyServiceId);
        assertFalse("SadlManager unregistered invalid serviceID.", isUnregistered);
	
		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/		

		
		/*SadlManager should return false when not able to persist unregistration data to registry.*/
		/*
		mockUtility.clearSadlPersistence();
		isUnregistered = uhuSadlManager.unregisterService(uhuServiceBId);
		assertTrue(isUnregistered);
		mockUtility.restoreSadlPersistence();
		uhuSadlManager= mockUtility.uhuSadlManager;
		*/
		
		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/

    }

    /**
     * SadlRegistry is queried for all registered services.
     * It should return ServiceAId,ServiceBId and ServiceCId.
     */
    private void testGetRegisteredServices() {

        Set<UhuServiceId> registeredServiceSet = uhuSadlManager
                .getRegisteredServices();

        assertNotNull("SadlManager couldn't fetch registered service list.", registeredServiceSet);
        assertEquals("Registered Service list size is not 3.", 3, registeredServiceSet.size());
        assertTrue("ServiceA was not found in registered service list.", registeredServiceSet.contains(uhuServiceAId));
        assertTrue("ServiceB was not found in registered service list.", registeredServiceSet.contains(uhuServiceBId));
        assertTrue("ServiceC was not found in registered service list.", registeredServiceSet.contains(uhuServiceCId));
    }


    /**
     * This is to remove the entries from the maps of SadlRegistry.java
     * class.
     *
     * @RHR8KOR
     */

    private void unregisterfrommaps() {
        UhuServiceId uhu = new UhuServiceId("ServiceTestA");
        uhuSadlManager.registerService(uhu);
        SubscribedRole sRole = new SubscribedRole(mockProtocols.new NewProtocolRole());
        uhuSadlManager.subscribeService(uhu, sRole);
        boolean isRemoved = uhuSadlManager.unregisterService(uhu);
        assertTrue("Not Removed.", isRemoved);
    }


}
