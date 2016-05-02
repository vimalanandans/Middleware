/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Zirk;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class UpdateMembership {
    private static final Logger logger = LoggerFactory.getLogger(UpdateMembership.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;
    private static Method method;

    @SuppressWarnings("rawtypes")
    private static Class[] parameterTypes;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:UpdateMembership TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        upaDevice = mockSetUp.upaDevice;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        parameterTypes = new Class[2];
        parameterTypes[0] = BezirkZirkId.class;
        parameterTypes[1] = java.lang.String.class;
        method = SphereRegistryWrapper.class.getDeclaredMethod("updateMembership", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:UpdateMembership TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#updateMembership(BezirkZirkId zirkId, String sphereId)}.
     * <p>
     * Test if a new sphere is added successfully to the zirk's sphere set.
     * </p>
     *
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public final void validServiceIdAndNewSphereIdReturnsTrue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Create sphere and add to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        registry.spheres.put(sphereId1, sphere);

        //Create one more sphereId which has to be updated to the zirk
        String sphereId2 = "NewSphere";
        registry.spheres.put(sphereId2, sphere);

        boolean isServiceUpdate = (Boolean) method.invoke(sphereRegistryWrapper, serviceId1, sphereId2);
        assertTrue(isServiceUpdate);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#updateMembership(BezirkZirkId zirkId, String sphereId)}.
     * <p>
     * If existing sphere Id is passed, it returns <code>true</code>.
     * </p>
     *
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public final void validServiceIdAndExistingSphereIdReturnsTrue() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Create sphere and add to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        registry.spheres.put(sphereId1, sphere);

        //Pass sphereId which already exists in the zirk
        boolean isServiceUpdate = (Boolean) method.invoke(sphereRegistryWrapper, serviceId1, sphereId1);
        assertTrue(isServiceUpdate);
    }


    /**
     * Test method for {@link SphereRegistryWrapper#updateMembership(BezirkZirkId zirkId, String sphereId)}.
     * <p>
     * If an invalid zirk ID is passed, the method should return <code>false</code>.
     * </p>
     *
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public final void invalidServiceIdReturnsFalse() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Create sphere and add to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1, but not added to the registry
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);

        registry.spheres.put(sphereId1, sphere);

        boolean isServiceUpdate = (Boolean) method.invoke(sphereRegistryWrapper, serviceId1, sphereId1);
        assertFalse(isServiceUpdate);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#updateMembership(BezirkZirkId zirkId, String sphereId)}.
     * <p>
     * If an invalid sphere ID is passed, the method should return <code>false</code>.
     * </p>
     *
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @Test
    public final void invalidSphereIdReturnsFalse() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Create sphere but don't add it to the registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
        String sphereId1 = sphereName + upaDevice.getDeviceId();

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        boolean isServiceUpdate = (Boolean) method.invoke(sphereRegistryWrapper, serviceId1, sphereId1);
        assertFalse(isServiceUpdate);
    }

}
