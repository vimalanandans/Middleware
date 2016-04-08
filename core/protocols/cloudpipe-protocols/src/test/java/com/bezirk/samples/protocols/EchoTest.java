package com.bezirk.samples.protocols;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;


/**
 *	 This testcase verifies the EchoRequest, EchoRequestProtocol , EchoReply, EchoReplyProtocol 
 *   by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class EchoTest {

	@Test
	public void test() {

		testEchoRequest();
		
		testEchoRequestProtocol();
		
		testEchoReply();
		
		testEchoReplyProtocol();
	}


	private void testEchoRequest() {
		

		String text = "Sample";
		EchoRequest echoRequest = new EchoRequest();
		echoRequest.setText(text);

		String serializedEchoRequest = echoRequest.serialize();

		EchoRequest deserializedEchoRequest = EchoRequest
				.deserialize(serializedEchoRequest);

		assertEquals("Echo Text is not equal to the set value.", text,
				deserializedEchoRequest.getText());
	}

	private void testEchoRequestProtocol() {
		
		EchoRequestProtocol echoRequestProtocol = new EchoRequestProtocol();

		assertNotNull("Description is null for EchoRequestProtocol",
				echoRequestProtocol.getDescription());
		assertNotNull("StreamTopics is null for EchoRequestProtocol",
				echoRequestProtocol.getStreamTopics());
		assertEquals("StreamTopic length is not zero for EchoRequestProtocol",0,
				echoRequestProtocol.getStreamTopics().length);

		assertEquals("ProtocolName is different for EchoRequestProtocol.",
				EchoRequestProtocol.class.getSimpleName(),
				echoRequestProtocol.getProtocolName());

		List<String> eventTopicList = Arrays.asList(echoRequestProtocol
				.getEventTopics());
		assertTrue("EchoRequestProtocol is missing EchoRequest topic in event topic list.",
				eventTopicList.contains(EchoRequest.class.getSimpleName()));
	}

	private void testEchoReply() {

		String text="SampleReply";

		EchoReply echoReply = new EchoReply();
		echoReply.setText(text);
		
		String serializedEchoReply = echoReply.serialize();

		EchoReply deserializedEchoReply = EchoReply
				.deserialize(serializedEchoReply);

		assertEquals("Echo Text is not equal to the set value.", text,
				deserializedEchoReply.getText());
		
	}
	
	private void testEchoReplyProtocol() {
		
		EchoReplyProtocol echoReplyProtocol = new EchoReplyProtocol();

		assertNotNull("Description is null for EchoReplyProtocol",
				echoReplyProtocol.getDescription());
		assertNotNull("StreamTopics is null for EchoReplyProtocol",
				echoReplyProtocol.getStreamTopics());
		assertEquals("StreamTopic length is not zero for EchoReplyProtocol",0,
				echoReplyProtocol.getStreamTopics().length);

		assertEquals("ProtocolName is different for EchoReplyProtocol.",
				EchoReplyProtocol.class.getSimpleName(),
				echoReplyProtocol.getProtocolName());

		List<String> eventTopicList = Arrays.asList(echoReplyProtocol
				.getEventTopics());
		assertTrue("EchoReplyProtocol is missing Echoreply topic in event topic list.",
				eventTopicList.contains(EchoReply.class.getSimpleName()));
		
	}
	
}
