/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.ZirkId;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class AddLocalServicesToSphereWithServiceIds {
    private static final Logger logger = LoggerFactory.getLogger(AddLocalServicesToSphereWithServiceIds.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static BezirkDeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:AddLocalServicesToSphereWithServiceIds TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        upaDevice = mockSetUp.upaDevice;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:AddLocalServicesToSphereWithServiceIds TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#addLocalServicesToSphere(Iterable, String)}.
     * <p>
     * When valid SphereId and ServiceIds objects is passed, it should return <code>true</code>
     * </p>
     */
    @Test
    public final void validSphereIdAndServiceIdsReturnsTrue() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_1;
        ZirkId serviceId1 = new ZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getZirkId(), zirk1);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.MEMBER_ZIRK_NAME_2;
        ZirkId serviceId2 = new ZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        sphereSet2.add(sphereId);
        Zirk zirk2 = new OwnerZirk(serviceName2,
                upaDevice.getDeviceId(), sphereSet2);
        registry.sphereMembership.put(serviceId2.getZirkId(), zirk2);

        registry.spheres.put(sphereId, sphere);

        List<ZirkId> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId1);
        serviceIds.add(serviceId2);

        assertTrue(sphereRegistryWrapper.addLocalServicesToSphere(serviceIds, sphereId));

    }


    /**
     * Test method for {@link SphereRegistryWrapper#addLocalServicesToSphere(Iterable, String)}.
     * <p>
     * When services are not added to registry, it should return <code>true</code>.
     * </p>
     */
    @Test
    public final void servicesNotAddedToRegistryReturnsFalse() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
        String sphereId = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1 but not added to registry
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_3;
        ZirkId serviceId1 = new ZirkId(serviceName1);

        //Create zirk 2 but not added to registry
        String serviceName2 = sphereTestUtility.MEMBER_ZIRK_NAME_4;
        ZirkId serviceId2 = new ZirkId(serviceName2);

        registry.spheres.put(sphereId, sphere);

        List<ZirkId> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId1);
        serviceIds.add(serviceId2);

        assertFalse(sphereRegistryWrapper.addLocalServicesToSphere(serviceIds, sphereId));

    }

}
