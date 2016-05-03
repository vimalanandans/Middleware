/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.device;

import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.impl.OwnerZirk;
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
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class GetBezirkDeviceInfo {
    private static final Logger logger = LoggerFactory.getLogger(GetBezirkDeviceInfo.class);

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
        logger.info("***** Setting up SphereRegistryWrapper:GetBezirkDeviceInfo TestCase *****");
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
        logger.info("***** Shutting down SphereRegistryWrapper:GetBezirkDeviceInfo TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getBezirkDeviceInfo(java.util.Map, HashSet)}.
     * <p>When valid map of device id and device services is passed, it should return iterable
     * <code>BezirkDeviceInfo</code> objects.
     * </p>
     */
    @Test
    public final void validServiceIdsReturnsValidList() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        List<BezirkDeviceInfo> retrievedDevices = (List<BezirkDeviceInfo>) sphereRegistryWrapper.getBezirkDeviceInfo(deviceServices, (HashSet<String>) defaultSphere.getOwnerDevices());
        for (int i = 0; i < retrievedDevices.size(); i++) {
            BezirkDeviceInfo retrieved = retrievedDevices.get(i);
            assertEquals(retrieved.getDeviceId(), upaDevice.getDeviceId());
            assertEquals(retrieved.getDeviceName(), upaDevice.getDeviceName());
            assertEquals(retrieved.getDeviceType(), upaDevice.getDeviceType());
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkDeviceInfo(java.util.Map, HashSet)}.
     * <p>
     * When owner devices HashSet is invalid, it should return iterable BezirkDeviceInfo objects.
     * And the BezirkDeviceRole will be BEZIRK_MEMBER
     * </p>
     */
    @Test
    public final void invalidOwnerDevicesReturnsValidList() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        HashSet<String> invalidOwnerDevices = new HashSet<>();
        invalidOwnerDevices.add("InvalidDevice1");
        invalidOwnerDevices.add("InvalidDevice2");

        // Passing invalid ownerDevices HashSet
        List<BezirkDeviceInfo> retrievedDevices = (List<BezirkDeviceInfo>) sphereRegistryWrapper.getBezirkDeviceInfo(deviceServices, invalidOwnerDevices);
        for (int i = 0; i < retrievedDevices.size(); i++) {
            BezirkDeviceInfo retrieved = retrievedDevices.get(i);
            assertEquals(retrieved.getDeviceId(), upaDevice.getDeviceId());
            assertEquals(retrieved.getDeviceName(), upaDevice.getDeviceName());
            assertEquals(retrieved.getDeviceType(), upaDevice.getDeviceType());
            assertEquals(retrieved.getDeviceRole(), BezirkDeviceInfo.BezirkDeviceRole.BEZIRK_MEMBER);
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkDeviceInfo(java.util.Map, HashSet)}.
     * <p>
     * When the device map passed is null, the method should throw an exception.
     * </p>
     */
    @Test(expected = NullPointerException.class)
    public final void nullDeviceMapThrowsException() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(serviceId1);

        // Passing devices map as null
        sphereRegistryWrapper.getBezirkDeviceInfo(null, (HashSet<String>) defaultSphere.getOwnerDevices());
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getBezirkDeviceInfo(java.util.Map, HashSet)}.
     * <p>
     * When owner devices is passed as null, it should throw an exception
     * </p>
     */
    @Test(expected = NullPointerException.class)
    public final void nullOwnerDevicesThrowsException() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        // Passing ownerDevices HashSet as null
        sphereRegistryWrapper.getBezirkDeviceInfo(deviceServices, null);
    }

}
