/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.sphere;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode.Mode;
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

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class GetSphereIds {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(GetSphereIds.class);
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static ISphereConfig sphereConfig;
    private static int spheres = 0;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:GetSphereIds TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereConfig = mockSetUp.sphereConfig;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:GetSphereIds TestCase *****");
        mockSetUp.destroyTestSetUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        spheres = (sphereConfig.getMode() == Mode.ON) ? 2 : 1;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link SphereRegistryWrapper#getSphereIds()}.
     * <p/>
     * <br>When no other sphere is present, this method should return the sphere Id of default sphere.
     */
    @Test
    public final void validDefaultSphere() {
        assertEquals(sphereRegistryWrapper.getSphereIds().size(), spheres);
    }


    /**
     * Test method for {@link SphereRegistryWrapper#getSphereIds()}.
     * <p/>
     * <br>When another sphere is added to the registry, the method should return 2 sphere Ids - one each for
     */
    @Test
    public final void validSpheres() {
        registry.spheres.put(UUID.randomUUID().toString(), new OwnerSphere());
        assertEquals(sphereRegistryWrapper.getSphereIds().size(), spheres + 1);
    }
}
