/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.sphere;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.BezirkSphereListener;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.api.ICryptoInternals;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author rishabh
 */
public class CreateSphere {
    private static final Logger logger = LoggerFactory.getLogger(CreateSphere.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static UPADeviceInterface upaDevice;
    private static SphereRegistry registry;
    private static ICryptoInternals crypto;
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereUtils:CreateSphere TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        upaDevice = mockSetUp.upaDevice;
        crypto = mockSetUp.cryptoEngine;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(
                mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereUtils:CreateSphere TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test method for createSphere
     * Tests if sphere with empty name is added to the registry
     */
    @Test
    public final void testRegistrySphereNameEmptyTypeNull() {
        String sphereName = "";
        String sphereId = sphereName + upaDevice.getDeviceId();
        sphereRegistryWrapper.createSphere(sphereName, null, null);
        assertFalse("Bezirk allowed sphere creation with empty name.", registry.spheres.containsKey(sphereId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test method for createSphere.
     * Tests if sphere is added to the registry
     */
    @Test
    public final void testRegistrySphereNameValidTypeNull() {
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        sphereRegistryWrapper.createSphere(sphereName, null, null);
        assertTrue("Bezirk dint allow valid sphere to be created.", registry.spheres.containsKey(sphereId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test method for createSphere
     * Tests if sphere is added to the registry and stays when another request with same name is received
     */
    @Test
    public final void testRegistryTwoCreatesSameName() {
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
        String sphereId1 = sphereRegistryWrapper.createSphere(sphereName, null, null);
        String sphereId2 = sphereRegistryWrapper.createSphere(sphereName, null, null);
        assertEquals(sphereId1, sphereId2);
        assertTrue("sphere Registry missing valid sphere Id.", registry.spheres.containsKey(sphereId1));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test method for createSphere
     * Tests if two spheres with different names are added to the registry
     */
    @Test
    public final void testRegistryTwoCreatesDifferentName() {
        String sphereName1 = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName1 + upaDevice.getDeviceId();
        String sphereName2 = sphereTestUtility.OWNER_SPHERE_NAME_2;
        String sphereId2 = sphereName2 + upaDevice.getDeviceId();

        sphereRegistryWrapper.createSphere(sphereName1, null, null);
        sphereRegistryWrapper.createSphere(sphereName2, null, null);
        assertTrue("Valid sphere ids missing in registry.", registry.spheres.containsKey(sphereId1) && registry.spheres.containsKey(sphereId2));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test method for createSphere
     * Tests creation of keys for a sphereId already added to crypto engine
     */
    @Test
    public final void testCryptoEngine() {
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        sphereRegistryWrapper.createSphere(sphereName, null, null);
        assertFalse("CryptoEngine generated keys for already existing sphereId", crypto.generateKeys(sphereId));
    }


    /**
     * Test method for {@link SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, BezirkSphereListener)}.
     * <p/>
     * Test default sphere creation when sphere name is null
     */
    @Test
    public final void testCreateSphereWithNullName() {
        String sphereId = sphereRegistryWrapper.createSphere(null, BezirkSphereType.BEZIRK_SPHERE_TYPE_OTHER, null);
        assertNotNull("SphereId null when sphere name is null in createSphere request", sphereId);
    }

    //can also add test for checking null input for sphereName and callback, problem with assertion since nothing available to assert

}
