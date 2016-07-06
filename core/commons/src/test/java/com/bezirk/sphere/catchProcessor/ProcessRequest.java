package com.bezirk.sphere.catchProcessor;

import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * @author rishabh
 */
public class ProcessRequest {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger logger = LoggerFactory.getLogger(ProcessRequest.class);
    private static CatchProcessor catchProcessor;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up CatchProcessor:ProcessRequest TestCase *****");
        mockSetUp.setUPTestEnv();
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down CatchProcessor:ProcessRequest TestCase *****");
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
    // TODO: expand/improve the SphereUtility for sphere and zirk generation
    // and also use mockito instead of actual classes especially registry
}