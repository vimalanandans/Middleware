/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
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
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.MemberSphere;
import com.bosch.upa.uhu.sphere.impl.OwnerService;
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class GetSphereMembership {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static UPADeviceInterface upaDevice;
	private static SphereRegistry registry;
	private static SphereTestUtility sphereTestUtility;
	private static final Logger log = LoggerFactory.getLogger(GetSphereMembership.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:GetSphereMembership TestCase *****");
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
		log.info("***** Shutting down SphereRegistryWrapper:GetSphereMembership TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getSphereMembership(UhuServiceId)}.
	 * 
	 * <br>When valid serviceId is passed,
	 *  it should return the sphere set for that service.
	 */
	@Test
	public final void validServiceIdReturnsTrue() {
		
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId1 = sphereName + upaDevice.getDeviceId();
		Sphere ownerSphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		registry.spheres.put(sphereId1, ownerSphere);
		
		// create member sphere
		String sphereId2 = UUID.randomUUID().toString();
		String sphereName2 = sphereTestUtility.MEMBER_SPHERE_NAME_1;
		String sphereType2 = UhuSphereType.UHU_SPHERE_TYPE_HOME;
		Sphere memberSphere = new MemberSphere(sphereName2, sphereType2,
				null, null, false);
		registry.spheres.put(sphereId2, memberSphere);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId1);
		sphereSet1.add(sphereId2);
		OwnerService ownerService = new OwnerService(serviceName1, "ownerDeviceId", sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), ownerService);
		
		 Set<String> retrievedSphereSet = (Set<String>) sphereRegistryWrapper.getSphereMembership(serviceId1);
		 assertEquals(sphereSet1, retrievedSphereSet);
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getSphereMembership(UhuServiceId)}.
	 * 
	 * <br>When sphere set for the given service id is empty
	 *  it should throw exception
	 */
	@Test(expected=NullPointerException.class)
	public final void sphereSetEmptyShouldThrowException() {
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = null;
		OwnerService ownerService = new OwnerService(serviceName1, "ownerDeviceId", sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), ownerService);
		
		sphereRegistryWrapper.getSphereMembership(serviceId1);
	}
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#getSphereMembership(UhuServiceId)}.
	 * 
	 * <br>When invalid serviceId(service id does not exist in registry) is passed,
	 *  it should return null.
	 */
	@Test
	public final void invalidServiceIdReturnsTrue() {
		
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId1 = sphereName + upaDevice.getDeviceId();
		Sphere ownerSphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		registry.spheres.put(sphereId1, ownerSphere);
		
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId1);
		OwnerService ownerService = new OwnerService(serviceName1, "ownerDeviceId", sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), ownerService);
		
		//Create service 2 but not added to registry
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		
		// Pass serviceId2 which is not in registry.
		assertNull(sphereRegistryWrapper.getSphereMembership(serviceId2));
	}
	
}
