/**
 * Number of devices: 3 (1 sender device0 and 2 receivers device1 and device2)
 * Scenario:
 * 1. Share request is sent from device0 to device1with share code got from device1.
 * 2. device1 processes the request and if successful, sends Share response to device0.
 * 3. device2 will not be able to process the request as the share code used in the request is not its own.
 * 4. device0 processes the response received from device1.
 */
package com.bezirk.sphere.testSphereUsingVirtualComms.shareProcessor;

import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;
import com.bezirk.sphere.testSphereUsingVirtualComms.BezirkCommsMock;
import com.bezirk.sphere.testSphereUsingVirtualComms.VirtualCommsManager;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;


public class TestWithTwoReceivers {

    private static final Logger log = LoggerFactory.getLogger(TestWithTwoReceivers.class);
    private static final VirtualCommsManager manager = new VirtualCommsManager();
    static int numOfDevices = 3;
    private static BezirkCommsMock bezirkCommsMock;

    /**
     * Creates the virtual devices required for this test and the BezirkCommsMock object.
     *
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("******** Setting up TestWithTwoReceiver ********");
        manager.setUp(numOfDevices);
        bezirkCommsMock = manager.bezirkCommsMock;
    }

    /**
     * Clears the registry of all the virtual devices created.
     *
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("******** Shutting down TestWithTwoReceiver ********");
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
     * If the share code used in the ShareRequest created by device0 is from device1, then
     * the processing of the request will be successful at device1 and it will send a ShareResponse.
     * But the processing at device2 will fail as the share code is not its own.
     * device0 then processes this response sent by device1.
     *
     * @throws Exception
     */
    @Test
    public final void twoReceivers() throws Exception {
        log.info("****** Starting the test with two receivers *****");

        //Get the sphere ID of device0
        String sphereId0 = manager.device[0].sphereId;

        //Get the share code created in device1. As we cannot simulate the scanning of QR code,
        //the share code is directly given to device0 when creating ShareRequest object.
        String shareCode = manager.device[1].sphereRegistryWrapper.getShareCode(manager.device[1].sphereId);

        //Process the share code we got above and send the share request.
        assertTrue(manager.device[0].shareProcessor.processShareCode(shareCode, sphereId0));

        //Get the ShareRequest object received at device1.
        ShareRequest receivedRequest = (ShareRequest) bezirkCommsMock.message.getMessage();
        //Process the received request at device1 and send a share response if processing was successful
        assertTrue(manager.device[1].shareProcessor.processRequest(receivedRequest));

        //The processing of request at device2 will be unsuccessful as the share code in the request 
        //does not match its own share code.
        assertFalse(manager.device[2].shareProcessor.processRequest(receivedRequest));

        //Process the response received at device0.
        ShareResponse receivedResponse = (ShareResponse) bezirkCommsMock.message.getMessage();
        assertTrue(manager.device[0].shareProcessor.processResponse(receivedResponse));

        log.info("****** Ending the test with two receivers *****");
    }

}
