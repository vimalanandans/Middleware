/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class ContainsService {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ContainsService.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:ContainsService TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:ContainsService TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#containsService(String)}.
	 * 
	 * <br>Check if the service exists in the registry.
	 *  containsService should return True when valid service id is passed.
	 */
	@Test
	public final void validServiceIdShouldReturnTrue() {
		String serviceId = UUID.randomUUID().toString();
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString();
		sphereSet.add(sphereId);
		registry.sphereMembership.put(serviceId, new OwnerService("serviceName", "ownerDeviceId", sphereSet));
		assertTrue(sphereRegistryWrapper.containsService(serviceId));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#containsService(String)}.
	 * 
	 * <br>Check if the service exists in the registry.
	 *  containsService should return False when invalid service id is passed.
	 */
	@Test
	public final void invalidServiceIdShouldReturnFalse() {
		//Create a service id, but don't add it to the registry
		String serviceId = UUID.randomUUID().toString();
		assertFalse(sphereRegistryWrapper.containsService(serviceId));
	}

	/**
	 * Test method for {@link SphereRegistryWrapper#containsService(String)}.
	 * 
	 * <br>Test the behavior of containsService when null is passed.
	 *  containsService should return False
	 */
	@Test
	public final void nullServiceIdShouldReturnFalse() {
		String serviceId = null;
		assertFalse(sphereRegistryWrapper.containsService(serviceId));
	}

}

