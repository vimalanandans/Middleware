package com.bezirk.test.sphere.catchProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh
 *
 */
public class ProcessRequest {

	private static CatchProcessor catchProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ProcessRequest.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up CatchProcessor:ProcessRequest TestCase *****");
		mockSetUp.setUPTestEnv();
		catchProcessor = mockSetUp.catchProcessor;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down CatchProcessor:ProcessRequest TestCase *****");
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
	 * Catch request is valid<br>
	 */
	@Test
	public final void validCatchRequest() {
		assertTrue(catchProcessor.processRequest(sphereTestUtility.getCatchRequestObj()));
	}
	
	/**
	 * Test the case where<br>
	 * Catch request is null (invalid)<br>
	 */
	@Test
	public final void invalidCatchRequest() {
		CatchRequest catchRequest = null;
		assertFalse(catchProcessor.processRequest(catchRequest));
	}

	// TODO: add more cases
	// TODO: expand/improve the SphereUtility for sphere and service generation
	// and also use mockito instead of actual classes especially registry
}
