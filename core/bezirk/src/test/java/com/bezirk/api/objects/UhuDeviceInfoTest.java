package com.bezirk.api.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;



import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bezirk.api.objects.UhuDeviceInfo.UhuDeviceRole;

/**
 *	 This testcase verifies the UhuDeviceInfo by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class UhuDeviceInfoTest {

	@Test
	public void test() {

		testUhuDeviceInfo();

		testEquality();
		
	}


	private void testUhuDeviceInfo() {
		
		String deviceId ="Device123";
		String deviceName ="DeviceA";
		String deviceType= "PC";
		UhuDeviceRole deviceRole =UhuDeviceRole.UHU_MEMBER;
		boolean deviceActive = true;
		String serviceName ="ServiceA";
		String serviceId="Service123";
		String serviceType="MemberService";
		boolean active=true;
		boolean visible=true;
		com.bezirk.api.objects.UhuServiceInfo uhuServiceInfo = new com.bezirk.api.objects.UhuServiceInfo(serviceId, serviceName, serviceType, active, visible);
		List<com.bezirk.api.objects.UhuServiceInfo> services = new ArrayList<>();
		services.add(uhuServiceInfo);
		
		com.bezirk.api.objects.UhuDeviceInfo uhuDeviceInfo = new com.bezirk.api.objects.UhuDeviceInfo(deviceId, deviceName,
				deviceType, deviceRole, deviceActive, services);
		

		assertEquals("DeviceID is not equal to the set value.",deviceId,uhuDeviceInfo.getDeviceId());
		assertEquals("DeviceName is not equal to the set value.",deviceName,uhuDeviceInfo.getDeviceName());
		assertEquals("DeviceRole is not equal to the set value.",deviceRole,uhuDeviceInfo.getDeviceRole());
		assertEquals("DeviceType is not equal to the set value.",deviceType,uhuDeviceInfo.getDeviceType());
		assertEquals("ServiceList is not equal to the set value.",services,uhuDeviceInfo.getServiceList());
		assertTrue("Device is not considered active",uhuDeviceInfo.isDeviceActive());
		
		deviceName ="DeviceB";
		com.bezirk.api.objects.UhuDeviceInfo uhuDeviceInfoTemp = new com.bezirk.api.objects.UhuDeviceInfo(deviceId, deviceName,
				deviceType, deviceRole, deviceActive, services);
		assertFalse("Different uhuDeviceInfo has same string representation.",uhuDeviceInfo.toString().equalsIgnoreCase(uhuDeviceInfoTemp.toString()));

	}

	
	private void testEquality() {
		
		String deviceId ="Device123";
		String deviceName ="DeviceA";
		String deviceType= "PC";
		UhuDeviceRole deviceRole =UhuDeviceRole.UHU_MEMBER;
		boolean deviceActive = true;
		String serviceName ="ServiceA";
		String serviceId="Service123";
		String serviceType="MemberService";
		boolean active=true;
		boolean visible=true;
		com.bezirk.api.objects.UhuServiceInfo uhuServiceInfo = new com.bezirk.api.objects.UhuServiceInfo(serviceId, serviceName, serviceType, active, visible);
		List<UhuServiceInfo> services = new ArrayList<>();
		services.add(uhuServiceInfo);
		
		com.bezirk.api.objects.UhuDeviceInfo uhuDeviceInfo = new com.bezirk.api.objects.UhuDeviceInfo(deviceId, deviceName,
				deviceType, deviceRole, deviceActive, services);
		
		com.bezirk.api.objects.UhuDeviceInfo uhuDeviceInfoTemp = uhuDeviceInfo;
		
		assertTrue("Similar uhudevice info are considered unequal.",uhuDeviceInfo.equals(uhuDeviceInfoTemp));
		assertEquals("Similar uhudevice info have different hashcode.",uhuDeviceInfo.hashCode(),uhuDeviceInfoTemp.hashCode());

		uhuDeviceInfo=new com.bezirk.api.objects.UhuDeviceInfo(null, deviceName, deviceType, deviceRole, deviceActive, services);
		assertFalse("Different uhudevice info are considered equal.",uhuDeviceInfoTemp.equals(uhuDeviceInfo));
		assertFalse("Different uhudevice info are considered equal.",uhuDeviceInfo.equals(uhuDeviceInfoTemp));
		
		assertFalse("Uhuserviceinfo is considered equal to uhudeviceinfo.",uhuDeviceInfo.equals(uhuServiceInfo));
		assertNotEquals("Different uhudeviceinfo  have same hashcode.",uhuDeviceInfo.hashCode(),uhuDeviceInfoTemp.hashCode());
		
		uhuDeviceInfoTemp=new com.bezirk.api.objects.UhuDeviceInfo("DeviceB", deviceName, deviceType, deviceRole, deviceActive, services);
		assertFalse("Different uhudevice info are considered equal.",uhuDeviceInfoTemp.equals(uhuDeviceInfo));
		assertFalse("Different uhudevice info are considered equal.",uhuDeviceInfo.equals(uhuDeviceInfoTemp));

		
	}

}