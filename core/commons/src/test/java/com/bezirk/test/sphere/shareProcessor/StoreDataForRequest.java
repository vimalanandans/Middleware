/**
 * 
 */
package com.bezirk.test.sphere.shareProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import  java.lang.reflect.Method;

import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.api.objects.UhuDeviceInfo;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class StoreDataForRequest {

	private static ShareProcessor shareProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(StoreDataForRequest.class);
	private static Method method;
	
	@SuppressWarnings("rawtypes")
	private static Class[] parameterTypes;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up ShareProcessor:StoreData TestCase *****");
		mockSetUp.setUPTestEnv();
		shareProcessor = mockSetUp.shareProcessor;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
		/** Reflection to test the private method **/
		parameterTypes = new Class[2];
		parameterTypes[0] = java.lang.String.class; 
		parameterTypes[1] = UhuDeviceInfo.class;
		method = ShareProcessor.class.getDeclaredMethod("storeData", parameterTypes);
		method.setAccessible(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down ShareProcessor:StoreData TestCase *****");
		mockSetUp.destroyTestSetUp();
		sphereTestUtility = null;
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
	 * Test the behavior of the storeData method when a valid InviterSphereId and sharerUhuDeviceInfo is passed.
	 * <br>The method under test is expected to return True
	 * @throws Exception
	 */
	@Test
	public void validInviterSphereIdAndDeviceInfoReturnsTrue() throws Exception {
		UhuDeviceInfo sharerUhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
		String inviterSphereId = sphereTestUtility.generateOwnerCombo();
		/** invoke the method under test using reflection **/
		boolean isDataStored = (Boolean)method.invoke(shareProcessor, inviterSphereId, sharerUhuDeviceInfo);
		assertTrue(isDataStored);
	}
	
	/**
	 * Test the behavior of the storeData method when a invalid InviterSphereId is passed.
	 * <br>The method under test is expected to return False
	 * @throws Exception
	 */
	@Test
	public void invalidInviterSphereIdValidDeviceInfoReturnsFalse() throws Exception {
		UhuDeviceInfo sharerUhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
		String inviterSphereId = "abcdefg";
		/** invoke the method under test using reflection **/
		boolean isDataStored = (Boolean)method.invoke(shareProcessor, inviterSphereId, sharerUhuDeviceInfo);
		assertFalse(isDataStored);
	}
	
}