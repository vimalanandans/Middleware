/**
 *
 */
package com.bezirk.sphere.shareProcessor;

import com.bezirk.commons.BezirkId;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class AddCatchCode {
    private static final Logger logger = LoggerFactory.getLogger(AddCatchCode.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static ShareProcessor shareProcessor;
    private static SphereTestUtility sphereTestUtility;
    private static Method method;

    @SuppressWarnings("rawtypes")
    private static Class[] parameterTypes;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("***** Setting up ShareProcessor:AddCatchCode TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = java.lang.String.class;
        method = ShareProcessor.class.getDeclaredMethod("addCatchCode", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down ShareProcessor:AddCatchCode TestCase *****");
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
     * Test the behavior of the addCatchCode method when a valid String inviterShortCode is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validInviterShortCodeReturnsTrue() throws Exception {
        String sphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new BezirkId().getShortIdByHash(sphereId);
        /** invoke the method under test using reflection **/
        boolean isShortCodeAdded = (Boolean) method.invoke(shareProcessor, inviterShortCode);
        assertTrue(isShortCodeAdded);
    }
}