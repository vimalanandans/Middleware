/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.Device;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class RegisterService {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:RegisterService TestCase *****");
		mockSetUp.setUPTestEnv();
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:RegisterService TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#registerService(UhuServiceId, String)}.
	 * 
	 * <br>When valid ServiceId and a new Service name is passed to registerService,
	 * it should add the service membership to the registry and return True.
	 */
	@Test
	public final void validServiceIdAndNewServiceNameReturnsTrue() {		
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId uhuServiceId = new UhuServiceId(serviceId);
		String serviceName = sphereTestUtility.OWNER_SERVICE_NAME_1;
		assertTrue(sphereRegistryWrapper.registerService(uhuServiceId, serviceName));	
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#registerService(UhuServiceId, String)}.
	 * 
	 * <br>When valid ServiceId and a new Service name is passed to registerService,
	 *  it should update the existing service with the name passed and return True.
	 */
	@Test
	public final void newServiceNameForExistingServiceReturnsTrue() {		
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId uhuServiceId = new UhuServiceId(serviceId);
		String serviceName = sphereTestUtility.OWNER_SERVICE_NAME_2; 
		sphereRegistryWrapper.addMembership(uhuServiceId, sphereRegistryWrapper.getDefaultSphereId(), Device.DEVICE_ID, serviceName);
		
		// When a new service name "newName" is passed for the same service Id, registerService method must return true.
		assertTrue(sphereRegistryWrapper.registerService(uhuServiceId, "newName"));	
	}
	
}
