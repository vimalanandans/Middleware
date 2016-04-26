/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.objects.BezirkZirkInfo;
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
public class GetServiceInfo {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetServiceInfo.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetServiceInfo TestCase *****");
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
        log.info("***** Shutting down SphereRegistryWrapper:GetServiceInfo TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getServiceInfo()}.
     * <p/>
     * <br>Test the behavior of getServiceInfo.
     * It should return list of BezirkZirkInfo objects
     */
    @Test
    public final void validServices() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);

        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String serviceType1 = "Owner";
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(defaultSphereId);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        String serviceType2 = "Owner";
        sphereSet2.add(defaultSphereId);
        Zirk zirk2 = new OwnerZirk(serviceName2,
                upaDevice.getDeviceId(), sphereSet2);
        registry.sphereMembership.put(serviceId2.getBezirkZirkId(), zirk2);

        ArrayList<BezirkZirkId> services = new ArrayList<>();
        services.add(serviceId1);
        services.add(serviceId2);

        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        registry.spheres.put(defaultSphereId, defaultSphere);

        // Create List of BezirkZirkInfo objects to compare.
        BezirkZirkInfo serviceInfo1 = new BezirkZirkInfo(serviceId1.getBezirkZirkId(), serviceName1, serviceType1, true, true);
        BezirkZirkInfo serviceInfo2 = new BezirkZirkInfo(serviceId2.getBezirkZirkId(), serviceName2, serviceType2, true, true);
        List<BezirkZirkInfo> createdServiceInfo = new ArrayList<>();
        createdServiceInfo.add(serviceInfo1);
        createdServiceInfo.add(serviceInfo2);

        List<BezirkZirkInfo> retrievedServices = (List<BezirkZirkInfo>) sphereRegistryWrapper.getServiceInfo();
        for (int i = 0; i < retrievedServices.size(); i++) {
            BezirkZirkInfo retrieved = retrievedServices.get(i);
            BezirkZirkInfo created = createdServiceInfo.get(i);
            assertEquals(created.getZirkId(), retrieved.getZirkId());
            assertEquals(created.getZirkName(), retrieved.getZirkName());
            assertEquals(created.getZirkType(), retrieved.getZirkType());
        }
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getServiceInfo()}.
     * <p/>
     * <br>Test the behavior of getServiceInfo when no devices are registered.
     * It should return null
     */
    @Test
    public final void noDevicesRegisteredWillReturnNull() {

        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(defaultSphereId);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);
        HashSet<String> sphereSet2 = new HashSet<>();
        sphereSet2.add(defaultSphereId);
        Zirk zirk2 = new OwnerZirk(serviceName2,
                upaDevice.getDeviceId(), sphereSet2);
        registry.sphereMembership.put(serviceId2.getBezirkZirkId(), zirk2);

        registry.spheres.put(defaultSphereId, defaultSphere);

        assertNull(sphereRegistryWrapper.getServiceInfo());
    }


}
