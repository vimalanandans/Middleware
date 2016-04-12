/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.device;

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
import com.bezirk.sphere.impl.DeviceInformation;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class AddDevice {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(AddDevice.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:AddDevice TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:AddDevice TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#addDevice(String deviceId, DeviceInformation deviceInformation)}.
	 * 
	 * <br>When valid DeviceId and DeviceInformation object is passed to addDevice,
	 * it should return True
	 */
	@Test
	public final void validDeviceIdAndDeviceInformationReturnsTrue() {		
		String deviceId = UUID.randomUUID().toString();
		DeviceInformation deviceInformation = new DeviceInformation();
		registry.devices.put(deviceId, deviceInformation);
		assertTrue(sphereRegistryWrapper.addDevice(deviceId, deviceInformation));	
		
		//verify persisted device with the device created for testing
		assertTrue(registry.devices.get(deviceId).equals(deviceInformation));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addDevice(String deviceId, DeviceInformation deviceInformation)}.
	 * 
	 * <br>Test behavior of addDevice when deviceId is null.
	 * addDevice is expected to return false.
	 */
	@Test
	public final void nullDeviceIdShouldReturnFalse() {		
		String deviceId = null;
		DeviceInformation deviceInformation = new DeviceInformation();
		registry.devices.put(deviceId, deviceInformation);
		assertFalse(sphereRegistryWrapper.addDevice(deviceId, deviceInformation));	
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addDevice(String deviceId, DeviceInformation deviceInformation)}.
	 * 
	 * <br>Test behavior of addDevice when DeviceInformation object is null.
	 * addDevice is expected to return false.
	 */
	@Test
	public final void nullDeviceInformationObjectShouldReturnFalse() {		
		String deviceId = UUID.randomUUID().toString();
		DeviceInformation deviceInformation = null;
		assertFalse(sphereRegistryWrapper.addDevice(deviceId, deviceInformation));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addDevice(String deviceId, DeviceInformation deviceInformation)}.
	 * 
	 * <br>Test behavior of addDevice when DeviceInformation object and deviceId are passed as null.
	 * addDevice is expected to return false.
	 */
	@Test
	public final void nullDeviceInformationObjectAndNullDeviceIdShouldReturnFalse() {		
		String deviceId = null;
		DeviceInformation deviceInformation = null;
		assertFalse(sphereRegistryWrapper.addDevice(deviceId, deviceInformation));
	}


}
