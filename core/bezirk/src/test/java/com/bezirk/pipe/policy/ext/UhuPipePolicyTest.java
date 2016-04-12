package com.bezirk.pipe.policy.ext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.bezirk.api.addressing.PipePolicy;

/**
 *	 This testcase verifies the UhuPipePolicy by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class UhuPipePolicyTest {

	@Test
	public void test() {
		

		HashMap<String, String> reasonMap = new HashMap<>();
		String protocolName = "TestProtocol";
		String reason = "Used for testing";
		reasonMap.put(protocolName, reason);
		
		PipePolicy pipePolicy = new  PipePolicy();
		pipePolicy.setReasonMap(reasonMap);
		com.bezirk.pipe.policy.ext.UhuPipePolicy uhuPipePolicy = new com.bezirk.pipe.policy.ext.UhuPipePolicy(pipePolicy);
		
		String serializedUhuPipePolicy =uhuPipePolicy.serialize();
		com.bezirk.pipe.policy.ext.UhuPipePolicy deserializedUhuPipePolicy = com.bezirk.pipe.policy.ext.UhuPipePolicy.deserialize(serializedUhuPipePolicy, com.bezirk.pipe.policy.ext.UhuPipePolicy.class);
		
		assertTrue("TestProtocol is missing in the list of protocol names in uhupipepolicy.",deserializedUhuPipePolicy.getProtocolNames().contains(protocolName));
		assertTrue("TestProtocol is missing in the list of allowed protocol names in uhupipepolicy.",deserializedUhuPipePolicy.getAllowedProtocols().contains(protocolName));
		assertEquals("TestProtocol is missing in the list of protocol names in uhupipepolicy.",reasonMap,deserializedUhuPipePolicy.getReasonMap());

		
		uhuPipePolicy.authorize(protocolName);
		assertTrue("Authorized protocol is considered as unauthorized by uhupipepolicy.",uhuPipePolicy.isAuthorized(protocolName));

		uhuPipePolicy.unAuthorize(protocolName);
		assertFalse("Unauthorized protocol is considered as authorized by uhupipepolicy.",uhuPipePolicy.isAuthorized(protocolName));
		
		
		String unknownProtocol="InvalidProtocol";
		uhuPipePolicy.authorize(unknownProtocol);
		assertFalse("Uhupolicy is authorizing protocol which is not present in reasonmap.",uhuPipePolicy.isAuthorized(protocolName));

		uhuPipePolicy.unAuthorize(unknownProtocol);
		assertFalse("Uhupolicy is unauthorizing protocol which is not present in reasonmap.",uhuPipePolicy.isAuthorized(protocolName));
	}

}
