/**
 *
 */
package com.bezirk.sphere.catchProcessor;

import com.bezirk.commons.BezirkId;
import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;
import com.bezrik.network.BezirkNetworkUtilities;

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
public class PrepareRequest {

    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static final Logger logger = LoggerFactory.getLogger(ProcessCatchCode.class);
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
        logger.info("***** Setting up CatchProcessor:AddCatchCode TestCase *****");
        mockSetUp.setUPTestEnv();
        sphereRegistryWrapper = mockSetUp.sphereRegistryWrapper;
        catchProcessor = mockSetUp.catchProcessor;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        /** Reflection to test the private method **/
        parameterTypes = new Class[2];
        parameterTypes[0] = java.lang.String.class;
        parameterTypes[1] = java.lang.String.class;
        method = CatchProcessor.class.getDeclaredMethod("prepareRequest", parameterTypes);
        method.setAccessible(true);
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("***** Shutting down CatchProcessor:prepareRequest TestCase *****");
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
     * Test the behavior of the prepare method when a valid SphereId and inviterShortCode is passed.
     * <br>The method under test is expected to return a CatchRequest object.
     * <br> But, as we can't compare the objects, we are testing the values of the object parameters.
     *
     * @throws Exception
     */
    @Test
    public void validSphereIdAndInviterShortCodeReturnsCatchRequestObj() throws Exception {
        String catcherSphereId = sphereTestUtility.generateOwnerCombo();
        String inviterShortCode = new BezirkId().getShortIdByHash(catcherSphereId);
        String sphereExchangeData = sphereRegistryWrapper.getShareCodeString(catcherSphereId);

        /** invoke the method under test using reflection **/
        CatchRequest preparedRequest = (CatchRequest) method.invoke(catchProcessor, catcherSphereId, inviterShortCode);

        assertEquals(catcherSphereId, preparedRequest.getCatcherSphereId());
        assertEquals(sphereExchangeData, preparedRequest.getSphereExchangeData());
        assertEquals(BezirkNetworkUtilities.getServiceEndPoint(null).device, preparedRequest.getSender().device); // To verify the BezirkZirkEndPoint obj
        assertEquals(BezirkNetworkUtilities.getServiceEndPoint(null).zirkId, preparedRequest.getSender().zirkId);// To verify the BezirkZirkEndPoint obj
    }

    /**
     * IMPORTANT: Not testing scenarios where catcherSpherId and inviterShortCode are null. These parameters
     * are not supposed to be null and this is checked in the method {@link #CatchRequest.processCatchCode(String, String)}
     */


}