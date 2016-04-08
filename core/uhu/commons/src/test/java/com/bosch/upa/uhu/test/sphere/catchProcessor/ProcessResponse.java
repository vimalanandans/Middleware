package com.bosch.upa.uhu.test.sphere.catchProcessor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.sphere.impl.CatchProcessor;
import com.bosch.upa.uhu.sphere.messages.CatchResponse;
import com.bosch.upa.uhu.test.sphere.testUtilities.MockSetUpUtility;
import com.bosch.upa.uhu.test.sphere.testUtilities.SphereTestUtility;

/**
 * @author rishabh
 *
 */
public class ProcessResponse {

	private static CatchProcessor catchProcessor;
	private static SphereTestUtility sphereTestUtility;
	private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
	private static final Logger log = LoggerFactory.getLogger(ProcessResponse.class);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("***** Setting up CatchProcessor:ProcessResponse TestCase *****");
		mockSetUp.setUPTestEnv();
		catchProcessor = mockSetUp.catchProcessor;
		sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("***** Shutting down CatchProcessor:ProcessResponse TestCase *****");
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
	 * Catch response is valid<br>
	 */
	@Test
	public final void validCatchResponse() {
		assertTrue(catchProcessor.processResponse(sphereTestUtility.getCatchResponseObj()));
	}
	
	/**
	 * Test the case where<br>
	 * Catch response is null<br>
	 */
	@Test
	public final void invalidCatchResponse() {
		CatchResponse catchResponse = null;
		assertFalse(catchProcessor.processResponse(catchResponse));
	}

	// TODO: add more cases
	// TODO: expand/improve the SphereUtility for sphere and service generation
	// and also use mockito instead of actual classes especially registry
}
