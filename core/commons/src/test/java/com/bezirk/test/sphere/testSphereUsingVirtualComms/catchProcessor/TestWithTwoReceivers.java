/**
 * Number of devices: 3 (1 sender device0 and 2 receiver device1)
 * Scenario:
 * 1. Catch request is sent from device0 to device1 with catch code got from device1.
 * 2. device1 processes the request and if successful, sends Catch response to device0.
 * 3. device2 will not be able to process the request as the catch code used in the request is not its own.
 * 4. device0 processes the response received from device1.
 */
package com.bezirk.test.sphere.testSphereUsingVirtualComms.catchProcessor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.test.sphere.testSphereUsingVirtualComms.VirtualCommsManager;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.test.sphere.testSphereUsingVirtualComms.UhuCommsMock;


public class TestWithTwoReceivers {

	private static Logger log = LoggerFactory.getLogger(TestWithTwoReceivers.class);
	private static VirtualCommsManager manager = new VirtualCommsManager();
	private static UhuCommsMock uhuCommsMock;
	static int numOfDevices = 3;

	/**
	 * Creates the virtual devices required for this test and the UhuCommsMock object.
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("******** Setting up TestWithTwoReceivers ********");
		manager.setUp(numOfDevices);
		uhuCommsMock = manager.uhuCommsMock;
	}

	/**
	 * Clears the registry of all the virtual devices created.
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("******** Shutting down TestWithTwoReceivers ********");
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
	 * If the catch code used in the CatchRequest created by device0 is from device1, then 
	 * the processing of the request will be successful at device1 and it will send a CatchResponse.
	 * But the processing at device2 will fail as the catch code is not its own.
	 * device0 then processes this response sent by device1.
	 * @throws Exception
	 */
	@Test
	public final void validCatchCodeReturnsTrue() throws Exception {
		log.info("****** Starting the valid catch code test *****");
		
		//Get the sphere ID of device0
		String sphereId0 = manager.device[0].sphereId;

		//Get the catch code created in device1. As we cannot simulate the scanning of QR code,
		//the catch code is directly given to device0 when creating CatchRequest object.
		String catchCode = manager.device[1].sphereRegistryWrapper.getShareCode(manager.device[1].sphereId);
		
		//Process the catch code we got above and send the catch request.
		assertTrue(manager.device[0].catchProcessor.processCatchCode(catchCode, sphereId0));
        
        //Get the CatchRequest object received at device1.
		CatchRequest receivedRequest = (CatchRequest) uhuCommsMock.message.getMessage();
        //Process the received request at device1 and send a catch response if processing was successful
        assertTrue(manager.device[1].catchProcessor.processRequest(receivedRequest));
        
        //The processing of request at device2 will be unsuccessful as the catch code in the request 
        //does not match its own catch code.
        assertFalse(manager.device[2].catchProcessor.processRequest(receivedRequest));
        
        //Process the response received at device0.
        CatchResponse receivedResponse = (CatchResponse) uhuCommsMock.message.getMessage();
        assertTrue(manager.device[0].catchProcessor.processResponse(receivedResponse));
        
		log.info("****** Ending the valid catch code test *****");
	}
	
}
