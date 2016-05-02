/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.objects.BezirkZirkInfo;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class GetBezirkServiceInfo {
    private static final Logger logger = LoggerFactory.getLogger(GetBezirkServiceInfo.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:GetBezirkServiceInfo TestCase *****");
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
        logger.info("***** Shutting down SphereRegistryWrapper:GetBezirkServiceInfo TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getBezirkServiceInfo(Iterable)}.
     * <p>
     * When valid ZirkId objects are passed, it should return List of BezirkZirkInfo objects
     * </p>
     */
    @Test
    public final void validServiceIdsReturnsTrue() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String serviceType1 = "Owner";
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        String serviceType2 = "Owner";
        sphereSet2.add(sphereId);
        Zirk zirk2 = new OwnerZirk(serviceName2,
                upaDevice.getDeviceId(), sphereSet2);
        registry.sphereMembership.put(serviceId2.getBezirkZirkId(), zirk2);

        registry.spheres.put(sphereId, sphere);

        List<BezirkZirkId> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId1);
        serviceIds.add(serviceId2);

        // Create List of BezirkZirkInfo objects to compare.
        BezirkZirkInfo serviceInfo1 = new BezirkZirkInfo(serviceId1.getBezirkZirkId(), serviceName1, serviceType1, true, true);
        BezirkZirkInfo serviceInfo2 = new BezirkZirkInfo(serviceId2.getBezirkZirkId(), serviceName2, serviceType2, true, true);
        List<BezirkZirkInfo> createdServiceInfo = new ArrayList<>();
        createdServiceInfo.add(serviceInfo1);
        createdServiceInfo.add(serviceInfo2);

        // Get the list of BezirkZirkInfo objects from the registry
        List<BezirkZirkInfo> retrievedServices = (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(serviceIds);
        for (int i = 0; i < retrievedServices.size(); i++) {
            BezirkZirkInfo retrieved = retrievedServices.get(i);
            BezirkZirkInfo created = createdServiceInfo.get(i);
            assertEquals(created.getZirkId(), retrieved.getZirkId());
            assertEquals(created.getZirkName(), retrieved.getZirkName());
            assertEquals(created.getZirkType(), retrieved.getZirkType());
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkServiceInfo(Iterable)}.
     * <p>
     * When <code>null</code> is passed, it should return <code>null</code>
     * </p>
     */
    @Test
    public final void nullServiceIdsReturnsNull() {
        List<BezirkZirkId> serviceIds = null;
        assertNull(sphereRegistryWrapper.getBezirkServiceInfo(serviceIds));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkServiceInfo(Iterable)}.
     * <p>
     * When services don't exist in the registry it should return Empty list
     * </p>
     */
    @Test
    public final void serviceNotAddedToRegistry() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String serviceType1 = "Owner";
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        String serviceType2 = "Owner";
        sphereSet2.add(sphereId);

        registry.spheres.put(sphereId, sphere);

        List<BezirkZirkId> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId1);
        serviceIds.add(serviceId2);

        // Create List of BezirkZirkInfo objects to compare.
        BezirkZirkInfo serviceInfo1 = new BezirkZirkInfo(serviceId1.getBezirkZirkId(), serviceName1, serviceType1, true, true);
        BezirkZirkInfo serviceInfo2 = new BezirkZirkInfo(serviceId2.getBezirkZirkId(), serviceName2, serviceType2, true, true);
        List<BezirkZirkInfo> createdServiceInfo = new ArrayList<>();
        createdServiceInfo.add(serviceInfo1);
        createdServiceInfo.add(serviceInfo2);

        List<BezirkZirkInfo> retrievedServices = (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(serviceIds);
        assertTrue(retrievedServices.isEmpty());
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkServiceInfo(Iterable)}.
     * <p>When valid zirkId exists but no mapping zirk to that,
     * it should return empty lists
     * </p>
     */
    @Test
    public final void serviceDoesNotExist() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String serviceType1 = "Owner";
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), null);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        String serviceType2 = "Owner";
        sphereSet2.add(sphereId);
        registry.sphereMembership.put(serviceId2.getBezirkZirkId(), null);

        registry.spheres.put(sphereId, sphere);

        List<BezirkZirkId> serviceIds = new ArrayList<>();
        serviceIds.add(serviceId1);
        serviceIds.add(serviceId2);

        // Create List of BezirkZirkInfo objects to compare.
        BezirkZirkInfo serviceInfo1 = new BezirkZirkInfo(serviceId1.getBezirkZirkId(), serviceName1, serviceType1, true, true);
        BezirkZirkInfo serviceInfo2 = new BezirkZirkInfo(serviceId2.getBezirkZirkId(), serviceName2, serviceType2, true, true);
        List<BezirkZirkInfo> createdServiceInfo = new ArrayList<>();
        createdServiceInfo.add(serviceInfo1);
        createdServiceInfo.add(serviceInfo2);

        List<BezirkZirkInfo> retrievedServices = (List<BezirkZirkInfo>) sphereRegistryWrapper.getBezirkServiceInfo(serviceIds);
        assertTrue(retrievedServices.isEmpty());
    }

}
