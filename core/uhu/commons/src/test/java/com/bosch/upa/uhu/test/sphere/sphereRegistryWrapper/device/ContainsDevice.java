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

import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.impl.DeviceInformation;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class ContainsDevice {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ContainsDevice.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:ContainsDevice TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:ContainsDevice TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#containsDevice(String)}.
	 * 
	 * <br>Test the behavior of containsDevice when valid deviceId is passed.
	 *  containsDevice should return True
	 */
	@Test
	public final void validDeviceIdShouldReturnTrue() {
		String deviceId = UUID.randomUUID().toString();
		registry.devices.put(deviceId, new DeviceInformation());
		assertTrue(sphereRegistryWrapper.containsDevice(deviceId));
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#containsDevice(String)}.
	 * 
	 * <br>Test the behavior of containsDevice when null is passed.
	 *  containsDevice should return False
	 */
	@Test
	public final void nullDeviceIdShouldReturnFalse() {
		String deviceId = null;
		assertFalse(sphereRegistryWrapper.containsDevice(deviceId));
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#containsDevice(String)}.
	 * 
	 * <br>Test the behavior of containsDevice when wrong device id is passed.
	 *  containsDevice should return False
	 */
	@Test
	public final void wrongDeviceIdShouldReturnFalse() {
		String deviceId = UUID.randomUUID().toString();
		registry.devices.put(deviceId, new DeviceInformation());
		assertFalse(sphereRegistryWrapper.containsDevice("InvalidDeviceId"));
	}


}

