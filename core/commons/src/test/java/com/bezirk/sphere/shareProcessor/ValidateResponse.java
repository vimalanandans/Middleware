/**
 *
 */
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

import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class ValidateResponse {
    private static final Logger logger = LoggerFactory.getLogger(ValidateResponse.class);

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
        logger.info("***** Setting up ShareProcessor:ValidateResponse TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = ShareResponse.class;
        method = ShareProcessor.class.getDeclaredMethod("validateResponse", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down ShareProcessor:ValidateResponse TestCase *****");
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
     * Test the behavior of the validateResponse method when a valid ShareResponse obj is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validShareResponseObjReturnsTrue() throws Exception {
        ShareResponse shareResponse = sphereTestUtility.getShareResponseObj();
        /** invoke the method under test using reflection **/
        boolean isResponseValid = (Boolean) method.invoke(shareProcessor, shareResponse);
        assertTrue(isResponseValid);
    }

    /**
     * Test the behavior of the validateResponse method when a null ShareResponse obj is passed.
     * <br>The method under test is expected to return False
     *
     * @throws Exception
     */
    @Test
    public void nullShareResponseObjReturnsFalse() throws Exception {
        ShareResponse shareResponse = null;
        /** invoke the method under test using reflection **/
        boolean isResponseValid = (Boolean) method.invoke(shareProcessor, shareResponse);
        assertFalse(isResponseValid);
    }

    /**
     * IMPORTANT: Testing the behavior of validateResponse method when SphereExchangeData, BezirkDeviceInfo or sharerSphereId
     * objects are null is not required as they cannot be null based on the agreement with the constructor in {@link #ShareResponse}.
     * If they are null, then the constructor of ShareResponse throws an exception.
     */
}