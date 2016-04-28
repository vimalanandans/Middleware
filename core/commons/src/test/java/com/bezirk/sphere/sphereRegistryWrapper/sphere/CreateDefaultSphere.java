/**
 *
 */
package com.bezirk.sphere.sphereRegistryWrapper.sphere;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkDevMode.Mode;
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

import static org.junit.Assert.assertTrue;

/**
 * @author rishabh
 */
public class CreateDefaultSphere {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(CreateDefaultSphere.class);
    private static SphereRegistry registry;
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereTestUtility sphereTestUtility;
    private static ISphereConfig sphereConfig;
    private static int spheres = 0;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up SphereRegistryWrapper:CreateDefaultSphere TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        sphereConfig = mockSetUp.sphereConfig;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(
                mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down SphereRegistryWrapper:CreateDefaultSphere TestCase *****");
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
     * Test method for
     * {@link SphereRegistryWrapper#createDefaultSphere(String)}
     * .
     * <p/>
     * Test default sphere changes after creating the default sphere again with
     * the same name
     */
    @Test
    public final void createDefaultSpherePostInitSameName() {
        log.info("No of spheres before creating second default sphere: " + registry.spheres.size());
        sphereRegistryWrapper.createDefaultSphere(sphereConfig.getDefaultSphereName());
        log.info("No of spheres after creating second default sphere: " + registry.spheres.size());

        assertTrue("Invalid No. of spheres in registry", registry.spheres.size() == spheres);
        assertTrue("sphere Name invalid", registry.spheres.entrySet().iterator().next().getValue().getSphereName()
                .equals(sphereConfig.getDefaultSphereName()));
        // TODO : Add name check for the default sphere based on upaDevice
    }

    /**
     * Test method for
     * {@link SphereRegistryWrapper#createDefaultSphere(String)}
     * .
     * <p/>
     * Test default sphere changes after creating the default sphere again with
     * a different name
     */
    @Test
    public final void createDefaultSpherePostInitDifferentName() {
        String newSphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
        log.info("No of spheres before creating second default sphere: " + registry.spheres.size());
        sphereRegistryWrapper.createDefaultSphere(newSphereName);
        log.info("No of spheres after creating second default sphere: " + registry.spheres.size());

        assertTrue("Invalid No. of spheres in registry", registry.spheres.size() == spheres);
        assertTrue("sphere Name invalid",
                registry.spheres.entrySet().iterator().next().getValue().getSphereName().equals(newSphereName));
    }

}
