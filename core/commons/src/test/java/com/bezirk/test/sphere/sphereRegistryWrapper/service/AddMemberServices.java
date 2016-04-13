/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.api.objects.UhuDeviceInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;

/**
 * @author rishabh
 *
 */
public class AddMemberServices {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static SphereTestUtility sphereTestUtility;
	private static UPADeviceInterface upaDevice;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(AddMemberServices.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:AddMemberServices TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
		upaDevice = mockSetUp.upaDevice;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:AddMemberServices TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#addMemberServices(UhuDeviceInfo, String, String)}.
	 * 
	 * <br>Test if services are added successfully to the registry and sphere.
	 *  The test should return True.
	 */
	@Test
	public final void validServiceSphereDeviceReturnsTrue() {
		// Create sphere and add to registry
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		registry.spheres.put(sphereId, sphere);
		
		// Create UhuDeviceInfo obj
		UhuDeviceInfo uhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
		
		// Device Id
		String ownerDeviceId = UUID.randomUUID().toString();
		
		assertTrue(sphereRegistryWrapper.addMemberServices(uhuDeviceInfo, sphereId, ownerDeviceId));
		
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addMemberServices(UhuDeviceInfo, String, String)}.
	 * 
	 * <br>Services are not added if there is no sphere in the registry. Hence this test should return False.
	 */
	@Test
	public final void noSphereInRegistryReturnsFalse() {
		// Create sphere and but not added to registry
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
		String sphereId = sphereName + upaDevice.getDeviceId();
			
		// Create UhuDeviceInfo obj
		UhuDeviceInfo uhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
		
		// Device Id
		String ownerDeviceId = UUID.randomUUID().toString();
		
		assertFalse(sphereRegistryWrapper.addMemberServices(uhuDeviceInfo, sphereId, ownerDeviceId));
	}
	
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addMemberServices(UhuDeviceInfo, String, String)}.
	 * 
	 * <br>Create UhuDeviceInfo object without services. The test should return False.
	 */
	@Test
	public final void noServicesRegisteredReturnsFalse() {
		// Create sphere and but not added to registry
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
		String sphereId = sphereName + upaDevice.getDeviceId();
			
		// Create UhuDeviceInfo obj WITHOUT services
		UhuDeviceInfo uhuDeviceInfo = new UhuDeviceInfo(sphereTestUtility.DEVICE_2.getDeviceId(), sphereTestUtility.DEVICE_2.getDeviceName(),
				sphereTestUtility.DEVICE_2.getDeviceType(), null, false, null);
		
		// Device Id
		String ownerDeviceId = UUID.randomUUID().toString();
		
		assertFalse(sphereRegistryWrapper.addMemberServices(uhuDeviceInfo, sphereId, ownerDeviceId));
		
	}

}