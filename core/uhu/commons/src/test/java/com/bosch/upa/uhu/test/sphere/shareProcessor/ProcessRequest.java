package com.bosch.upa.uhu.test.sphere.shareProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.sphere.impl.ShareProcessor;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.sphere.messages.ShareRequest;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh
 *
 */
public class ProcessRequest {

	private static ShareProcessor shareProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static SphereRegistryWrapper sphereRegistryWrapper; //for setting up shortCode-sphereId mapping in registry
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ProcessRequest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up ShareProcessor:ProcessRequest TestCase *****");
		mockSetUp.setUPTestEnv();
		shareProcessor = mockSetUp.shareProcessor;
		sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down ShareProcessor:ProcessRequest TestCase *****");
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
	 * Test the case where<br>
	 * Share request is valid<br>
	 */
	@Test
	public final void validShareRequest() {
		//add the sphere which has shared the code/qr, so that the request can be validated and processed.
		String sphereId = sphereTestUtility.generateOwnerCombo();
		String shortCode = sphereRegistryWrapper.getShareCode(sphereId);
		
		UhuServiceEndPoint sender = new UhuServiceEndPoint(sphereTestUtility.OWNER_SERVICE_ID_3);
		sender.device = sphereTestUtility.DEVICE_2.getDeviceName();
		ShareRequest shareRequest = new ShareRequest(shortCode, sphereTestUtility.getUhuDeviceInfo(), sender, "");
		assertTrue(shareProcessor.processRequest(shareRequest));
	}

	/**
	 * Test the case where<br>
	 * Share request is invalid(null)<br>
	 */
	@Test
	public final void invalidShareRequest() {
		ShareRequest shareRequest = null;
		assertFalse(shareProcessor.processRequest(shareRequest));
	}
	// TODO: add more cases
	// TODO: expand/improve the SphereUtility for sphere and service generation
	// and also use mockito instead of actual classes especially registry
}
