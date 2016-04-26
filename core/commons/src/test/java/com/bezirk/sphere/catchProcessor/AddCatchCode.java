/**
 *
 */
package com.bezirk.sphere.catchProcessor;

import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

/**
 * @author karthik
 */
public class AddCatchCode {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(ProcessCatchCode.class);
    private static CatchProcessor catchProcessor;
    private static SphereTestUtility sphereTestUtility;
    private static SphereRegistryWrapper sphereRegistryWrapper;
    private static Method method;

    @SuppressWarnings("rawtypes")
    private static Class[] parameterTypes;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up CatchProcessor:AddCatchCode TestCase *****");
        mockSetUp.setUPTestEnv();
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = java.lang.String.class;
        method = CatchProcessor.class.getDeclaredMethod("addCatchCode", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down CatchProcessor:AddCatchCode TestCase *****");
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
     * Test the behavior of the addCatchCode method when a valid inviterShortCode is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validInviterShortCodeReturnsTrue() throws Exception {
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = sphereRegistryWrapper.getShareCode(catcherSphereId);
        /** invoke the method under test using reflection **/
        boolean isDataStored = (Boolean) method.invoke(catchProcessor, inviterShortCode);
        assertTrue(isDataStored);
    }

    /**
     * Test the behavior of the addCatchCode method when a null String is passed.
     * Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullInviterShortCodeThrowsException() throws Exception {
        String inviterShortCode = null;
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, inviterShortCode);
    }

}