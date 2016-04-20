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

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.objects.UhuServiceInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class GetUhuServiceInfo{

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static UPADeviceInterface upaDevice;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(GetUhuServiceInfo.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:GetUhuServiceInfo TestCase *****");
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
		log.info("***** Shutting down SphereRegistryWrapper:GetUhuServiceInfo TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#getUhuServiceInfo(Iterable)}.
	 * 
	 * <br>When valid ServiceId objects are passed,
	 *  it should return List of UhuServiceInfo objects
	 */
	@Test
	public final void validServiceIdsReturnsTrue() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName, upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		String serviceType1 = "Owner";
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		Service service1 = new OwnerService(serviceName1,
						upaDevice.getDeviceId(), sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		String serviceType2 = "Owner";
		sphereSet2.add(sphereId);
		Service service2 = new OwnerService(serviceName2,
						upaDevice.getDeviceId(), sphereSet2);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);
				
		registry.spheres.put(sphereId, sphere);
		
		List<UhuServiceId> serviceIds = new ArrayList<>();
		serviceIds.add(serviceId1);
		serviceIds.add(serviceId2);
		
		// Create List of UhuServiceInfo objects to compare.
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, serviceType1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, serviceType2, true, true);
		List<UhuServiceInfo> createdServiceInfo = new ArrayList<>();
		createdServiceInfo.add(serviceInfo1);
		createdServiceInfo.add(serviceInfo2);
		
		// Get the list of UhuServiceInfo objects from the registry
		List<UhuServiceInfo> retrievedServices = (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(serviceIds);
		for (int i = 0; i < retrievedServices.size(); i++) {
			UhuServiceInfo retrieved = retrievedServices.get(i);
			UhuServiceInfo created = createdServiceInfo.get(i);
			assertEquals(created.getServiceId(), retrieved.getServiceId());
			assertEquals(created.getServiceName(), retrieved.getServiceName());
			assertEquals(created.getServiceType(), retrieved.getServiceType());	
		}	
	}
	
	
	/**
	 * Test method for {@link SphereRegistryWrapper#getUhuServiceInfo(Iterable)}.
	 * 
	 * <br>When null is passed, it should return Null
	 */
	@Test
	public final void nullServiceIdsReturnsNull() {	
		List<UhuServiceId> serviceIds = null;
		assertNull(sphereRegistryWrapper.getUhuServiceInfo(serviceIds));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#getUhuServiceInfo(Iterable)}.
	 * 
	 * When services don't exist in the registry
	 *  it should return Empty list
	 */
	@Test
	public final void serviceNotAddedToRegistry() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		String serviceType1 = "Owner";
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		String serviceType2 = "Owner";
		sphereSet2.add(sphereId);
				
		registry.spheres.put(sphereId, sphere);
		
		List<UhuServiceId> serviceIds = new ArrayList<>();
		serviceIds.add(serviceId1);
		serviceIds.add(serviceId2);
		
		// Create List of UhuServiceInfo objects to compare.
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, serviceType1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, serviceType2, true, true);
		List<UhuServiceInfo> createdServiceInfo = new ArrayList<>();
		createdServiceInfo.add(serviceInfo1);
		createdServiceInfo.add(serviceInfo2);
		
		List<UhuServiceInfo> retrievedServices = (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(serviceIds);
		assertTrue(retrievedServices.isEmpty());
	}
	
	
	/**
	 * Test method for {@link SphereRegistryWrapper#getUhuServiceInfo(Iterable)}.
	 * 
	 * <br>When valid serviceId exists but no mapping service to that,
	 *  it should return empty lists
	 */
	@Test
	public final void serviceDoesNotExist() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		String serviceType1 = "Owner";
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), null);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		String serviceType2 = "Owner";
		sphereSet2.add(sphereId);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), null);
				
		registry.spheres.put(sphereId, sphere);
		
		List<UhuServiceId> serviceIds = new ArrayList<>();
		serviceIds.add(serviceId1);
		serviceIds.add(serviceId2);
		
		// Create List of UhuServiceInfo objects to compare.
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, serviceType1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, serviceType2, true, true);
		List<UhuServiceInfo> createdServiceInfo = new ArrayList<>();
		createdServiceInfo.add(serviceInfo1);
		createdServiceInfo.add(serviceInfo2);
		
		List<UhuServiceInfo> retrievedServices = (List<UhuServiceInfo>) sphereRegistryWrapper.getUhuServiceInfo(serviceIds);
		assertTrue(retrievedServices.isEmpty());
	}

}
