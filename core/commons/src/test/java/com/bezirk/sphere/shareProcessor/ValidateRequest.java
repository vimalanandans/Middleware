/**
 *
 */
package com.bezirk.sphere.shareProcessor;

import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.messages.ShareRequest;
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
public class ValidateRequest {
    private static final Logger logger = LoggerFactory.getLogger(ValidateRequest.class);

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
        logger.info("***** Setting up ShareProcessor:ValidateRequest TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = ShareRequest.class;
        method = ShareProcessor.class.getDeclaredMethod("validateRequest", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down ShareProcessor:ValidateRequest TestCase *****");
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
     * Test the behavior of the validateRequest method when a valid ShareRequest obj is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validShareReqObjReturnsTrue() throws Exception {
        ShareRequest shareRequest = sphereTestUtility.getShareRequestObj();
        /** invoke the method under test using reflection **/
        boolean isRequestValid = (Boolean) method.invoke(shareProcessor, shareRequest);
        assertTrue(isRequestValid);
    }

    /**
     * Test the behavior of the validateRequest method when a null ShareRequest obj is passed.
     * <br>The method under test is expected to return False
     *
     * @throws Exception
     */
    @Test
    public void nullShareReqObjReturnsTrue() throws Exception {
        ShareRequest shareRequest = null;
        /** invoke the method under test using reflection **/
        boolean isRequestValid = (Boolean) method.invoke(shareProcessor, shareRequest);
        assertFalse(isRequestValid);
    }
}