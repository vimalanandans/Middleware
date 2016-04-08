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

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.impl.DeviceInformation;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class GetDeviceName {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	public static UPADeviceInterface upaDevice;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(GetDeviceName.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:GetDeviceName TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		upaDevice = mockSetUp.upaDevice;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:GetDeviceName TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getDeviceName(String)}.
	 * 
	 * <br>Test the behavior of getDeviceName when valid deviceId is passed.
	 *  It should return a Device Name associated with the deviceId
	 */
	@Test
	public final void validDeviceIdReturnsDeviceName() {
		String deviceId = UUID.randomUUID().toString();
		DeviceInformation deviceInformation = new DeviceInformation();
		registry.devices.put(deviceId, deviceInformation);
		assertEquals(sphereRegistryWrapper.getDeviceName(deviceId), deviceInformation.getDeviceName());
	}
	
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getDeviceName(String)}.
	 * 
	 * <br>Test the behavior of getDeviceName method when wrong device id is passed.
	 *  getDeviceName method is expected to return null.
	 */
	@Test
	public final void wrongDeviceIdShouldRetunNull() {
		String deviceId = UUID.randomUUID().toString();
		assertNull(sphereRegistryWrapper.getDeviceInformation(deviceId));
	}
	

}

