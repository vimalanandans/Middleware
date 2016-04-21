/**
 *
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

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

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetService.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetService TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:GetService TestCase *****");
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
     * <p/>
     * <br> Test the behavior of getService when valid serviceId is passed.
     * It should return a Service object associated with the serviceId
     */
    @Test
    public final void validServiceId() {
        //Create a service and add it to the registry.
        String serviceId = UUID.randomUUID().toString();
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        OwnerService ownerService = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
        registry.sphereMembership.put(serviceId, ownerService);

        Service retrievedService = sphereRegistryWrapper.getService(serviceId);

        assertEquals(ownerService, retrievedService);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getService(String)}.
     * <p/>
     * <br>Test the behavior of getService method when null string is passed to it.
     * getService method is expected to return null.
     */
    @Test
    public final void nullServiceIdShouldRetunNull() {
        String serviceId = null;
        Service retrievedService = sphereRegistryWrapper.getService(serviceId);
        assertNull(retrievedService);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getService(String)}.
     * <p/>
     * <br>When service is not in the registry,
     * getService method is expected to return null.
     */
    @Test
    public final void serviceNotInRegistryRetunsNull() {
        String serviceId = UUID.randomUUID().toString();
        Service retrievedService = sphereRegistryWrapper.getService(serviceId);
        assertNull(retrievedService);
    }


}
