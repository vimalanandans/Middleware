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
public class AddService {
    private static final Logger logger = LoggerFactory.getLogger(AddService.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:AddService TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:AddService TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#addService(String, Zirk)}.
     * <p/>
     * <br>When valid ZirkId and Zirk object is passed to addService, the zirk is added to the registry and the method
     * should return True
     */
    @Test
    public final void validServiceIdAndServiceReturnsTrue() {

        //Create a zirk
        String serviceId = UUID.randomUUID().toString();
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        Zirk zirk = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);
        assertTrue(sphereRegistryWrapper.addService(serviceId, zirk));

        //verify persisted zirk with the zirk created for testing
        assertTrue(registry.sphereMembership.get(serviceId).equals(zirk));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addService(String, Zirk)}.
     * <p/>
     * <br>Test behavior of addService when zirkId is passed as null.
     * addService is expected to return false.
     */
    @Test
    public final void nullServiceIdShouldReturnFalse() {
        String serviceId = null;
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        Zirk zirk = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);
        assertFalse(sphereRegistryWrapper.addService(serviceId, zirk));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addService(String, Zirk)}.
     * <p/>
     * <br>Test behavior of addService when Zirk object is passed as null.
     * addService is expected to return false.
     */
    @Test
    public final void nullServiceObjectShouldReturnFalse() {
        String serviceId = UUID.randomUUID().toString();
        Zirk zirk = null;
        assertFalse(sphereRegistryWrapper.addService(serviceId, zirk));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addService(String, Zirk)}.
     * <p/>
     * <br>Test behavior of addService when Zirk object and zirkId are passed as null.
     * addService is expected to return false.
     */
    @Test
    public final void nullServiceObjectAndNullServiceIdShouldReturnFalse() {
        String serviceId = null;
        Zirk zirk = null;
        assertFalse(sphereRegistryWrapper.addService(serviceId, zirk));
    }


}
