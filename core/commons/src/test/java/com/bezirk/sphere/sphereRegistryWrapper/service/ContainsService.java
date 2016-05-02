/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerZirk;
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
public class ContainsService {
    private static final Logger logger = LoggerFactory.getLogger(ContainsService.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:ContainsService TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:ContainsService TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#containsService(String)}.
     * <p>
     * Check if the zirk exists in the registry.
     * containsService should return True when valid zirk id is passed.
     * </p>
     */
    @Test
    public final void validServiceIdShouldReturnTrue() {
        String serviceId = UUID.randomUUID().toString();
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        registry.sphereMembership.put(serviceId, new OwnerZirk("serviceName", "ownerDeviceId", sphereSet));
        assertTrue(sphereRegistryWrapper.containsService(serviceId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#containsService(String)}.
     * <p>
     * Check if the zirk exists in the registry.
     * containsService should return False when invalid zirk id is passed.
     * </p>
     */
    @Test
    public final void invalidServiceIdShouldReturnFalse() {
        //Create a zirk id, but don't add it to the registry
        String serviceId = UUID.randomUUID().toString();
        assertFalse(sphereRegistryWrapper.containsService(serviceId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#containsService(String)}.
     * <p>
     * Test the behavior of containsService when <code>null</code> is passed.
     * containsService should return <code>false</code>
     * </p>
     */
    @Test
    public final void nullServiceIdShouldReturnFalse() {
        String serviceId = null;
        assertFalse(sphereRegistryWrapper.containsService(serviceId));
    }

}

