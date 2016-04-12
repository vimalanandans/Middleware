/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class ValidateServices {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static UPADeviceInterface upaDevice;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ValidateServices.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:ValidateServices TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		upaDevice = mockSetUp.upaDevice;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:ValidateServices TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#validateServices(Iterable)}.
	 * 
	 * When valid service Ids are passed, it should return True
	 */
	@Test
	public final void validDeviceIdReturnsTrue() {
		
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), null);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(sphereId);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), null);
				
		registry.spheres.put(sphereId, sphere);
		
		List<UhuServiceId> serviceIds = new ArrayList<>();
		serviceIds.add(serviceId1);
		serviceIds.add(serviceId2);
		
		assertTrue(sphereRegistryWrapper.validateServices(serviceIds));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#validateServices(Iterable)}.
	 * 
	 * <br>When services are not registered, it should return False.
	 */
	@Test
	public final void servicesNotRegisteredReturnsFalse() {
		
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(sphereId);
				
		registry.spheres.put(sphereId, sphere);
		
		List<UhuServiceId> serviceIds = new ArrayList<>();
		serviceIds.add(serviceId1);
		serviceIds.add(serviceId2);
		
		assertFalse(sphereRegistryWrapper.validateServices(serviceIds));
	}
	
}
