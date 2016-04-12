package com.bezirk.proxy.api.impl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the EchoRequest by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class UhuServiceEndPointTest {

	@Test
	public void test() {

		com.bezirk.proxy.api.impl.UhuServiceId serviceId = new com.bezirk.proxy.api.impl.UhuServiceId("Service25");
		com.bezirk.proxy.api.impl.UhuServiceEndPoint uhuServiceEndPoint = new UhuServiceEndPoint(serviceId );
		assertEquals("UhuServiceId is not matching with the set value.",serviceId,uhuServiceEndPoint.getUhuServiceId());
		
	}

}
