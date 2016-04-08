package com.bosch.upa.uhu.sphere.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the OwnerLeaveResponse by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class OwnerLeaveResponseTest {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(OwnerLeaveResponseTest.class);

	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceBId);



	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		LOGGER.info("***** Setting up OwnerLeaveResponseTest TestCase *****");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		LOGGER.info("***** Shutting down OwnerLeaveResponseTest TestCase *****");
	}

	
	@Test
	public void testOwnerLeaveResponse() {
		
		OwnerLeaveResponse ownerLeaveResponse = new OwnerLeaveResponse(sphereId, serviceId, recipient, true);
		String serializedMessage = ownerLeaveResponse.serialize();
		OwnerLeaveResponse deserializedOwnerLeaveResponse = OwnerLeaveResponse.deserialize(serializedMessage, OwnerLeaveResponse.class);
		assertEquals("SphereID not equal to the set value.",sphereId, deserializedOwnerLeaveResponse.getSphereID());
		assertEquals("ServiceID not equal to the set value.",serviceId,deserializedOwnerLeaveResponse.getServiceId());
		assertTrue("IsRemovedSuccessfully not equal to the set value.", deserializedOwnerLeaveResponse.isRemovedSuccessfully());
		assertEquals("Time not equal after deserialization.", ownerLeaveResponse.getTime(),deserializedOwnerLeaveResponse.getTime());
	
	}


}
