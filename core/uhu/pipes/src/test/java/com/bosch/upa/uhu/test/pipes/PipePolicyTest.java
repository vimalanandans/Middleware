package com.bosch.upa.uhu.test.pipes;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bezirk.protocols.penguin.v01.UserProxyRole;
import com.bezirk.protocols.smartservice.SmartServiceRole;
import com.bezirk.api.addressing.PipePolicy;
import com.bezirk.api.messages.ProtocolRole;

/**
 * 
 * @author Mansi
 *
 */
public class PipePolicyTest {
	private final String reason1 = "For test 1 protocol";
	private final String reason2 = "For test 2 protocol";
	private final ProtocolRole pRole1 = new UserProxyRole();
	private final ProtocolRole pRole2 = new SmartServiceRole();


	@Test
	public void test() {
		PipePolicy policy = new PipePolicy();
		policy.addProtocol(pRole1, reason1);
		policy.addProtocol(pRole2, reason2);
		String sPolicy = policy.serialize();
		System.out.println("Policy try out: "+ sPolicy);
		PipePolicy deserializedPolicy = PipePolicy.deserialize(sPolicy, PipePolicy.class);
		assertTrue(deserializedPolicy.getReason(pRole1.getProtocolName()).equals(reason1));
		assertTrue(deserializedPolicy.getReason(pRole2.getProtocolName()).equals(reason2));
	}
}
