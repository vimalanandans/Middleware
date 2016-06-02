/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.impl.OwnerZirk;
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
    private static final Logger logger = LoggerFactory.getLogger(GetServiceName.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:GetServiceName TestCase *****");
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
        logger.info("***** Shutting down SphereRegistryWrapper:GetServiceName TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getServiceName(ZirkId)}.
     * <p>
     * Test the behavior of getZirkName when valid zirkId is passed.
     * It should return name associated with the zirk Id
     * </p>
     */
    @Test
    public final void validServiceIdReturnsTheServiceName() {
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);

        String serviceId = UUID.randomUUID().toString();
        ZirkId zirkId = new ZirkId(serviceId);
        String serviceName = sphereTestUtility.OWNER_ZIRK_NAME_1;
        OwnerZirk ownerService = new OwnerZirk(serviceName, "ownerDeviceId", sphereSet);
        registry.sphereMembership.put(serviceId, ownerService);

        String retrievedService = sphereRegistryWrapper.getServiceName(zirkId);
        assertEquals(serviceName, retrievedService);
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getServiceName(ZirkId)}.
     * <p>
     * Test the behavior of getZirkName when zirkId passed is not present in the registry.
     * It should return <code>null</code>.
     * </p>
     */
    @Test
    public final void serviceNotPresentInRegistryReturnsNull() {
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);

        //Zirk is created but not added in registry.
        String serviceId = UUID.randomUUID().toString();
        ZirkId zirkId = new ZirkId(serviceId);

        String retrievedService = sphereRegistryWrapper.getServiceName(zirkId);
        assertNull(retrievedService);
    }


    //Test to check the behavior of the method when null is passed is not done as zirk id cannot be null
    //as per the contract of the method.


}
