/**
 * Number of devices: 2 (1 sender device0 and 1 receiver device1)
 * Scenario:
 * 1. Share request is sent from device0 with the share code got from device1.
 * 2. device1 processes the request and if successful, sends Share response to device0.
 * 3. device0 processes the response received from device1.
 */
package com.bezirk.test.sphere.testSphereUsingVirtualComms.shareProcessor;

import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;
import com.bezirk.test.sphere.testSphereUsingVirtualComms.UhuCommsMock;
import com.bezirk.test.sphere.testSphereUsingVirtualComms.VirtualCommsManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


public class TestWithOneReceiver {

    static int numOfDevices = 2;
    private static final Logger logger = LoggerFactory.getLogger(TestWithOneReceiver.class);
    private static VirtualCommsManager manager = new VirtualCommsManager();
    private static UhuCommsMock uhuCommsMock;

    /**
     * Creates the virtual devices required for this test and the UhuCommsMock object.
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        logger.info("******** Setting up TestWithOneReceiver ********");
        manager.setUp(numOfDevices);
        uhuCommsMock = manager.uhuCommsMock;
    }

    /**
     * Clears the registry of all the virtual devices created.
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("******** Shutting down TestWithOneReceiver ********");
        manager.destroy(numOfDevices);
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
     * If the share code used in the ShareRequest created by device0 is correct, then
     * the processing of the request will be successful at device1 and it will send a ShareResponse.
     * device0 then processes this response sent by device1.
     *
     * @throws Exception
     */
    @Test
    public final void validShareCodeReturnsTrue() throws Exception {
        logger.info("****** Starting the valid share code test *****");

        //Get the sphere ID of device0
        String sphereId0 = manager.device[0].sphereId;

        //Get the share code created in device1. As we cannot simulate the scanning of QR code,
        //the share code is directly given to device0 when creating ShareRequest object.
        String shareCode = manager.device[1].sphereRegistryWrapper.getShareCode(manager.device[1].sphereId);

        //Process the share code we got above and send the share request.
        assertTrue(manager.device[0].shareProcessor.processShareCode(shareCode, sphereId0));

        //Get the ShareRequest object received at device1.
        ShareRequest receivedRequest = (ShareRequest) uhuCommsMock.message.getMessage();
        //Process the received request at device1 and send a share response if processing was successful
        assertTrue(manager.device[1].shareProcessor.processRequest(receivedRequest));

        //Process the response received at device0.
        ShareResponse receivedResponse = (ShareResponse) uhuCommsMock.message.getMessage();
        assertTrue(manager.device[0].shareProcessor.processResponse(receivedResponse));

        logger.info("****** Ending the valid share code test *****");
    }


    /**
     * If the share code used in the ShareRequest created by device0 is incorrect, then
     * the processing of the request will be unsuccessful at device1.
     *
     * @throws Exception
     */
    @Test
    public final void invalidShareCodeReturnsFalse() throws Exception {
        logger.info("****** Starting the invalid share code test *****");

        //Get the sphere ID of device0
        String sphereId0 = manager.device[0].sphereId;

        //This is an invalid share code as it is not retrieved from device1.
        String shareCode = "abcdefg";

        //Process the share code we got above and send the share request.
        assertTrue(manager.device[0].shareProcessor.processShareCode(shareCode, sphereId0));

        //Get the ShareRequest object received at device1.
        ShareRequest receivedRequest = (ShareRequest) uhuCommsMock.message.getMessage();
        //Process the received request at device1 and send a share response if processing was successful
        assertFalse(manager.device[1].shareProcessor.processRequest(receivedRequest));

        logger.info("****** Ending the invalid share code test *****");
    }

}
