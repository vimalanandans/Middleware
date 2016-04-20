/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import com.bezirk.middleware.objects.UhuServiceInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class GetServiceInfo {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static UPADeviceInterface upaDevice;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(GetServiceInfo.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:GetServiceInfo TestCase *****");
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
		log.info("***** Shutting down SphereRegistryWrapper:GetServiceInfo TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#getServiceInfo()}.
	 * 
	 * <br>Test the behavior of getServiceInfo.
	 *  It should return list of UhuServiceInfo objects
	 */
	@Test
	public final void validServices() {
		
		String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
	   
		Sphere defaultSphere = registry.spheres.get(defaultSphereId);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		String serviceType1 = "Owner";
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(defaultSphereId);
		Service service1 = new OwnerService(serviceName1,
						upaDevice.getDeviceId(), sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		String serviceType2 = "Owner";
		sphereSet2.add(defaultSphereId);
		Service service2 = new OwnerService(serviceName2,
						upaDevice.getDeviceId(), sphereSet2);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);
		
		ArrayList<UhuServiceId> services = new ArrayList<>();
		services.add(serviceId1);
		services.add(serviceId2);
		
		LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
		deviceServices.put(upaDevice.getDeviceId(), services);
		defaultSphere.setDeviceServices(deviceServices);
				
		registry.spheres.put(defaultSphereId, defaultSphere);
		
		// Create List of UhuServiceInfo objects to compare.
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, serviceType1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, serviceType2, true, true);
		List<UhuServiceInfo> createdServiceInfo = new ArrayList<>();
		createdServiceInfo.add(serviceInfo1);
		createdServiceInfo.add(serviceInfo2);
		
		List<UhuServiceInfo> retrievedServices = (List<UhuServiceInfo>) sphereRegistryWrapper.getServiceInfo();
		for (int i = 0; i < retrievedServices.size(); i++) {
			UhuServiceInfo retrieved = retrievedServices.get(i);
			UhuServiceInfo created = createdServiceInfo.get(i);
			assertEquals(created.getServiceId(), retrieved.getServiceId());
			assertEquals(created.getServiceName(), retrieved.getServiceName());
			assertEquals(created.getServiceType(), retrieved.getServiceType());	
		}		
	}
	
	
	/**
	 * Test method for {@link SphereRegistryWrapper#getServiceInfo()}.
	 * 
	 * <br>Test the behavior of getServiceInfo when no devices are registered.
	 *  It should return null
	 */
	@Test
	public final void noDevicesRegisteredWillReturnNull() {
		
		String defaultSphereId = mockSetUp.getDefaultSphereId(registry);
		Sphere defaultSphere = registry.spheres.get(defaultSphereId);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.OWNER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(defaultSphereId);
		Service service1 = new OwnerService(serviceName1,
						upaDevice.getDeviceId(), sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.OWNER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(defaultSphereId);
		Service service2 = new OwnerService(serviceName2,
						upaDevice.getDeviceId(), sphereSet2);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);
				
		registry.spheres.put(defaultSphereId, defaultSphere);
		
		assertNull(sphereRegistryWrapper.getServiceInfo());
	}
	

}
