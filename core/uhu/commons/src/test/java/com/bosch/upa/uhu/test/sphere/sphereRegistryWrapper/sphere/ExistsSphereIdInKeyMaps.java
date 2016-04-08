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
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.sphere.security.SphereKeys;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author rishabh
 *
 */
public class ExistsSphereIdInKeyMaps {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ExistsSphereIdInKeyMaps.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:ExistsSphereIdInKeyMaps TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:ExistsSphereIdInKeyMaps TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#existsSphereIdInKeyMaps(String)}.
	 * 
	 * <br>Test existence of the sphereId in sphereKeyMap
	 */
	@Test
	public final void existsSphereIdInKeyMap() {
		String sphereId = UUID.randomUUID().toString();

		// add sphereId into the key map of registry
		registry.sphereKeyMap.put(sphereId, new SphereKeys());
		assertTrue(sphereRegistryWrapper.existsSphereIdInKeyMaps(sphereId));
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#existsSphereIdInKeyMaps(String)}.
	 * 
	 * <br>Test existence of the sphereId in sphereHashKeyMap
	 */
	@Test
	public final void existsSphereIdInHashKeyMap() {
		String sphereId = UUID.randomUUID().toString();

		// add sphereId into the hash key map of registry
		registry.sphereHashKeyMap.put(sphereId, registry.new HashKey(new byte[1], sphereId));
		assertTrue(sphereRegistryWrapper.existsSphereIdInKeyMaps(sphereId));
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#existsSphereIdInKeyMaps(String)}.
	 * 
	 * <br>Test existence of the sphereId in both sphereHashKeyMap and sphereKeyMap
	 */
	@Test
	public final void existsSphereIdInMaps() {
		String sphereId = UUID.randomUUID().toString();

		// add sphereId into the hash key map of registry
		registry.sphereKeyMap.put(sphereId, new SphereKeys());
		registry.sphereHashKeyMap.put(sphereId, registry.new HashKey(new byte[1], sphereId));
		assertTrue(sphereRegistryWrapper.existsSphereIdInKeyMaps(sphereId));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#existsSphereIdInKeyMaps(String)}.
	 * 
	 * <br>Test behavior of existsSphereIdInKeyMaps when SphereId string is passed as null.
	 *  existsSphereIdInKeyMaps method is expected to return False. 
	 */
	@Test
	public final void nullSphereIdShouldReturnFalse() {
		String sphereId = null;
		assertFalse(sphereRegistryWrapper.existsSphereIdInKeyMaps(sphereId));
	}

}
