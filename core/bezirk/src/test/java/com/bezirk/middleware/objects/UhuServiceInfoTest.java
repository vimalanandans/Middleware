package com.bezirk.middleware.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *	 This testcase verifies the UhuServiceInfo by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */

public class UhuServiceInfoTest {

	@Test
	public void test() {

		String serviceName ="ServiceA";
		String serviceId="Service123";
		String serviceType="MemberService";
		boolean active=true;
		boolean visible=true;
		com.bezirk.middleware.objects.UhuServiceInfo uhuServiceInfo = new com.bezirk.middleware.objects.UhuServiceInfo(serviceId, serviceName, serviceType, active, visible);

		assertEquals("ServiceId is not equal to the set value.",serviceId,uhuServiceInfo.getServiceId());
		assertEquals("ServiceName is not equal to the set value.",serviceName,uhuServiceInfo.getServiceName());
		assertEquals("ServiceType is not equal to the set value.",serviceType,uhuServiceInfo.getServiceType());
		assertTrue("Service is considered inactive.",uhuServiceInfo.isActive());
		assertTrue("Service is not visible.",uhuServiceInfo.isVisible());
		
		uhuServiceInfo.setActive(false);
		assertFalse("Inactive service is considered active.",uhuServiceInfo.isActive());
		
		serviceName ="ServiceB";
		com.bezirk.middleware.objects.UhuServiceInfo uhuServiceInfoTemp = new com.bezirk.middleware.objects.UhuServiceInfo(serviceId, serviceName, serviceType, active, visible);
		assertFalse("Different uhuserviceinfo has same string representation.",uhuServiceInfo.toString().equalsIgnoreCase(uhuServiceInfoTemp.toString()));

	}

}
