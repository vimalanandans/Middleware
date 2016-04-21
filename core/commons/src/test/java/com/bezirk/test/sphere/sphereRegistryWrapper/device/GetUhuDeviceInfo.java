/**
 *
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.device;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.middleware.objects.UhuDeviceInfo.UhuDeviceRole;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

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
public class GetUhuDeviceInfo {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetUhuDeviceInfo.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetUhuDeviceInfo TestCase *****");
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
        log.info("***** Shutting down SphereRegistryWrapper:GetUhuDeviceInfo TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getUhuDeviceInfo(java.util.Map, HashSet)}.
     * <p/>
     * <br>When valid map of device id and device services is passed, it should return iterable UhuDeviceInfo objects.
     */
    @Test
    public final void validServiceIdsReturnsValidList() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
        UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
        Service service1 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);

        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        List<UhuDeviceInfo> retrievedDevices = (List<UhuDeviceInfo>) sphereRegistryWrapper.getUhuDeviceInfo(deviceServices, (HashSet<String>) defaultSphere.getOwnerDevices());
        for (int i = 0; i < retrievedDevices.size(); i++) {
            UhuDeviceInfo retrieved = retrievedDevices.get(i);
            assertEquals(retrieved.getDeviceId(), upaDevice.getDeviceId());
            assertEquals(retrieved.getDeviceName(), upaDevice.getDeviceName());
            assertEquals(retrieved.getDeviceType(), upaDevice.getDeviceType());
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getUhuDeviceInfo(java.util.Map, HashSet)}.
     * <p/>
     * <br>When owner devices HashSet is invalid, it should return iterable UhuDeviceInfo objects. And the UhuDeviceRole will be UHU_MEMBER
     */
    @Test
    public final void invalidOwnerDevicesReturnsValidList() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
        UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
        Service service1 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);

        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        HashSet<String> invalidOwnerDevices = new HashSet<>();
        invalidOwnerDevices.add("InvalidDevice1");
        invalidOwnerDevices.add("InvalidDevice2");

        // Passing invalid ownerDevices HashSet
        List<UhuDeviceInfo> retrievedDevices = (List<UhuDeviceInfo>) sphereRegistryWrapper.getUhuDeviceInfo(deviceServices, invalidOwnerDevices);
        for (int i = 0; i < retrievedDevices.size(); i++) {
            UhuDeviceInfo retrieved = retrievedDevices.get(i);
            assertEquals(retrieved.getDeviceId(), upaDevice.getDeviceId());
            assertEquals(retrieved.getDeviceName(), upaDevice.getDeviceName());
            assertEquals(retrieved.getDeviceType(), upaDevice.getDeviceType());
            assertEquals(retrieved.getDeviceRole(), UhuDeviceRole.UHU_MEMBER);
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getUhuDeviceInfo(java.util.Map, HashSet)}.
     * <p/>
     * <br>When the device map passed is null, the method should throw an exception.
     */
    @Test(expected = NullPointerException.class)
    public final void nullDeviceMapThrowsException() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
        UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
        Service service1 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);

        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(serviceId1);

        // Passing devices map as null
        sphereRegistryWrapper.getUhuDeviceInfo(null, (HashSet<String>) defaultSphere.getOwnerDevices());
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getUhuDeviceInfo(java.util.Map, HashSet)}.
     * <p/>
     * <br>When owner devices is passed as null, it should throw an exception
     */
    @Test(expected = NullPointerException.class)
    public final void nullOwnerDevicesThrowsException() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
        UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
        Service service1 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);

        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(serviceId1);

        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        // Passing ownerDevices HashSet as null
        sphereRegistryWrapper.getUhuDeviceInfo(deviceServices, null);
    }

}
