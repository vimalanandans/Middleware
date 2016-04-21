/**
 *
 */
package com.bezirk.test.sphere.shareProcessor;

import com.bezirk.middleware.objects.UhuDeviceInfo;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.impl.SphereExchangeData;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.test.sphere.testUtilities.SphereTestUtility;

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
public class StoreDataForResponse {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger log = LoggerFactory.getLogger(StoreDataForResponse.class);
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
        log.info("***** Setting up ShareProcessor:StoreData TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[3];
        parameterTypes[0] = SphereExchangeData.class;
        parameterTypes[1] = UhuDeviceInfo.class;
        parameterTypes[2] = java.lang.String.class;
        method = ShareProcessor.class.getDeclaredMethod("storeData", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down ShareProcessor:StoreData TestCase *****");
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
     * Test the behavior of the storeData method when a valid sphereExchangeData, inviterUhuDeviceInfo, sharerSphereId is passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validParametersReturnsTrue() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        UhuDeviceInfo inviterUhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String sharerSphereId = sphereTestUtility.generateOwnerCombo();
        /** invoke the method under test using reflection **/
        boolean isDataStored = (Boolean) method.invoke(shareProcessor, sphereExchangeData, inviterUhuDeviceInfo, sharerSphereId);
        assertTrue(isDataStored);
    }

    /**
     * Test the behavior of the storeData method when a null SphereExchangeData is passed.
     * <br>The method under test is expected to throw a NullPointerException. <br> The exception thrown by this test
     * will be InvocationTargetException because this wraps(due to reflection) NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullSphereExchangeDataThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = null;
        UhuDeviceInfo inviterUhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String sharerSphereId = sphereTestUtility.generateOwnerCombo();
        /** invoke the method under test using reflection **/
        method.invoke(shareProcessor, sphereExchangeData, inviterUhuDeviceInfo, sharerSphereId);
    }


    /**
     * Test the behavior of the storeData method when a null sharerSphereId is passed.
     * <br>The method under test is expected to throw a NullPointerException. <br> The exception thrown by this test
     * will be InvocationTargetException because this wraps(due to reflection) NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullSharerSphereIdThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        UhuDeviceInfo inviterUhuDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String sharerSphereId = null;
        /** invoke the method under test using reflection **/
        method.invoke(shareProcessor, sphereExchangeData, inviterUhuDeviceInfo, sharerSphereId);
    }

}