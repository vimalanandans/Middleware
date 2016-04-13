package com.bezirk.sphere.messages;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the SphereDiscoveryRequest by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class SphereDiscoveryRequestTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(SphereDiscoveryRequestTest.class);


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up SphereDiscoveryRequestTest TestCase *****");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down SphereDiscoveryRequestTest TestCase *****");
	}

	@Test
	public void testSphereDiscoveryRequest() {

	String scanSphereId ="SCANNEDID";
	UhuServiceId serviceId = new UhuServiceId("ServiceA");
	UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	com.bezirk.sphere.messages.SphereDiscoveryRequest sphereDiscoveryRequest = new com.bezirk.sphere.messages.SphereDiscoveryRequest(scanSphereId, sender);
	String serializedMessage = sphereDiscoveryRequest.serialize();
	com.bezirk.sphere.messages.SphereDiscoveryRequest deserializedSphereDiscoveryRequest = com.bezirk.sphere.messages.SphereDiscoveryRequest.deserialize(serializedMessage, com.bezirk.sphere.messages.SphereDiscoveryRequest.class);
	assertEquals("Deserialized Request MessageId not equal to original sphere discovery request",sphereDiscoveryRequest.getMessageId(), deserializedSphereDiscoveryRequest.getMessageId());
	
	}

}