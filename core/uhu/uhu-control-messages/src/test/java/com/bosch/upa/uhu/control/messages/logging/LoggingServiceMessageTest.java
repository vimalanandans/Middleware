package com.bosch.upa.uhu.control.messages.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the LoggingServiceMessage by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class LoggingServiceMessageTest {

	private static final Logger log = LoggerFactory
			.getLogger(LoggingServiceMessageTest.class);

	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(
			serviceId);


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.info("***** Setting up LoggingServiceMessageTest TestCase *****");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down LoggingServiceMessageTest TestCase *****");
	}

	@Test
	public void testLoggingServiceMessage() {

		String serverIp = "TEST_IP";
		
		int serverPort = 1999; 
		String[] sphereList =new String[]{"Sphere1","Sphere2","Sphere3"};
		boolean loggingStatus  =true;
		LoggingServiceMessage loggingServiceMessage = new LoggingServiceMessage(sender, sphereId, serverIp , serverPort, sphereList, loggingStatus);
		String serializedMessage = loggingServiceMessage.serialize();
		LoggingServiceMessage deserializedLoggingServiceMessage = LoggingServiceMessage
				.deserialize(serializedMessage, LoggingServiceMessage.class);
		assertEquals("LoggingServiceIP not equal to the set value.",serverIp,
				deserializedLoggingServiceMessage.getRemoteLoggingServiceIP());
		assertEquals("LoggingServicePort not set properly.",
				serverPort,deserializedLoggingServiceMessage.getRemoteLoggingServicePort());
		String[] deserializedSphereList = deserializedLoggingServiceMessage.getSphereList();
		assertTrue("SphereList not equal to the set value.", deserializedSphereList!=null && Arrays.asList(deserializedSphereList).containsAll(Arrays.asList(sphereList)));
		assertEquals("LoggingStatus not equal to the set value.",
				loggingStatus, deserializedLoggingServiceMessage.isLoggingStatus());
		
		/*Check Updation using setters*/
		loggingStatus =false;
		serverIp ="TEST_UPDATED_IP";
		serverPort =1001;
		sphereList = new String[]{"7899","9908","7788"};
		loggingServiceMessage.setLoggingStatus(loggingStatus);
		loggingServiceMessage.setRemoteLoggingServiceIP(serverIp);
		loggingServiceMessage.setRemoteLoggingServicePort(serverPort);
		loggingServiceMessage.setSphereList(sphereList);
		loggingServiceMessage = new LoggingServiceMessage(sender, sphereId, serverIp , serverPort, sphereList, loggingStatus);
		serializedMessage = loggingServiceMessage.serialize();
		deserializedLoggingServiceMessage = LoggingServiceMessage
				.deserialize(serializedMessage, LoggingServiceMessage.class);
		assertEquals("LoggingServiceIP not equal to the set value.",serverIp,
				deserializedLoggingServiceMessage.getRemoteLoggingServiceIP());
		assertEquals("LoggingServicePort not set properly.",
				serverPort,deserializedLoggingServiceMessage.getRemoteLoggingServicePort());
		deserializedSphereList = deserializedLoggingServiceMessage.getSphereList();
		assertTrue("SphereList not equal to the set value.", deserializedSphereList!=null && Arrays.asList(deserializedSphereList).containsAll(Arrays.asList(sphereList)));
		assertEquals("LoggingStatus not equal to the set value.",
				loggingStatus, deserializedLoggingServiceMessage.isLoggingStatus());
		
	}

}
