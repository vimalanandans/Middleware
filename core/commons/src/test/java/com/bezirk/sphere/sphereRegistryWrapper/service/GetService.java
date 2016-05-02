/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.Zirk;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;

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
public class GetService {
    private static final Logger logger = LoggerFactory.getLogger(GetService.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:GetService TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:GetService TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getService(String)}.
     * <p>
     * Test the behavior of getService when valid zirkId is passed.
     * It should return a Zirk object associated with the zirkId
     * </p>
     */
    @Test
    public final void validServiceId() {
        //Create a zirk and add it to the registry.
        String serviceId = UUID.randomUUID().toString();
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        OwnerZirk ownerService = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);
        registry.sphereMembership.put(serviceId, ownerService);

        Zirk retrievedZirk = sphereRegistryWrapper.getService(serviceId);

        assertEquals(ownerService, retrievedZirk);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getService(String)}.
     * <p>
     * Test the behavior of getService method when null string is passed to it.
     * getService method is expected to return <code>null</code>.
     * </p>
     */
    @Test
    public final void nullServiceIdShouldRetunNull() {
        String serviceId = null;
        Zirk retrievedZirk = sphereRegistryWrapper.getService(serviceId);
        assertNull(retrievedZirk);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getService(String)}.
     * <p>
     * When zirk is not in the registry, getService method is expected to return
     * <code>null</code>.
     * </p>
     */
    @Test
    public final void serviceNotInRegistryRetunsNull() {
        String serviceId = UUID.randomUUID().toString();
        Zirk retrievedZirk = sphereRegistryWrapper.getService(serviceId);
        assertNull(retrievedZirk);
    }


}
