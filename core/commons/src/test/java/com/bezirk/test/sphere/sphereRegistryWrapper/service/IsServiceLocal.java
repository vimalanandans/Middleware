/**
 * 
 */
package com.bezirk.test.sphere.sphereRegistryWrapper.service;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.sphere.impl.OwnerService;
import com.bezirk.sphere.impl.Service;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;

/**
 * @author karthik
 *
 */
public class IsServiceLocal {

	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(IsServiceLocal.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up SphereRegistryWrapper:IsServiceLocal TestCase *****");
		mockSetUp.setUPTestEnv();
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down SphereRegistryWrapper:IsServiceLocal TestCase *****");
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
	 * Test method for {@link SphereRegistryWrapper#isServiceLocal(String)}.
	 * 
	 * <br>When valid deviceId is passed to isServiceLocal,
	 *  it should return True
	 */
	@Test
	public final void validDeviceIdReturnsTrue() {
		String sphereId ="Home";
		HashSet<String> sphereSet = new HashSet<>();
		sphereSet.add(sphereId);
		String deviceId = mockSetUp.upaDevice.getDeviceId();
		Service service = new OwnerService("ServiceA", deviceId, sphereSet);
		assertTrue(sphereRegistryWrapper.isServiceLocal(service.getOwnerDeviceId()));		
	}
	
	
	/**
	 * Test method for {@link SphereRegistryWrapper#isServiceLocal(String)}.
	 * 
	 * <br>When invalid deviceId is passed to isServiceLocal,
	 *  it should return False
	 */
	@Test
	public final void invalidDeviceIdReturnsFalse() {
		// The correct device id is mockSetUp.upaDevice.getDeviceId(), but we are passing an invalid device id here.
		assertFalse(sphereRegistryWrapper.isServiceLocal("InvalidDeviceId"));		
	}
}
