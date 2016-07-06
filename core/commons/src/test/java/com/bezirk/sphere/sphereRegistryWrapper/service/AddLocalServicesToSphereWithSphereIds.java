/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.BezirkSphere;
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

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class AddLocalServicesToSphereWithSphereIds {
    private static final Logger logger = LoggerFactory.getLogger(AddLocalServicesToSphereWithSphereIds.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static BezirkDeviceInterface upaDevice;
    private static BezirkSphere bezirkSphere;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:AddLocalServicesToSphereWithSphereIds TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        upaDevice = mockSetUp.upaDevice;
        bezirkSphere = mockSetUp.bezirkSphere;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:AddLocalServicesToSphereWithSphereIds TestCase *****");
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
     * <p>
     * When valid SphereId is passed, it should return <code>true</code>
     * </p>
     */
    @Test
    public final void validSphereIdReturnsTrue() {

        // Create default sphere
        String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
        Sphere defaultSphere = registry.spheres.get(defaultSphereId);

        HashSet<String> spheres = new HashSet<>();
        spheres.add(defaultSphereId);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_1;
        ZirkId serviceId1 = new ZirkId(serviceName1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId1.getZirkId(), zirk1);

        //Create zirk 2
        String serviceName2 = sphereTestUtility.MEMBER_ZIRK_NAME_2;
        ZirkId serviceId2 = new ZirkId(serviceName2);
        Zirk zirk2 = new OwnerZirk(serviceName1,
                upaDevice.getDeviceId(), spheres);
        registry.sphereMembership.put(serviceId2.getZirkId(), zirk2);

        ArrayList<ZirkId> services = new ArrayList<>();
        services.add(serviceId1);
        services.add(serviceId2);

        LinkedHashMap<String, ArrayList<ZirkId>> deviceServices = new LinkedHashMap<>();
        deviceServices.put(upaDevice.getDeviceId(), services);
        defaultSphere.setDeviceServices(deviceServices);

        //add this sphereId to the sphere set of the services
        String testSphereId = bezirkSphere.createSphere("TESTDEFAULTSPHERE", BezirkSphereType.BEZIRK_SPHERE_TYPE_OTHER);

        assertTrue(sphereRegistryWrapper.addLocalServicesToSphere(testSphereId));
    }

}
