/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.device;

import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
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

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class IsThisDeviceOwnsSphere {
    private static final Logger logger = LoggerFactory.getLogger(IsThisDeviceOwnsSphere.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:IsThisDeviceOwnsSphere TestCase *****");
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
        logger.info("***** Shutting down SphereRegistryWrapper:IsThisDeviceOwnsSphere TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#isThisDeviceOwnsSphere(BezirkSphereInfo)}.
     * <p>
     * Test if the device owns sphere.
     * </p>
     */
    @Test
    public final void validSphereInfo() {
        String sphereId = UUID.randomUUID().toString();
        Sphere ownerSphere = new OwnerSphere(sphereTestUtility.OWNER_SPHERE_NAME_1,
                sphereTestUtility.DEVICE_1.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME);
        registry.spheres.put(sphereId, ownerSphere);
        BezirkSphereInfo sphereInfo = new BezirkSphereInfo(sphereId, ownerSphere.getSphereName(), ownerSphere.getSphereType(), null, null);
        assertTrue(sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#isThisDeviceOwnsSphere(BezirkSphereInfo)}.
     * <p>
     * Test if the device owns sphere, but the sphere is not added to registry.
     * Returns <code>false</code>.
     * </p>
     */
    @Test
    public final void sphereNotPresentInRegistry() {
        String sphereId = UUID.randomUUID().toString();
        Sphere ownerSphere = new OwnerSphere(sphereTestUtility.OWNER_SPHERE_NAME_1,
                sphereTestUtility.DEVICE_1.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME);
        BezirkSphereInfo sphereInfo = new BezirkSphereInfo(sphereId, ownerSphere.getSphereName(), ownerSphere.getSphereType(), null, null);
        assertFalse(sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo));
    }

}
