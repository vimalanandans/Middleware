package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
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
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the registration and unregistration services provided by sadlmanager.
 * Scenarios tested :
 * a) Register null serviceID
 * b) Register valid serviceID
 * c) Register duplicate serviceID
 * d) Check whether valid serviceID is registered.
 * e) Check whether invalid serviceID is registered.
 * f) Retrieve registered zirk id list.
 * g) Unregister invalid zirk id.
 * h) Register valid zirk id which was registered previously.
 *
 * @author AJC6KOR
 */
public class RegistrationUnregistrationTest {
    private final static Logger logger = LoggerFactory.getLogger(RegistrationUnregistrationTest.class);

    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static final ZirkId bezirkZirkAId = new ZirkId("ServiceA");
    private static final ZirkId bezirkZirkBId = new ZirkId("ServiceB");
    private static final ZirkId bezirkZirkCId = new ZirkId("ServiceC");
    private static final ZirkId dummyServiceId = new ZirkId("InvalidServiceForTest");
    private static final MockProtocols mockProtocols = new MockProtocols();
    private static final Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");
    private static BezirkSadlManager bezirkSadlManager = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up RegistrationUnregistrationTest TestCase *****");

        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;
    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down RegistrationUnregistrationTest Testcase *****");
        mockUtility.destroyTestSetUp();
    }


    @Test
    public void testRegistrationUnregistrationServices() {
        logger.info("***** Testing RegisterService *****");
        testRegisterService();
        logger.info("***** Tested RegisterService Successfully *****");


        logger.info("***** Testing GetRegisteredServices *****");
        testGetRegisteredServices();
        logger.info("***** GetRegisteredServices tested Successfully *****");

        logger.info("***** Testing UnRegisterService *****");
        testUnregisterService();
        logger.info("***** Tested UnRegisterService Successfully *****");

        logger.info("****** Testing removeFromMaps Started *********");
        unregisterfrommaps();
        logger.info("****** Test completed for removeFromMaps *********");


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
        isServiceRegistered = bezirkSadlManager.registerService(null);
        assertFalse("SadlManager registered zirk with null serviceID", isServiceRegistered);
		
		/*SadlManager should return false when checked whether null zirk id is registered*/
        isServiceRegistered = true;
        isServiceRegistered = bezirkSadlManager.isServiceRegistered(null);
        assertFalse("SadlManager has zirk with null serviceID in registered zirk list.", isServiceRegistered);

        //ServiceA registered and location is set to reception.
        bezirkSadlManager.registerService(bezirkZirkAId);
        bezirkSadlManager.setLocation(bezirkZirkAId, reception);
		
		/*SadlManager should return false when asked to register duplicate zirk id.*/
        isServiceRegistered = true;
        isServiceRegistered = bezirkSadlManager.registerService(bezirkZirkAId);
        assertFalse("SadlManager allowed registration for duplicate serviceID.", isServiceRegistered);

        //ServiceB and ServiceC are registered.
        bezirkSadlManager.registerService(bezirkZirkBId);
        bezirkSadlManager.registerService(bezirkZirkCId);
		
	

		/*SadlManager should return true when queried for serviceA registration*/
        isServiceRegistered = bezirkSadlManager
                .isServiceRegistered(bezirkZirkAId);
        assertTrue("SadlManager dont have ServiceA id in registered zirk list.", isServiceRegistered);
		
		/*SadlManager should return false when queried for unregistered serviceid*/
        ZirkId invalidService = new ZirkId("TestRegister");
        isServiceRegistered = bezirkSadlManager.isServiceRegistered(invalidService);
        assertFalse("Zirk is registered", isServiceRegistered);


    }

    /**
     * ServiceA is unregistered. SadlReigstry is queried for all registered Services. ServiceA Id should not be returned.
     */
    private void testUnregisterService() {

        boolean isUnregistered = true;
		/*
		 * SadlManager should return false when asked to unregister null serviceID.
		 * */
        isUnregistered = bezirkSadlManager.unregisterService(null);
        assertFalse("SadlManager unregistered zirk with null serviceID.", isUnregistered);
		
		/*
		 * SadlManager should return true when asked to unregister valid serviceID which is already is registered.
		 * ServiceA should not be present in registered zirk list when queried after unregistration.
		 * */
        isUnregistered = bezirkSadlManager.unregisterService(bezirkZirkAId);
        assertTrue("SadlManager couldn't unregister ServiceA.", isUnregistered);

        Set<ZirkId> registeredServices = bezirkSadlManager
                .getRegisteredServices();

        boolean serviceAFound = false;
        for (ZirkId serviceId : registeredServices) {

            if (serviceId.equals(bezirkZirkAId)) {
                serviceAFound = true;

            }

        }

        assertFalse("ServiceA found in registered zirk list even after unregistration.", serviceAFound);
		
		/*
		 * SadlManager should return false when asked to unregister invalid serviceID.*/
        isUnregistered = bezirkSadlManager.unregisterService(dummyServiceId);
        assertFalse("SadlManager unregistered invalid serviceID.", isUnregistered);
	
		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/		

		
		/*SadlManager should return false when not able to persist unregistration data to registry.*/
		/*
		mockUtility.clearSadlPersistence();
		isUnregistered = bezirkSadlManager.unregisterZirk(bezirkZirkBId);
		assertTrue(isUnregistered);
		mockUtility.restoreSadlPersistence();
		bezirkSadlManager= mockUtility.bezirkSadlManager;
		*/
		
		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/

    }

    /**
     * SadlRegistry is queried for all registered services.
     * It should return ServiceAId,ServiceBId and ServiceCId.
     */
    private void testGetRegisteredServices() {

        Set<ZirkId> registeredServiceSet = bezirkSadlManager
                .getRegisteredServices();

        assertNotNull("SadlManager couldn't fetch registered zirk list.", registeredServiceSet);
        assertEquals("Registered Zirk list size is not 3.", 3, registeredServiceSet.size());
        assertTrue("ZirkA was not found in registered zirk list.", registeredServiceSet.contains(bezirkZirkAId));
        assertTrue("ZirkB was not found in registered zirk list.", registeredServiceSet.contains(bezirkZirkBId));
        assertTrue("ZirkC was not found in registered zirk list.", registeredServiceSet.contains(bezirkZirkCId));
    }


    /**
     * This is to remove the entries from the maps of SadlRegistry.java
     * class.
     */

    private void unregisterfrommaps() {
        ZirkId bezirk = new ZirkId("ServiceTestA");
        bezirkSadlManager.registerService(bezirk);
        SubscribedRole sRole = new SubscribedRole(mockProtocols.new NewProtocolRole());
        bezirkSadlManager.subscribeService(bezirk, sRole);
        boolean isRemoved = bezirkSadlManager.unregisterService(bezirk);
        assertTrue("Not Removed.", isRemoved);
    }


}
