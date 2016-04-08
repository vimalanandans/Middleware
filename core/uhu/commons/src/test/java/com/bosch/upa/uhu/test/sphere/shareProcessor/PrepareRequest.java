/**
 * 
 */
package com.bosch.upa.uhu.test.sphere.shareProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import  java.lang.reflect.Method;

import com.bosch.upa.uhu.commons.UhuId;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.sphere.impl.ShareProcessor;
import com.bosch.upa.uhu.sphere.messages.ShareRequest;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author karthik
 *
 */
public class PrepareRequest {

	private static ShareProcessor shareProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(PrepareRequest.class);
	private static Method method;
	
	@SuppressWarnings("rawtypes")
	private static Class[] parameterTypes;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up ShareProcessor:PrepareRequest TestCase *****");
		mockSetUp.setUPTestEnv();
		shareProcessor = mockSetUp.shareProcessor;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
		/** Reflection to test the private method **/
		parameterTypes = new Class[2];
		parameterTypes[0] = java.lang.String.class; 
		parameterTypes[1] = java.lang.String.class;
		method = ShareProcessor.class.getDeclaredMethod("prepareRequest", parameterTypes);
		method.setAccessible(true);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down ShareProcessor:PrepareRequest TestCase *****");
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
	 * Test the behavior of the prepareRequest method when a valid sharerSphereId, inviterShortCode is passed.
	 * <br>The method under test is expected to return True
	 * @throws Exception
	 */
	@Test
	public void validParametersReturnsTrue() throws Exception {
		String sharerSphereId = sphereTestUtility.generateOwnerCombo();
		String inviterShortCode = new UhuId().getShortIdByHash(sharerSphereId);
		/** invoke the method under test using reflection **/
        ShareRequest preparedRequest = (ShareRequest)method.invoke(shareProcessor, sharerSphereId, inviterShortCode);
		
		assertEquals(sharerSphereId, preparedRequest.getSharerSphereId());
		assertEquals(inviterShortCode, preparedRequest.getSphereId()); //inviterShortCode is initialized in ControlMessage class
		assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).device, preparedRequest.getSender().device);
		assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).serviceId, preparedRequest.getSender().serviceId);
	}
	
}