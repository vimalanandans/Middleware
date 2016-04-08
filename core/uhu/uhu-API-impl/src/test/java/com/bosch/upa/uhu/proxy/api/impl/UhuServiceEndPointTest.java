package com.bosch.upa.uhu.proxy.api.impl;

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

		UhuServiceId serviceId = new UhuServiceId("Service25");
		UhuServiceEndPoint uhuServiceEndPoint = new UhuServiceEndPoint(serviceId );
		assertEquals("UhuServiceId is not matching with the set value.",serviceId,uhuServiceEndPoint.getUhuServiceId());
		
	}

}
