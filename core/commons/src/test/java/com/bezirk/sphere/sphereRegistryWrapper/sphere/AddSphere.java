/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.sphere;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class AddSphere {
    private static final Logger logger = LoggerFactory.getLogger(AddSphere.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up SphereRegistryWrapper:AddSphere TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down SphereRegistryWrapper:AddSphere TestCase *****");
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
     * Test method for {@link SphereRegistryWrapper#addSphere(String, Sphere)}.
     * <p>
     * Test if a sphere is added to the registry.
     * </p>
     */
    @Test
    public final void addSphere() {
        String sphereId = UUID.randomUUID().toString();
        Sphere sphere = new OwnerSphere();
        assertTrue(sphereRegistryWrapper.addSphere(sphereId, new OwnerSphere()));

        //verify persisted sphere with the sphere created for testing
        assertTrue(registry.spheres.get(sphereId).equals(sphere));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addSphere(String, Sphere)}.
     * <p>
     * Test behavior of addSphere when sphereId is passed as null.
     * addSphere is expected to return <code>false</code>.
     * </p>
     */
    @Test
    public final void nullSphereIdShouldReturnFalse() {
        String sphereId = null;
        Sphere sphere = new OwnerSphere();
        assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addSphere(String, Sphere)}.
     * <p>
     * Test behavior of addSphere when sphere object is passed as null.
     * addSphere is expected to return <code>false</code>.
     * </p>
     */
    @Test
    public final void nullSphereObjectShouldReturnFalse() {
        String sphereId = UUID.randomUUID().toString();
        Sphere sphere = null;
        assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));
    }

    /**
     * Test method for {@link SphereRegistryWrapper#addSphere(String, Sphere)}.
     * <p>
     * Test behavior of addSphere when sphere object and sphereId are passed as null.
     * addSphere is expected to return <code>false</code>.
     * </p>
     */
    @Test
    public final void nullSphereObjectAndNullSphereIdShouldReturnFalse() {
        String sphereId = null;
        Sphere sphere = null;
        assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));
    }


}