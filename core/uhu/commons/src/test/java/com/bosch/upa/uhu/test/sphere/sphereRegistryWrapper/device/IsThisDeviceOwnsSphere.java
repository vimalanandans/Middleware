/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.device;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.api.objects.UhuSphereInfo;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh
 *
 */
public class IsThisDeviceOwnsSphere {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(IsThisDeviceOwnsSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:IsThisDeviceOwnsSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:IsThisDeviceOwnsSphere TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#isThisDeviceOwnsSphere(UhuSphereInfo)}.
	 * 
	 * <br>Test if the device owns sphere.
	 */
	@Test
	public final void validSphereInfo() {
		String sphereId = UUID.randomUUID().toString();
		Sphere ownerSphere = new OwnerSphere(sphereTestUtility.OWNER_SPHERE_NAME_1,
				sphereTestUtility.DEVICE_1.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_HOME);
		registry.spheres.put(sphereId, ownerSphere);
		UhuSphereInfo sphereInfo = new UhuSphereInfo(sphereId, ownerSphere.getSphereName(), ownerSphere.getSphereType(),null, null);
		assertTrue(sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#isThisDeviceOwnsSphere(UhuSphereInfo)}.
	 * 
	 * <br>Test if the device owns sphere, but the sphere is not added to registry.
	 *  Returns false.
	 */
	@Test
	public final void sphereNotPresentInRegistry() {
		String sphereId = UUID.randomUUID().toString();
		Sphere ownerSphere = new OwnerSphere(sphereTestUtility.OWNER_SPHERE_NAME_1,
				sphereTestUtility.DEVICE_1.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_HOME);
		UhuSphereInfo sphereInfo = new UhuSphereInfo(sphereId, ownerSphere.getSphereName(), ownerSphere.getSphereType(),null, null);
		assertFalse(sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo));	
	}
	
}
