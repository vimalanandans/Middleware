package com.bezirk.api.objects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bezirk.api.objects.UhuDeviceInfo.UhuDeviceRole;

/**
 *	 This testcase verifies the UhuSphereInfo by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class UhuSphereInfoTest {

	@Test
	public void test() {

		String sphereID ="Sphere24";
		String sphereName="HomeSphere";
		String sphereType="OwnerSphere";
		ArrayList<com.bezirk.api.objects.UhuDeviceInfo> deviceList= getDeviceList();
		ArrayList<com.bezirk.api.objects.UhuPipeInfo> pipeList =getPipeList();
		com.bezirk.api.objects.UhuSphereInfo uhuSphereInfo = new com.bezirk.api.objects.UhuSphereInfo(sphereID, sphereName,
				sphereType, deviceList, pipeList);
		uhuSphereInfo.setThisDeviceOwnsSphere(true);
		
		assertEquals("DeviceList is not equal to the set value.",deviceList,uhuSphereInfo.getDeviceList());
		assertEquals("PipeList is not equal to the set value.",pipeList,uhuSphereInfo.getPipeList());
		assertEquals("SphereID is not equal to the set value.",sphereID,uhuSphereInfo.getSphereID());
		assertEquals("SphereName is not equal to the set value.",sphereName,uhuSphereInfo.getSphereName());
		assertEquals("SphereType is not equal to the set value.",sphereType,uhuSphereInfo.getSphereType());
		assertTrue("Device is not considered as owner of sphere.",uhuSphereInfo.isThisDeviceOwnsSphere());
		
		sphereName ="TestSphere";
		com.bezirk.api.objects.UhuSphereInfo uhuSphereInfoTemp = new UhuSphereInfo(sphereID, sphereName,
				sphereType, deviceList, pipeList);
		assertFalse("Different uhuSphereInfo has same string representation.",uhuSphereInfo.toString().equalsIgnoreCase(uhuSphereInfoTemp.toString()));
	}

	
	private ArrayList<com.bezirk.api.objects.UhuPipeInfo> getPipeList() {

		String pipeId ="Pipe24";
		String pipeName="TestPipe";
		String pipeURL="http://test.com";
		com.bezirk.api.objects.UhuPipeInfo uhuPipeInfo = new com.bezirk.api.objects.UhuPipeInfo(pipeId, pipeName, pipeURL);
		
		ArrayList<com.bezirk.api.objects.UhuPipeInfo> pipeList = new ArrayList<com.bezirk.api.objects.UhuPipeInfo>();
		pipeList.add(uhuPipeInfo);
		
		return pipeList;
	}

	
	private ArrayList<com.bezirk.api.objects.UhuDeviceInfo> getDeviceList() {
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
		
		ArrayList<com.bezirk.api.objects.UhuDeviceInfo> deviceList = new ArrayList<UhuDeviceInfo>();
		deviceList.add(uhuDeviceInfo);
		
		return deviceList;
	}

}
