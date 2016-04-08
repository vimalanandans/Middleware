package com.bosch.upa.uhu.control.messages;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the UnicastHeader POJO by retrieving the field values.
 * 
 * @author AJC6KOR
 *
 */
public class UnicastHeaderTest {

	private static final Logger log = LoggerFactory
			.getLogger(UnicastHeaderTest.class);

	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(
			serviceId);
	private static final String sphereName = "TestSphere";
	private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
	private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceBId);
	private static final String messageId = GenerateMsgId.generateEvtId(senderSEP);
	private static final String topic = "Message";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		log.info("***** Setting up UnicastHeaderTest TestCase *****");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down UnicastHeaderTest TestCase *****");
	}

	@Test
	public void testUnicastHeader() {

		UnicastHeader unicastHeader = prepareUnicastHeader();

		assertEquals("Recipient not equal to the set value.", recipient,
				unicastHeader.getRecipient());
		assertEquals("Sender not equal to the set value.", senderSEP,
				unicastHeader.getSenderSEP());
		assertEquals("SphereName not equal to the set value.",
				sphereName, unicastHeader.getSphereName());
		assertEquals("Topic not equal to the set value.",
				topic, unicastHeader.getTopic());
		assertEquals("MessageID not equal to the set value.", messageId,
				unicastHeader.getUniqueMsgId());

	}

	private UnicastHeader prepareUnicastHeader() {
		UnicastHeader unicastHeader = new UnicastHeader();
		unicastHeader.setRecipient(recipient);
		unicastHeader.setSenderSEP(senderSEP);
		unicastHeader.setSphereName(sphereName);
		unicastHeader.setTopic(topic);
		unicastHeader.setUniqueMsgId(messageId);
		return unicastHeader;
	}
}
