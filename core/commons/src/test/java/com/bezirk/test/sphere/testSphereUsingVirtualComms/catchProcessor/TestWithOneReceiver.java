/**
 * Number of devices: 2 (1 sender device0 and 1 receiver device1)
 * Scenario:
 * 1. Catch request is sent from device0 with the catch code got from device1.
 * 2. device1 processes this request and sends a catch response to device0
 * 3. device0 processes the response. 
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


public class TestWithOneReceiver {

	private static Logger log = LoggerFactory.getLogger(TestWithTwoReceivers.class);
	private static VirtualCommsManager manager = new VirtualCommsManager();
	private static UhuCommsMock uhuCommsMock;
	static int numOfDevices = 2;

	/**
	 * Creates the virtual devices required for this test and the UhuCommsMock object.
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info("******** Setting up TestWithOneReceiver ********");
		manager.setUp(numOfDevices);
		uhuCommsMock = manager.uhuCommsMock;
	}

	/**
	 * Clears the registry of all the virtual devices created.
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info("******** Shutting down TestWithOneReceiver ********");
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
	 * If the catch code used in the CatchRequest created by device0 is correct, then 
	 * the processing of the request will be successful at device1 and it will send a CatchResponse.
	 * device0 then processes this response.
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
        
        //Process the response received at device0.
        CatchResponse receivedResponse = (CatchResponse) uhuCommsMock.message.getMessage();
        assertTrue(manager.device[0].catchProcessor.processResponse(receivedResponse));
        
		log.info("****** Ending the valid catch code test *****");
	}
	
	
	/**
	 * If the catch code used in the CatchRequest created by device0 is incorrect, then 
	 * the processing of the request will be unsuccessful at device1.
	 * @throws Exception
	 */
	@Test
	public final void invalidCatchCodeReturnsFalse() throws Exception {
		log.info("****** Starting the invalid catch code test *****");
		
		//Get the sphere ID of device0
		String sphereId0 = manager.device[0].sphereId;

		//This is an invalid catch code as it is not retrieved from device1.
		String catchCode = "abcdefg";
		
		//Process the catch code we got above and send the catch request.
		assertTrue(manager.device[0].catchProcessor.processCatchCode(catchCode, sphereId0));
        
        //Get the CatchRequest object received at device1.
		CatchRequest receivedRequest = (CatchRequest) uhuCommsMock.message.getMessage();
        //Process the received request at device1 and send a catch response if processing was successful
        assertFalse(manager.device[1].catchProcessor.processRequest(receivedRequest));

		log.info("****** Ending the invalid catch code test *****");
	}
	
}
