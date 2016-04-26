package com.bezirk.sphere.shareProcessor;

import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.messages.ShareResponse;
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
public class ProcessResponse {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(ProcessResponse.class);
    private static ShareProcessor shareProcessor;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up ShareProcessor:ProcessResponse TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down ShareProcessor:ProcessResponse TestCase *****");
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
     * Share response is valid<br>
     */
    @Test
    public final void validShareResponse() {
        /**create the ShareResponse**/
        ShareResponse shareResponse = sphereTestUtility.getShareResponseObj();
        assertTrue(shareProcessor.processResponse(shareResponse));
    }

    /**
     * Test the case where<br>
     * Share response is invalid(null)<br>
     */
    @Test
    public final void nullShareResponseObjReturnsFalse() {
        ShareResponse shareResponse = null;
        assertFalse(shareProcessor.processResponse(shareResponse));
    }
    // TODO: add more cases
    // TODO: expand/improve the SphereUtility for sphere and service generation
    // and also use mockito instead of actual classes especially registry
}
