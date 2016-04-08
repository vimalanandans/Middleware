package com.bosch.upa.uhu.sphere.messages;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the MemberLeaveRequest by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class MemberLeaveRequestTest {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(MemberLeaveRequestTest.class);

	private static final String sphereId = "TestSphere";
	private static final String sphereName = "Test";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		LOGGER.info("***** Setting up MemberLeaveRequestTest TestCase *****");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		LOGGER.info("***** Shutting down MemberLeaveRequestTest TestCase *****");
	}

	
	@Test
	public void testMemberLeaveRequest() {
		
		MemberLeaveRequest memberLeaveRequest = new MemberLeaveRequest(sphereId, serviceId, sphereName, sender);
		String serializedMessage = memberLeaveRequest.serialize();
		MemberLeaveRequest deserializedMemberLeaveRequest = MemberLeaveRequest.deserialize(serializedMessage, MemberLeaveRequest.class);
		assertEquals("ServiceId not equal to the set value.",serviceId, deserializedMemberLeaveRequest.getServiceId());

		
	}

}
