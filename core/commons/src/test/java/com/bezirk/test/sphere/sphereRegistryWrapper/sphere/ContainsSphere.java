/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.sphere;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class ContainsSphere {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ContainsSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:ContainsSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:ContainsSphere TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#containsSphere(String)}.
	 * 
	 * <br>Test if the sphere id exists in the registry. 
	 */
	@Test
	public final void validSphere() {
		String sphereId = UUID.randomUUID().toString();
		registry.spheres.put(sphereId, new OwnerSphere());
		assertTrue(sphereRegistryWrapper.containsSphere(sphereId));
	}

	/**
	 * Test method for {@link SphereRegistryWrapper#containsSphere(String)}.
	 * 
	 * <br>Test behavior of containsSphere method when null String(SphereId) is passed to it.
	 * containsSphere method is expected to return False.
	 */
	@Test
	public final void nullSphereIdShouldReturnFalse() {
		String sphereId = null;
		assertFalse(sphereRegistryWrapper.containsSphere(sphereId));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#containsSphere(String)}.
	 * 
	 * <br>Test behavior of containsSphere method when the sphere does not exist in registry.
	 * containsSphere method is expected to return False.
	 */
	@Test
	public final void sphereNotInRegistryReturnsFalse() {
		String sphereId = UUID.randomUUID().toString();
		assertFalse(sphereRegistryWrapper.containsSphere(sphereId));
	}
	
}
