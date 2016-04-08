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
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author rishabh
 *
 */
public class AddSphere {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(AddSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:AddSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:AddSphere TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addSphere(String, Sphere)}.
	 * 
	 * <br>Test if a sphere is added to the registry.
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addSphere(String, Sphere)}.
	 * 
	 * <br>Test behavior of addSphere when sphereId is passed as null.
	 * addSphere is expected to return false.
	 */
	@Test
	public final void nullSphereIdShouldReturnFalse() {		
		String sphereId = null;
		Sphere sphere = new OwnerSphere();
		assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));	
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addSphere(String, Sphere)}.
	 * 
	 * <br>Test behavior of addSphere when Sphere object is passed as null.
	 * addSphere is expected to return false.
	 */
	@Test
	public final void nullSphereObjectShouldReturnFalse() {		
		String sphereId = UUID.randomUUID().toString();
		Sphere sphere = null;
		assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addSphere(String, Sphere)}.
	 * 
	 * <br>Test behavior of addSphere when Sphere object and sphereId are passed as null.
	 * addSphere is expected to return false.
	 */
	@Test
	public final void nullSphereObjectAndNullSphereIdShouldReturnFalse() {		
		String sphereId = null;
		Sphere sphere = null;
		assertFalse(sphereRegistryWrapper.addSphere(sphereId, sphere));
	}


}
