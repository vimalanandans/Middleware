/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuZirkId;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class GetServiceName {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetServiceName.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetServiceName TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:GetServiceName TestCase *****");
        mockSetUp.destroyTestSetUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getServiceName(UhuZirkId)}.
     * <p/>
     * <br>Test the behavior of getZirkName when valid serviceId is passed.
     * It should return name associated with the service Id
     */
    @Test
    public final void validServiceIdReturnsTheServiceName() {
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);

        String serviceId = UUID.randomUUID().toString();
        UhuZirkId uhuServiceId = new UhuZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_SERVICE_NAME_1;
        OwnerService ownerService = new OwnerService(serviceName, "ownerDeviceId", sphereSet);
        registry.sphereMembership.put(serviceId, ownerService);

        String retrievedService = sphereRegistryWrapper.getServiceName(uhuServiceId);
        assertEquals(serviceName, retrievedService);
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getServiceName(UhuZirkId)}.
     * <p/>
     * <br>Test the behavior of getZirkName when serviceId passed is not present in the registry.
     * It should return null.
     */
    @Test
    public final void serviceNotPresentInRegistryReturnsNull() {
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);

        //Service is created but not added in registry.
        String serviceId = UUID.randomUUID().toString();
        UhuZirkId uhuServiceId = new UhuZirkId(serviceId);

        String retrievedService = sphereRegistryWrapper.getServiceName(uhuServiceId);
        assertNull(retrievedService);
    }


    //Test to check the behavior of the method when null is passed is not done as service id cannot be null
    //as per the contract of the method.


}
