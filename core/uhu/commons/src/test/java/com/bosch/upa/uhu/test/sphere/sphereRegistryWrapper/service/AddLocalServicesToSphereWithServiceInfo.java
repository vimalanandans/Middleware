/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.sphereRegistryWrapper.service;

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

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.OwnerService;
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.Service;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class AddLocalServicesToSphereWithServiceInfo {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static SphereTestUtility sphereTestUtility;
	private static UPADeviceInterface upaDevice;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(AddLocalServicesToSphereWithServiceInfo.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:AddLocalServicesToSphereWithServiceInfo TestCase *****");
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
		log.info("***** Shutting down SphereRegistryWrapper:AddLocalServicesToSphereWithServiceInfo TestCase *****");
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
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addLocalServicesToSphere(String, Iterable)}.
	 * 
	 * <br>When valid SphereId and ServiceInfos objects is passed,
	 *  it should return True
	 */
	@Test
	public final void validSphereIdAndServiceInfoReturnsTrue() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1
		String serviceName1 = sphereTestUtility.MEMBER_SERVICE_NAME_1;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		Service service1 = new OwnerService(serviceName1,
						upaDevice.getDeviceId(), sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);
		
		//Create service 2
		String serviceName2 = sphereTestUtility.MEMBER_SERVICE_NAME_2;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(sphereId);
		Service service2 = new OwnerService(serviceName2,
						upaDevice.getDeviceId(), sphereSet2);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);
				
		registry.spheres.put(sphereId, sphere);
		
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, sphereTestUtility.OWNER_SERVICE_NAME_1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, sphereTestUtility.OWNER_SERVICE_NAME_2, true, true);
		List<UhuServiceInfo> serviceInfo = new ArrayList<>();
		serviceInfo.add(serviceInfo1);
		serviceInfo.add(serviceInfo2);
		
		assertTrue(sphereRegistryWrapper.addLocalServicesToSphere(sphereId, serviceInfo));
		
	}
	
	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addLocalServicesToSphere(String, Iterable)}.
	 * 
	 * <br>When services are not added to registry,
	 *  it should return False
	 */
	@Test
	public final void servicesNotAddedToRegistryReturnsFalse() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_2;
		String sphereId = sphereName + upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereName,upaDevice.getDeviceId(), UhuSphereType.UHU_SPHERE_TYPE_DEFAULT);
		
		//Create service 1 but not added to registry
		String serviceName1 = sphereTestUtility.MEMBER_SERVICE_NAME_3;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		
		//Create service 2 but not added to registry
		String serviceName2 = sphereTestUtility.MEMBER_SERVICE_NAME_4;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		
		registry.spheres.put(sphereId, sphere);
		
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, sphereTestUtility.OWNER_SERVICE_NAME_1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, sphereTestUtility.OWNER_SERVICE_NAME_2, true, true);
		List<UhuServiceInfo> serviceInfo = new ArrayList<>();
		serviceInfo.add(serviceInfo1);
		serviceInfo.add(serviceInfo2);
		
		assertFalse(sphereRegistryWrapper.addLocalServicesToSphere(sphereId, serviceInfo));
	}

	
	/**
	 * Test method for {@link com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper#addLocalServicesToSphere(String, Iterable)}.
	 * 
	 * <br>When sphere does not exist in the registry,
	 *  it should return False
	 */
	@Test
	public final void sphereNotInRegistryReturnsFalse() {		
	
		// create owner sphere
		String sphereName = sphereTestUtility.OWNER_SPHERE_NAME_1;
		String sphereId = sphereName + upaDevice.getDeviceId();
		
		//Create service 1 but not added to registry
		String serviceName1 = sphereTestUtility.MEMBER_SERVICE_NAME_3;
		UhuServiceId serviceId1 = new UhuServiceId(serviceName1);
		HashSet<String> sphereSet1 = new HashSet<>();
		sphereSet1.add(sphereId);
		Service service1 = new OwnerService(serviceName1,
						upaDevice.getDeviceId(), sphereSet1);
		registry.sphereMembership.put(serviceId1.getUhuServiceId(), service1);
		
		//Create service 2 but not added to registry
		String serviceName2 = sphereTestUtility.MEMBER_SERVICE_NAME_4;
		UhuServiceId serviceId2 = new UhuServiceId(serviceName2);
		HashSet<String> sphereSet2 = new HashSet<>();
		sphereSet2.add(sphereId);
		Service service2 = new OwnerService(serviceName2,
						upaDevice.getDeviceId(), sphereSet2);
		registry.sphereMembership.put(serviceId2.getUhuServiceId(), service2);
		
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo(serviceId1.getUhuServiceId(), serviceName1, sphereTestUtility.OWNER_SERVICE_NAME_1, true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo(serviceId2.getUhuServiceId(), serviceName2, sphereTestUtility.OWNER_SERVICE_NAME_2, true, true);
		List<UhuServiceInfo> serviceInfo = new ArrayList<>();
		serviceInfo.add(serviceInfo1);
		serviceInfo.add(serviceInfo2);
		
		assertFalse(sphereRegistryWrapper.addLocalServicesToSphere(sphereId, serviceInfo));
	}
}
