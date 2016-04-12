/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.UUID;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;

/**
 * @author karthik
 *
 */
public class IsServiceInSphere {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(IsServiceInSphere.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:IsServiceInSphere TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:IsServiceInSphere TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#isServiceInSphere(UhuServiceId, String)}.
	 * 
	 * <br>Test the behavior of isServiceInSphere when valid service and sphereId is passed.
	 *  isServiceInSphere should return True if the sphere id is in the sphere set of the service.
	 */
	@Test
	public final void validServiceAndSphereIdShouldReturnTrue() {
		
		//Create service and sphere set
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId service = new UhuServiceId(serviceId);
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString();
		sphereSet.add(sphereId);
		OwnerService ownerService = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
		
		registry.spheres.put(sphereId, new OwnerSphere());
		registry.sphereMembership.put(serviceId, ownerService);
		sphereRegistryWrapper.addService(serviceId, ownerService);
		
		assertTrue(sphereRegistryWrapper.isServiceInSphere(service, sphereId));	 
	}

	/**
	 * Test method for {@link SphereRegistryWrapper#isServiceInSphere(UhuServiceId, String)}.
	 * 
	 * <br>Test the behavior of isServiceInSphere when valid service and sphereId is passed.
	 * But the sphereId is not part of the sphere set.
	 * isServiceInSphere should return False
	 */
	@Test
	public final void wrongSphereIdShouldReturnFalse() {
		
		// create a service and service set
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId service = new UhuServiceId(serviceId);
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString(); 
		
		sphereSet.add("abcd"); //The above created sphereId is not added to the sphere set.
		OwnerService ownerService = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
		
		registry.spheres.put(sphereId, new OwnerSphere());
		registry.sphereMembership.put(serviceId, ownerService);
		sphereRegistryWrapper.addService(serviceId, ownerService);
		
		// It should return false because the sphere id in the sphere set is "abcd", not the sphereId which is being passed here
		assertFalse(sphereRegistryWrapper.isServiceInSphere(service, sphereId));	 
	}

	/**
	 * Test method for {@link SphereRegistryWrapper#isServiceInSphere(UhuServiceId, String)}.
	 * 
	 * <br>Test the behavior of isServiceInSphere when wrong service id is passed.
	 * isServiceInSphere should return False
	 */
	@Test
	public final void wrongServiceIdShouldReturnFalse() {
		
		// create a service and service set but not added to registry
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId service = new UhuServiceId(serviceId);
		String sphereId = UUID.randomUUID().toString(); 
		
		// It should return false because the service id is not added to registry
		assertFalse(sphereRegistryWrapper.isServiceInSphere(service, sphereId));	 
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#isServiceInSphere(UhuServiceId, String)}.
	 * 
	 * <br>Test the behavior of isServiceInSphere when service id is passed as null.
	 * isServiceInSphere should throw an exception
	 */
	@Test(expected=NullPointerException.class)
	public final void nullServiceIdThrowsException() {
		
		// create a service and service set but not added to registry
		String sphereId = UUID.randomUUID().toString(); 
		
		// The method will throw an exception if service id is null
	    sphereRegistryWrapper.isServiceInSphere(null, sphereId);	 
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#isServiceInSphere(UhuServiceId, String)}.
	 * 
	 * <br>Test the behavior of isServiceInSphere when null sphereId is passed.
	 *  isServiceInSphere should return False.
	 */
	@Test
	public final void nullSphereIdReturnsFalse() {
		
		//Create service and sphere set
		String serviceId = UUID.randomUUID().toString();
		UhuServiceId service = new UhuServiceId(serviceId);
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString();
		sphereSet.add(sphereId);
		OwnerService ownerService = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
		
		registry.spheres.put(sphereId, new OwnerSphere());
		registry.sphereMembership.put(serviceId, ownerService);
		sphereRegistryWrapper.addService(serviceId, ownerService);
		
		// Pass sphere id as null string
		assertFalse(sphereRegistryWrapper.isServiceInSphere(service, null));	 
	}

}

