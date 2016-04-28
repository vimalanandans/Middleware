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

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class AddMembership {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(AddMembership.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static UPADeviceInterface upaDevice;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:AddMembership TestCase *****");
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
        log.info("***** Shutting down SphereRegistryWrapper:AddMembership TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#addMembership(BezirkZirkId, String, String, String)}.
     * <p/>
     * <br>Test if a new zirk is added successfully to the registry.
     * The method should return True.
     */
    @Test
    public final void newServiceIdReturnsTrue() {
        // Create sphere and add to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1 but dont add it to registry
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String ownerDeviceId = upaDevice.getDeviceId();
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);

        registry.spheres.put(sphereId1, sphere);

        assertTrue(sphereRegistryWrapper.addMembership(serviceId1, sphereId1, ownerDeviceId, serviceName1));
    }


    /**
     * Test method for {@link SphereRegistryWrapper#addMembership(BezirkZirkId, String, String, String)}.
     * <p/>
     * <br>If an existing zirk is being added to the registry, then just the zirk name is updated.
     * The method should return True.
     */
    @Test
    public final void existingServiceIdReturnsTrue() {
        // Create sphere and add to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);

        //Create zirk 1 and add it to registry
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String ownerDeviceId = upaDevice.getDeviceId();
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                ownerDeviceId, sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        registry.spheres.put(sphereId1, sphere);

        assertTrue(sphereRegistryWrapper.addMembership(serviceId1, sphereId1, ownerDeviceId, serviceName1));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addMembership(BezirkZirkId, String, String, String)}.
     * <p/>
     * <br>When sphere does not exist, the method will return False
     */
    @Test
    public final void sphereDoesNotExistReturnsFalse() {
        // Create sphere but NOT added to registry
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();

        //Create zirk 1 and add it to registry
        String serviceName1 = sphereTestUtility.MEMBER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        String ownerDeviceId = upaDevice.getDeviceId();
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        Zirk zirk1 = new OwnerZirk(serviceName1,
                ownerDeviceId, sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), zirk1);

        assertFalse(sphereRegistryWrapper.addMembership(serviceId1, sphereId1, ownerDeviceId, serviceName1));
    }
}
