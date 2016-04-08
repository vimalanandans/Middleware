/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.sphere;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuDevMode.Mode;
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author rishabh
 *
 */
public class GetSphereIds {

    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static SphereRegistry registry;
    private static ISphereConfig sphereConfig;
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static int spheres = 0;
    private static final Logger log = LoggerFactory.getLogger(GetSphereIds.class);

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
     *  Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getSphereIds()}.
     *  
     * <br>When no other sphere is present, this method should return the sphere Id of default sphere.
     */
    @Test
    public final void validDefaultSphere() {
        assertEquals(sphereRegistryWrapper.getSphereIds().size(), spheres);
    }
    
    
    /**
     * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getSphereIds()}.
	 * 
	 * <br>When another sphere is added to the registry, the method should return 2 sphere Ids - one each for 
     */
    @Test
    public final void validSpheres() {
        registry.spheres.put(UUID.randomUUID().toString(), new OwnerSphere());
        assertEquals(sphereRegistryWrapper.getSphereIds().size(), spheres + 1);
    }
}
