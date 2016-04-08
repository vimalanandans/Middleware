package com.bosch.upa.uhu.comms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestUhuCommsPC {

	@Test
	public void testLoadProperties(){
		try {
			assertNotNull("Properties is null", UhuCommsPC.loadProperties());
			assertTrue("UhuCommsPc properties is empty.",UhuCommsPC.loadProperties().size() >0);
			assertFalse("UhuCommsPc properties is empty.",UhuCommsPC.loadProperties().isEmpty());
			//Following test case will fail if we remove the EmulticastPortVal property or if the port value is set as more than 9999
			int EmulticastPortVal= Integer.valueOf(UhuCommsPC.loadProperties().getProperty("EMulticastPort"));
			assertTrue("EmulticastPortVal value is different from default value range.",0<=EmulticastPortVal && 9999 >= EmulticastPortVal);
		} catch (Exception e) {

			fail("Exception in fetching uhucommsPC properties. "+e.getMessage());
		
		}
	}
	
	

}
