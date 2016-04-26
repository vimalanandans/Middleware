/**
 *
 */
package com.bezirk.sphere.catchProcessor;

import com.bezirk.sphere.impl.CatchProcessor;
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
public class ProcessCatchCode {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(ProcessCatchCode.class);
    private static CatchProcessor catchProcessor;
    private static SphereTestUtility sphereTestUtility;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up CatchProcessor:ProcessCatchCode TestCase *****");
        mockSetUp.setUPTestEnv();
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down CatchProcessor:ProcessCatchCode TestCase *****");
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
     * Catch code is valid<br>
     * sphere catching the services is valid<br>
     */
    @Test
    public final void validCatchCodeValidCatcherSphere() {
        String catchCode = "abcdefg";
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertTrue(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Catch code is invalid<br>
     * sphere catching the services is valid<br>
     */
    @Test
    public final void invalidCatchCodeValidCatcherSphere() {
        String catchCode = "abcde"; // Catch code has to have 7 characters.
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertFalse(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Catch code is valid<br>
     * sphere catching the services is invalid<br>
     */
    @Test
    public final void validCatchCodeInvalidCatcherSphere() {
        String catchCode = "abcdefg";
        String sphereId = "invalidSphereId";
        assertFalse(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Catch code is invalid<br>
     * sphere catching the services is invalid<br>
     */
    @Test
    public final void invalidCatchCodeInvalidCatcherSphere() {
        String catchCode = "abcdefg123"; // Catch code has to have 7 characters.
        String sphereId = "invalidSphereId";
        assertFalse(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Catch code is null<br>
     * sphere catching the services is invalid<br>
     */
    @Test(expected = NullPointerException.class)
    public final void nullCatchCodeValidCatcherSphere() {
        String catchCode = null;
        String sphereId = sphereTestUtility.generateOwnerCombo();
        assertFalse(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    /**
     * Test the case where<br>
     * Catch code is valid<br>
     * sphere catching the services is null<br>
     */
    @Test
    public final void validCatchCodeNullCatcherSphere() {
        String catchCode = "abcdefg";
        String sphereId = null;
        assertFalse(catchProcessor.processCatchCode(catchCode, sphereId));
    }

    // TODO: add cases for various combination of inputs and validate the return
    // values
    // TODO: expand/improve the SphereUtility for sphere and service generation
    // and also use mockito instead of actual classes especially registry
}
