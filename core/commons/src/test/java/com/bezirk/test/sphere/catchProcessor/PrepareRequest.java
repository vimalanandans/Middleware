/**
 * 
 */
package com.bezirk.test.sphere.catchProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import  java.lang.reflect.Method;

import com.bezirk.commons.UhuId;
import com.bezrik.network.UhuNetworkUtilities;
import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class PrepareRequest {

	private static CatchProcessor catchProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static SphereRegistryWrapper sphereRegistryWrapper;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ProcessCatchCode.class);
	private static Method method;
	
	@SuppressWarnings("rawtypes")
	private static Class[] parameterTypes;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up CatchProcessor:AddCatchCode TestCase *****");
		mockSetUp.setUPTestEnv();
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		catchProcessor = mockSetUp.catchProcessor;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
		/** Reflection to test the private method **/
		parameterTypes = new Class[2];
		parameterTypes[0] = java.lang.String.class;
		parameterTypes[1] = java.lang.String.class;
		method = CatchProcessor.class.getDeclaredMethod("prepareRequest", parameterTypes);
		method.setAccessible(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down CatchProcessor:prepareRequest TestCase *****");
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
	 * Test the behavior of the prepare method when a valid SphereId and inviterShortCode is passed.
	 * <br>The method under test is expected to return a CatchRequest object. 
	 * <br> But, as we can't compare the objects, we are testing the values of the object parameters.
	 * @throws Exception
	 */
	@Test
	public void validSphereIdAndInviterShortCodeReturnsCatchRequestObj() throws Exception {
		String catcherSphereId = sphereTestUtility.generateOwnerCombo();
		String inviterShortCode = new UhuId().getShortIdByHash(catcherSphereId);
		String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(catcherSphereId);
		
		/** invoke the method under test using reflection **/
		CatchRequest preparedRequest = (CatchRequest)method.invoke(catchProcessor, catcherSphereId, inviterShortCode);
		
		assertEquals(catcherSphereId, preparedRequest.getCatcherSphereId());
		assertEquals(sphereExchangeData, preparedRequest.getSphereExchangeData());
		assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).device, preparedRequest.getSender().device); // To verify the UhuServiceEndPoint obj
		assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).serviceId, preparedRequest.getSender().serviceId);// To verify the UhuServiceEndPoint obj 
	}
	
	/**
	 * IMPORTANT: Not testing scenarios where catcherSpherId and inviterShortCode are null. These parameters
	 * are not supposed to be null and this is checked in the method {@link #CatchRequest.processCatchCode(String, String)}
	 */
	
	
}