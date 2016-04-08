package com.bosch.upa.uhu.proxy.api.impl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the equals and hashcode apis of UhuServiceId.
 * 
 * @author AJC6KOR
 *
 */
public class UhuServiceIdTest {

	@Test
	public void test() {

		String serviceId = "Service25";
		UhuServiceId uhuServiceId = new UhuServiceId(serviceId);
		
		assertEquals("ServiceId is not equal to the set value.",serviceId,uhuServiceId.getUhuServiceId());
		
		UhuServiceId uhuServiceIdTemp = null;
		assertFalse("UhuserviceID is considered equal to null.",uhuServiceId.equals(uhuServiceIdTemp));
		
		assertFalse("UhuserviceID is considered equal to serviceId.",uhuServiceId.equals(serviceId));

				
		uhuServiceIdTemp = new UhuServiceId(null);
		assertFalse("UhuserviceIDs with different serviceIds are considered equal.",uhuServiceId.equals(uhuServiceIdTemp));
		assertFalse("UhuserviceIDs with different serviceIds are considered equal.",uhuServiceIdTemp.equals(uhuServiceId));
		assertNotEquals("UhuserviceIDs with different serviceIds have same hashcode.",uhuServiceId.hashCode(),uhuServiceIdTemp.hashCode());

		uhuServiceIdTemp = new UhuServiceId(serviceId);
		assertTrue("UhuserviceIDs with same serviceIds are considered unequal.",uhuServiceId.equals(uhuServiceIdTemp));
		assertEquals("UhuserviceIDs with same serviceIds have different hashcode.",uhuServiceId.hashCode(),uhuServiceIdTemp.hashCode());

		serviceId ="Service26";
		uhuServiceIdTemp = new UhuServiceId(serviceId);
		assertFalse("UhuserviceIDs with different serviceIds are considered equal.",uhuServiceId.equals(uhuServiceIdTemp));
		assertNotEquals("UhuserviceIDs with different serviceIds have same hashcode.",uhuServiceId.hashCode(),uhuServiceIdTemp.hashCode());
	
		
	}

}
