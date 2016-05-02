/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.service;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.impl.OwnerZirk;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class IsServiceInSphere {
    private static final Logger logger = LoggerFactory.getLogger(IsServiceInSphere.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:IsServiceInSphere TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:IsServiceInSphere TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#isServiceInSphere(BezirkZirkId, String)}.
     * <p>
     * Test the behavior of isZirkInSphere when valid zirk and sphereId is passed.
     * isZirkInSphere should return True if the sphere id is in the sphere set of the zirk.
     * </p>
     */
    @Test
    public final void validServiceAndSphereIdShouldReturnTrue() {

        //Create zirk and sphere set
        String serviceId = UUID.randomUUID().toString();
        BezirkZirkId service = new BezirkZirkId(serviceId);
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        OwnerZirk ownerService = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);

        registry.spheres.put(sphereId, new OwnerSphere());
        registry.sphereMembership.put(serviceId, ownerService);
        sphereRegistryWrapper.addService(serviceId, ownerService);

        assertTrue(sphereRegistryWrapper.isServiceInSphere(service, sphereId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#isServiceInSphere(BezirkZirkId, String)}.
     * <p>
     * Test the behavior of isZirkInSphere when valid zirk and sphereId is passed.
     * But the sphereId is not part of the sphere set.
     * isZirkInSphere should return <code>false</code>
     * </p>
     */
    @Test
    public final void wrongSphereIdShouldReturnFalse() {

        // create a zirk and zirk set
        String serviceId = UUID.randomUUID().toString();
        BezirkZirkId service = new BezirkZirkId(serviceId);
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();

        sphereSet.add("abcd"); //The above created sphereId is not added to the sphere set.
        OwnerZirk ownerService = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);

        registry.spheres.put(sphereId, new OwnerSphere());
        registry.sphereMembership.put(serviceId, ownerService);
        sphereRegistryWrapper.addService(serviceId, ownerService);

        // It should return false because the sphere id in the sphere set is "abcd", not the sphereId which is being passed here
        assertFalse(sphereRegistryWrapper.isServiceInSphere(service, sphereId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#isServiceInSphere(BezirkZirkId, String)}.
     * <p>
     * Test the behavior of isZirkInSphere when wrong zirk id is passed.
     * isZirkInSphere should return <code>false</code>
     * </p>
     */
    @Test
    public final void wrongServiceIdShouldReturnFalse() {

        // create a zirk and zirk set but not added to registry
        String serviceId = UUID.randomUUID().toString();
        BezirkZirkId service = new BezirkZirkId(serviceId);
        String sphereId = UUID.randomUUID().toString();

        // It should return false because the zirk id is not added to registry
        assertFalse(sphereRegistryWrapper.isServiceInSphere(service, sphereId));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#isServiceInSphere(BezirkZirkId, String)}.
     * <p>
     * Test the behavior of isZirkInSphere when zirk id is passed as null.
     * isZirkInSphere should throw an exception
     * </p>
     */
    @Test(expected = NullPointerException.class)
    public final void nullServiceIdThrowsException() {

        // create a zirk and zirk set but not added to registry
        String sphereId = UUID.randomUUID().toString();

        // The method will throw an exception if zirk id is null
        sphereRegistryWrapper.isServiceInSphere(null, sphereId);
    }

    /**
     * Test method for {@link SphereRegistryWrapper#isServiceInSphere(BezirkZirkId, String)}.
     * <p>
     * Test the behavior of isZirkInSphere when null sphereId is passed.
     * isZirkInSphere should return <code>false</code>.
     * </p>
     */
    @Test
    public final void nullSphereIdReturnsFalse() {

        //Create zirk and sphere set
        String serviceId = UUID.randomUUID().toString();
        BezirkZirkId service = new BezirkZirkId(serviceId);
        HashSet<String> sphereSet = new HashSet<String>();
        String sphereId = UUID.randomUUID().toString();
        sphereSet.add(sphereId);
        OwnerZirk ownerService = new OwnerZirk("serviceName", "ownerDeviceId", sphereSet);

        registry.spheres.put(sphereId, new OwnerSphere());
        registry.sphereMembership.put(serviceId, ownerService);
        sphereRegistryWrapper.addService(serviceId, ownerService);

        // Pass sphere id as null string
        assertFalse(sphereRegistryWrapper.isServiceInSphere(service, null));
    }

}

