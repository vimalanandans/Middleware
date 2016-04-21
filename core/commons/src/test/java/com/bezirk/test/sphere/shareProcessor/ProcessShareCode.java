/**
 *
 */
package com.bezirk.test.sphere.shareProcessor;

import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

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
public class ProcessShareCode {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(ProcessShareCode.class);
    private static ShareProcessor shareProcessor;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up ShareProcessor:ProcessShareCode TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down ShareProcessor:ProcessShareCode TestCase *****");
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
     * Share code is valid<br>
     * Sphere sharing the services is valid<br>
     */
    @Test
    public final void validShareCodeValidSharerSphere() {
        String shareCode = "abcdefg";
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertTrue(shareProcessor.processShareCode(shareCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Share code is invalid<br>
     * Sphere sharing the services is valid<br>
     */
    @Test
    public final void invalidShareCodeValidSharerSphere() {
        String shareCode = "abcdefg123";
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertFalse(shareProcessor.processShareCode(shareCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Share code is valid<br>
     * Sphere sharing the services is invalid<br>
     */
    @Test
    public final void validShareCodeInvalidSharerSphere() {
        String shareCode = "abcdefg";
        String sphereId = "invalidSphereId";
        assertFalse(shareProcessor.processShareCode(shareCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Share code is invalid<br>
     * Sphere sharing the services is invalid<br>
     */
    @Test
    public final void invalidShareCodeInvalidSharerSphere() {
        String shareCode = "abcdefg123";
        String sphereId = "invalidSphereId";
        assertFalse(shareProcessor.processShareCode(shareCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Share code is null<br>
     * Sphere sharing the services is valid<br>
     */
    @Test(expected = NullPointerException.class)
    public final void nullShareCodeValidSharerSphere() {
        String shareCode = null;
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertFalse(shareProcessor.processShareCode(shareCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Share code is valid<br>
     * Sphere sharing the services is null<br>
     */
    @Test
    public final void validShareCodeNullSharerSphere() {
        String shareCode = "abcdefg";
        String sphereId = null;
        assertFalse(shareProcessor.processShareCode(shareCode, sphereId));
    }
    // TODO: add cases for various combination of inputs and validate the return
    // values
    // TODO: expand/improve the SphereUtility for sphere and service generation
    // and also use mockito instead of actual classes especially registry
}
