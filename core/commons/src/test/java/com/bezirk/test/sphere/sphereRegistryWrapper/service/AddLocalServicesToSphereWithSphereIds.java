/**
 *
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.impl.UhuSphere;
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

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class AddLocalServicesToSphereWithSphereIds {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(AddLocalServicesToSphereWithSphereIds.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static UhuSphere uhuSphere;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:AddLocalServicesToSphereWithSphereIds TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        upaDevice = mockSetUp.upaDevice;
        uhuSphere = mockSetUp.uhuSphere;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:AddLocalServicesToSphereWithSphereIds TestCase *****");
        mockSetUp.destroyTestSetUp();
        sphereTestUtility = null;
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
     * Test method for {@link SphereRegistryWrapper#addLocalServicesToSphere(String)}.
     * <p/>
     * <br>When valid SphereId is passed,
     * it should return True
     */
    @Test
    public final void validSphereIdReturnsTrue() {

        // Create default sphere
        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        //Create service 1
        String serviceName1 = sphereTestUtility.MEMBER_SERVICE_NAME_1;
        UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
        Service service1 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);

        //Create service 2
        String serviceName2 = sphereTestUtility.MEMBER_SERVICE_NAME_2;
        UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
        Service service2 = new OwnerService(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);

        ArrayList<UhuServiceId> services = new ArrayList<>();
        services.add(serviceId1);
        services.add(serviceId2);

        LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        //add this sphereId to the sphere set of the services
        String testSphereId = uhuSphere.createSphere("TESTDEFAULTSPHERE", UhuSphereType.UHU_SPHERE_TYPE_OTHER);

        assertTrue(sphereRegistryWrapper.addLocalServicesToSphere(testSphereId));
    }

}
