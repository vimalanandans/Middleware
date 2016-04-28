/**
 *
 */
package com.bezirk.sphere.catchProcessor;

import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.SphereExchangeData;
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
public class StoreData {
    private static final Logger logger = LoggerFactory.getLogger(ProcessCatchCode.class);

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
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
        logger.info("***** Setting up CatchProcessor:StoreData TestCase *****");
        mockSetUp.setUPTestEnv();
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[1];
        parameterTypes[0] = SphereExchangeData.class;
        method = CatchProcessor.class.getDeclaredMethod("storeData", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down CatchProcessor:StoreData TestCase *****");
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
     * Test the behavior of the storeData method when a valid SphereExchangeData object is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validSphereExchangeDataObjReturnsTrue() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        /** invoke the method under test using reflection **/
        boolean isDataStored = (Boolean) method.invoke(catchProcessor, sphereExchangeData);
        assertTrue(isDataStored);
    }

    /**
     * Test the behavior of the storeData method when a null SphereExchangeData object is passed.
     * <br>Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullCatchRequestObjThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = null;
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, sphereExchangeData);
    }

}