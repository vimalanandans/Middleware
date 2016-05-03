/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.MemberSphere;
import com.bezirk.sphere.impl.OwnerZirk;
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

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class GetSphereMembership {
    private static final Logger logger = LoggerFactory.getLogger(GetSphereMembership.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static BezirkDeviceInterface upaDevice;
    private static SphereRegistry registry;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:GetSphereMembership TestCase *****");
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
        logger.info("***** Shutting down SphereRegistryWrapper:GetSphereMembership TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#getSphereMembership(BezirkZirkId)}.
     * <p>
     * When valid zirkId is passed, it should return the sphere set for that zirk.
     * </p>
     */
    @Test
    public final void validServiceIdReturnsTrue() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere ownerSphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);
        registry.spheres.put(sphereId1, ownerSphere);

        // create member sphere
        String sphereId2 = UUID.randomUUID().toString();
        String sphereName2 = sphereTestUtility.MEMBER_SPHERE_NAME_1;
        String sphereType2 = BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME;
        Sphere memberSphere = new MemberSphere(sphereName2, sphereType2,
                null, null, false);
        registry.spheres.put(sphereId2, memberSphere);

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        sphereSet1.add(sphereId2);
        OwnerZirk ownerService = new OwnerZirk(serviceName1, "ownerDeviceId", sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), ownerService);

        Set<String> retrievedSphereSet = (Set<String>) sphereRegistryWrapper.getSphereMembership(serviceId1);
        assertEquals(sphereSet1, retrievedSphereSet);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getSphereMembership(BezirkZirkId)}.
     * <p>
     * When sphere set for the given zirk id is empty it should throw exception
     * </p>
     */
    @Test(expected = NullPointerException.class)
    public final void sphereSetEmptyShouldThrowException() {

        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = null;
        OwnerZirk ownerService = new OwnerZirk(serviceName1, "ownerDeviceId", sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), ownerService);

        sphereRegistryWrapper.getSphereMembership(serviceId1);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getSphereMembership(BezirkZirkId)}.
     * <p>
     * When invalid zirkId(zirk id does not exist in registry) is passed,
     * it should return <code>null</code>.
     * </p>
     */
    @Test
    public final void invalidServiceIdReturnsTrue() {

        // create owner sphere
        String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
        String sphereId1 = sphereName + upaDevice.getDeviceId();
        Sphere ownerSphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT);
        registry.spheres.put(sphereId1, ownerSphere);


        //Create zirk 1
        String serviceName1 = sphereTestUtility.OWNER_ZIRK_NAME_1;
        BezirkZirkId serviceId1 = new BezirkZirkId(serviceName1);
        HashSet<String> sphereSet1 = new HashSet<>();
        sphereSet1.add(sphereId1);
        OwnerZirk ownerService = new OwnerZirk(serviceName1, "ownerDeviceId", sphereSet1);
        registry.sphereMembership.put(serviceId1.getBezirkZirkId(), ownerService);

        //Create zirk 2 but not added to registry
        String serviceName2 = sphereTestUtility.OWNER_ZIRK_NAME_2;
        BezirkZirkId serviceId2 = new BezirkZirkId(serviceName2);

        // Pass serviceId2 which is not in registry.
        assertNull(sphereRegistryWrapper.getSphereMembership(serviceId2));
    }

}
