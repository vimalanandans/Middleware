package com.bezirk.proxy.api.impl;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.bezirk.api.messages.ProtocolRole;
import com.bezirk.util.MockProtocolRole;

/**
 *	 This testcase verifies the SubscribedRole by checking the event and stream topics.
 * 
 * @author AJC6KOR
 *
 */
public class SubscribedRoleTest {

	@Test
	public void test() {


		ProtocolRole pRole = new MockProtocolRole();
		com.bezirk.proxy.api.impl.SubscribedRole subscribedRole = new com.bezirk.proxy.api.impl.SubscribedRole(pRole);
		
		assertEquals("Description is not same as the mockprotocol description.",pRole.getDescription(),subscribedRole.getDescription());
		assertEquals("SubscribedRole name is not same as the mockprotocol name.",pRole.getProtocolName(),subscribedRole.getProtocolName());
		assertTrue("EventTopics is not same as the mockprotocol events.",Arrays.equals(pRole.getEventTopics(),subscribedRole.getEventTopics()));
		assertTrue("StreamTopics is not same as the mockprotocol streams.",Arrays.equals(pRole.getStreamTopics(),subscribedRole.getStreamTopics()));
		
		String protocolAsString =subscribedRole.getSubscribedProtocolRole();
		assertEquals("Protocol is not matching with the set value.", subscribedRole.toString(), subscribedRole.getProtocolRole(protocolAsString).toString());
		
	}

}