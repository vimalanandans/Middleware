/**
 *
 */
package com.bezirk.sphere.shareProcessor;

import com.bezirk.commons.UhuId;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.messages.ShareResponse;
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
    private static final Logger log = LoggerFactory.getLogger(PrepareResponse.class);
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
        log.info("***** Setting up ShareProcessor:PrepareResponse TestCase *****");
        mockSetUp.setUPTestEnv();
        shareProcessor = mockSetUp.shareProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[5];
        parameterTypes[0] = java.lang.String.class;
        parameterTypes[1] = java.lang.String.class;
        parameterTypes[2] = UhuZirkEndPoint.class;
        parameterTypes[3] = java.lang.String.class;
        parameterTypes[4] = java.lang.String.class;
        method = ShareProcessor.class.getDeclaredMethod("prepareResponse", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down ShareProcessor:prepareResponse TestCase *****");
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
     * Test the behavior of the sendResponse method when a valid parameters is passed.
     * <br>The method under test is expected to return ShareResponseObj
     *
     * @throws Exception
     */
    @Test
    public void validParametersReturnsShareResponseObj() throws Exception {
        String sharerSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new UhuId().getShortIdByHash(sharerSphereId);
        String inviterSphereId = sphereTestUtility.generateOwnerCombo();
        UhuZirkEndPoint sharer = new UhuZirkEndPoint(sphereTestUtility.OWNER_SERVICE_ID_3);
        sharer.device = sphereTestUtility.DEVICE_2.getDeviceName();

        /** invoke the method under test using reflection **/
        ShareResponse preparedResponse = (ShareResponse) method.invoke(shareProcessor, inviterShortCode, inviterSphereId, sharer, "abcdefg", sharerSphereId);

        assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).device, preparedResponse.getSender().device);
        assertEquals(UhuNetworkUtilities.getServiceEndPoint(null).serviceId, preparedResponse.getSender().serviceId);//Sender service end point
        assertEquals(sharer, preparedResponse.getRecipient()); //Recipient service end point 
        assertEquals(inviterShortCode, preparedResponse.getSphereId());  //inviterShortCode is initialized in ControlMessage class
        assertEquals(sharerSphereId, preparedResponse.getSharerSphereId());
    }

    /**
     * Test the behavior of the sendResponse method when inviterSphereId is null.
     * <br>The method under test is expected to return null
     *
     * @throws Exception
     */
    @Test
    public void nullInviterSphereIdReturnsNull() throws Exception {
        String sharerSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new UhuId().getShortIdByHash(sharerSphereId);
        String inviterSphereId = null;
        UhuZirkEndPoint sharer = new UhuZirkEndPoint(sphereTestUtility.OWNER_SERVICE_ID_3);
        sharer.device = sphereTestUtility.DEVICE_2.getDeviceName();

        /** invoke the method under test using reflection **/
        ShareResponse preparedResponse = (ShareResponse) method.invoke(shareProcessor, inviterShortCode, inviterSphereId, sharer, "abcdefg", sharerSphereId);
        assertNull(preparedResponse);
    }

    /**
     * Test the behavior of the sendResponse method when sharer UhuZirkEndPoint  is null.
     * <br>The method under test is expected to throw exception
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullSharerThrowsException() throws Exception {
        String sharerSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new UhuId().getShortIdByHash(sharerSphereId);
        String inviterSphereId = sphereTestUtility.generateOwnerCombo();
        UhuZirkEndPoint sharer = null;

        /** invoke the method under test using reflection **/
        method.invoke(shareProcessor, inviterShortCode, inviterSphereId, sharer, "abcdefg", sharerSphereId);
    }

    /**
     * Test the behavior of the sendResponse method when sharerSphereId is null.
     * <br>The method under test is expected to throw exception
     *
     * @throws Exception
     */
    @Test(expected = InvocationTargetException.class)
    public void nullSharerSphereIdReturnsNull() throws Exception {
        String sharerSphereId = null;
        String inviterShortCode = new UhuId().getShortIdByHash(sphereTestUtility.generateOwnerCombo());
        String inviterSphereId = sphereTestUtility.generateOwnerCombo();
        UhuZirkEndPoint sharer = new UhuZirkEndPoint(sphereTestUtility.OWNER_SERVICE_ID_3);
        sharer.device = sphereTestUtility.DEVICE_2.getDeviceName();

        /** invoke the method under test using reflection **/
        method.invoke(shareProcessor, inviterShortCode, inviterSphereId, sharer, "abcdefgh", sharerSphereId);
    }
}