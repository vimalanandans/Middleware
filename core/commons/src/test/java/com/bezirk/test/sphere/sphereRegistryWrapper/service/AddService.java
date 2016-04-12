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
import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class AddService {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static SphereRegistry registry;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(AddService.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:AddService TestCase *****");
		mockSetUp.setUPTestEnv();
		registry = mockSetUp.registry;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:AddService TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#addService(String, Service)}.
	 * 
	 * <br>When valid ServiceId and Service object is passed to addService, the service is added to the registry and the method
	 *  should return True
	 */
	@Test
	public final void validServiceIdAndServiceReturnsTrue() {
		
		//Create a service
		String serviceId = UUID.randomUUID().toString();
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString();
		sphereSet.add(sphereId);
		Service service = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
		assertTrue(sphereRegistryWrapper.addService(serviceId, service));	
		
		//verify persisted service with the service created for testing
		assertTrue(registry.sphereMembership.get(serviceId).equals(service));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addService(String, Service)}.
	 * 
	 * <br>Test behavior of addService when serviceId is passed as null.
	 * addService is expected to return false.
	 */
	@Test
	public final void nullServiceIdShouldReturnFalse() {		
		String serviceId = null;
		HashSet<String> sphereSet = new HashSet<String>();
		String sphereId = UUID.randomUUID().toString();
		sphereSet.add(sphereId);
		Service service = new OwnerService("serviceName", "ownerDeviceId", sphereSet);
		assertFalse(sphereRegistryWrapper.addService(serviceId, service));	
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addService(String, Service)}.
	 * 
	 * <br>Test behavior of addService when Service object is passed as null.
	 * addService is expected to return false.
	 */
	@Test
	public final void nullServiceObjectShouldReturnFalse() {		
		String serviceId = UUID.randomUUID().toString();
		Service service = null;
		assertFalse(sphereRegistryWrapper.addService(serviceId, service));
	}
	
	/**
	 * Test method for {@link SphereRegistryWrapper#addService(String, Service)}.
	 * 
	 * <br>Test behavior of addService when Service object and serviceId are passed as null.
	 * addService is expected to return false.
	 */
	@Test
	public final void nullServiceObjectAndNullServiceIdShouldReturnFalse() {		
		String serviceId = null;
		Service service = null;
		assertFalse(sphereRegistryWrapper.addService(serviceId, service));
	}


}
