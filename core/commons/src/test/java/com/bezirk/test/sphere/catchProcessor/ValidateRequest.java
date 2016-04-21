/**
 *
 */
package com.bezirk.test.sphere.catchProcessor;

import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

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

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(ProcessCatchCode.class);
    private static CatchProcessor catchProcessor;
    private static SphereTestUtility sphereTestUtility;
    private static Method method;

    @SuppressWarnings("rawtypes")
    private static Class[] parameterTypes;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up CatchProcessor:validateRequest TestCase *****");
        mockSetUp.setUPTestEnv();
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = CatchRequest.class;
        method = CatchProcessor.class.getDeclaredMethod("validateRequest", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down CatchProcessor:validateRequest TestCase *****");
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
     * Test the behavior of the validateRequest method when a valid CatchRequest obj is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validCatchRequestObjReturnsTrue() throws Exception {
        /** invoke the method under test using reflection **/
        boolean isRequestValid = (Boolean) method.invoke(catchProcessor, sphereTestUtility.getCatchRequestObj());
        assertTrue(isRequestValid);
    }

    /**
     * Test the behavior of the validateRequest method when a null is passed.
     * <br>The method under test should return False.
     *
     * @throws Exception
     */
    @Test
    public void nullCatchRequestObjReturnsFalse() throws Exception {
        CatchRequest catchRequest = null;
        /** invoke the method under test using reflection **/
        boolean isRequestValid = (Boolean) method.invoke(catchProcessor, catchRequest);
        assertFalse(isRequestValid);
    }

}