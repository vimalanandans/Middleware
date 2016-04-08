/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.sphere;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.ICryptoInternals;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh
 *
 */
public class CreateSphere {

	private static UPADeviceInterface upaDevice;
	private static SphereRegistry registry;	
	private static ICryptoInternals crypto;
	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(CreateSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereUtils:CreateSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		upaDevice = mockSetUp.upaDevice;
		crypto = mockSetUp.cryptoEngine;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(
				mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereUtils:CreateSphere TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test method for createSphere
	 * Tests if sphere with empty name is added to the registry
	 */
	@Test
	public final void testRegistrySphereNameEmptyTypeNull() {		
		String sphereName = "";
		String sphereId = sphereName + upaDevice.getDeviceId();
		sphereRegistryWrapper.createSphere(sphereName , null, null);
		assertFalse("Uhu allowed sphere creation with empty name.",registry.spheres.containsKey(sphereId));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test method for createSphere.
	 * Tests if sphere is added to the registry
	 */
	@Test
	public final void testRegistrySphereNameValidTypeNull() {
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		sphereRegistryWrapper.createSphere(sphereName, null, null);
		assertTrue("Uhu dint allow valid sphere to be created.",registry.spheres.containsKey(sphereId));
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test method for createSphere
	 * Tests if sphere is added to the registry and stays when another request with same name is received
	 */
	@Test
	public final void testRegistryTwoCreatesSameName() {
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
		String sphereId1 = sphereRegistryWrapper.createSphere(sphereName, null, null);
		String sphereId2 = sphereRegistryWrapper.createSphere(sphereName, null, null);
		assertEquals(sphereId1, sphereId2);
		assertTrue("Sphere Registry missing valid Sphere Id.",registry.spheres.containsKey(sphereId1));
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test method for createSphere
	 * Tests if two spheres with different names are added to the registry
	 */
	@Test
	public final void testRegistryTwoCreatesDifferentName() {
		String sphereName1 = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId1 = sphereName1 + upaDevice.getDeviceId();
		String sphereName2 = sphereTestUtility.OWNER_SPHERE_NAME_2;
		String sphereId2 = sphereName2 + upaDevice.getDeviceId();
		
		sphereRegistryWrapper.createSphere(sphereName1, null, null);
		sphereRegistryWrapper.createSphere(sphereName2, null, null);
		assertTrue("Valid sphere ids missing in registry.",registry.spheres.containsKey(sphereId1) && registry.spheres.containsKey(sphereId2));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test method for createSphere
	 * Tests creation of keys for a sphereId already added to crypto engine
	 */
	@Test
	public final void testCryptoEngine() {
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		sphereRegistryWrapper.createSphere(sphereName, null, null);
		assertFalse("CryptoEngine generated keys for already existing sphereId",crypto.generateKeys(sphereId));		
	}


	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#createSphere(java.lang.String, java.lang.String, com.bosch.upa.uhu.sphere.api.IUhuSphereListener)}.
	 * 
	 * Test default sphere creation when sphere name is null	 
	 */
	@Test
	public final void testCreateSphereWithNullName() {
		String sphereId = sphereRegistryWrapper.createSphere(null, UhuSphereType.UHU_SPHERE_TYPE_OTHER, null);
		assertNotNull("SphereId null when sphere name is null in createSphere request",sphereId);
	}
	
	//can also add test for checking null input for sphereName and callback, problem with assertion since nothing available to assert

}
