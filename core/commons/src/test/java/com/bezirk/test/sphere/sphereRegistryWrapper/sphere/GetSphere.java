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
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh & karthik
 *
 */
public class GetSphere {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(GetSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:GetSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(
				mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:GetSphere TestCase *****");
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
	 * Test method for
	 * {@link SphereRegistryWrapper#getSphere(String)}
	 * 
	 * <br>
	 * Test if the getSphere method returns the sphere object for the given
	 * sphereId
	 */
	@Test
	public final void validSphereId() {
		String sphereId = UUID.randomUUID().toString();
		Sphere ownerSphere = new OwnerSphere(
				sphereTestUtility.OWNER_SPHERE_NAME_1,
				sphereTestUtility.DEVICE_1.getDeviceId(),
				UhuSphereType.UHU_SPHERE_TYPE_HOME);
		registry.spheres.put(sphereId, ownerSphere);

		Sphere retrievedSphere = sphereRegistryWrapper.getSphere(sphereId);
		assertEquals(ownerSphere, retrievedSphere);
	}

	/**
	 * Test method for
	 * {@link SphereRegistryWrapper#getSphere(String)}
	 * 
	 * <br>
	 * Test the behavior of getSphere method when null string is passed to it.
	 * getSphere method is expected to return null.
	 */
	@Test
	public final void nullSphereIdShouldRetunNull() {
		String sphereId = null;
		Sphere retrievedSphere = sphereRegistryWrapper.getSphere(sphereId);
		assertNull(retrievedSphere);
	}

	/**
	 * Test method for
	 * {@link SphereRegistryWrapper#getSphere(String)}
	 * 
	 * <br>
	 * The getSphere method should return null when there is no mapping Sphere
	 * object for the passed sphereId.
	 */
	@Test
	public final void sphereNotInRegistryReturnsNull() {
		String sphereId = UUID.randomUUID().toString();
		assertNull(sphereRegistryWrapper.getSphere(sphereId));
	}

}
