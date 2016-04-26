/**
 *
 */
package com.bezirk.sphere.catchProcessor;

import com.bezirk.commons.UhuId;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.SphereExchangeData;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;
import com.bezrik.network.UhuNetworkUtilities;

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
public class PrepareResponse {

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
        log.info("***** Setting up CatchProcessor:prepareResponse TestCase *****");
        mockSetUp.setUPTestEnv();
        catchProcessor = mockSetUp.catchProcessor;
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[4];
        parameterTypes[0] = SphereExchangeData.class;
        parameterTypes[1] = BezirkDeviceInfo.class;
        parameterTypes[2] = java.lang.String.class;
        parameterTypes[3] = java.lang.String.class;
        method = CatchProcessor.class.getDeclaredMethod("prepareResponse", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down CatchProcessor:prepareResponse TestCase *****");
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
     * Test the behavior of the prepareResponse method when a valid parameters are passed.
     * <br>The method under test is expected to return True
     *
     * @throws Exception
     */
    @Test
    public void validParametersCatchResponseObjTrue() throws Exception {
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new UhuId().getShortIdByHash(catcherSphereId);
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        BezirkDeviceInfo catcherBezirkDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String catcherDeviceId = catcherBezirkDeviceInfo.getDeviceId();

        /** invoke the method under test using reflection **/
        CatchResponse preparedResponse = (CatchResponse) method.invoke(catchProcessor, sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode, catcherSphereId);

        assertEquals(catcherSphereId, preparedResponse.getCatcherSphereId());
        assertEquals(catcherDeviceId, preparedResponse.getCatcherDeviceId());
        assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).device, preparedResponse.getSender().device); // To verify the BezirkZirkEndPoint obj
        assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).zirkId, preparedResponse.getSender().zirkId);// To verify the BezirkZirkEndPoint obj
    }

    /**
     * Test the behavior of the prepareResponse method when SphereExchangeData obj is null.
     * <br> Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullSphereExchangeDataObjThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = null;
        BezirkDeviceInfo catcherBezirkDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new UhuId().getShortIdByHash(catcherSphereId);
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode, catcherSphereId);
    }

    /**
     * Test the behavior of the prepareResponse method when BezirkDeviceInfo obj is null.
     * <br> Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullUhuDeviceInfoObjThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        BezirkDeviceInfo catcherBezirkDeviceInfo = null;
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = sphereRegistryWrapper.getShareCode(catcherSphereId);
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode, catcherSphereId);
    }

    /**
     * Test the behavior of the prepareResponse method when inviterShortCode is null.
     * <br> Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullInviterShortCodeThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        BezirkDeviceInfo catcherBezirkDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = null;
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode, catcherSphereId);
    }

    /**
     * Test the behavior of the prepareResponse method when catcherSphereId is null.
     * <br> Here, InvocationTargetException will be thrown as we are using reflection.<br>
     * InvocationTargetException wraps the actual exception,i.e,NullPointerException.
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullCatcherSphereIdThrowsException() throws Exception {
        SphereExchangeData sphereExchangeData = sphereTestUtility.getSphereExchangeDataObj();
        BezirkDeviceInfo catcherBezirkDeviceInfo = sphereTestUtility.getUhuDeviceInfo();
        String catcherSphereId = null;
        String inviterShortCode = sphereRegistryWrapper.getShareCode(catcherSphereId); //this will also be null. Hence exception will be thrown at the same place as the previous test.
        /** invoke the method under test using reflection **/
        method.invoke(catchProcessor, sphereExchangeData, catcherBezirkDeviceInfo, inviterShortCode, catcherSphereId);
    }

}