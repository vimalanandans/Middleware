package com.bosch.upa.uhu.api.objects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bosch.upa.uhu.api.objects.UhuDeviceInfo.UhuDeviceRole;

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
		ArrayList<UhuDeviceInfo> deviceList= getDeviceList();
		ArrayList<UhuPipeInfo> pipeList =getPipeList();
		UhuSphereInfo uhuSphereInfo = new UhuSphereInfo(sphereID, sphereName,
				sphereType, deviceList, pipeList);
		uhuSphereInfo.setThisDeviceOwnsSphere(true);
		
		assertEquals("DeviceList is not equal to the set value.",deviceList,uhuSphereInfo.getDeviceList());
		assertEquals("PipeList is not equal to the set value.",pipeList,uhuSphereInfo.getPipeList());
		assertEquals("SphereID is not equal to the set value.",sphereID,uhuSphereInfo.getSphereID());
		assertEquals("SphereName is not equal to the set value.",sphereName,uhuSphereInfo.getSphereName());
		assertEquals("SphereType is not equal to the set value.",sphereType,uhuSphereInfo.getSphereType());
		assertTrue("Device is not considered as owner of sphere.",uhuSphereInfo.isThisDeviceOwnsSphere());
		
		sphereName ="TestSphere";
		UhuSphereInfo uhuSphereInfoTemp = new UhuSphereInfo(sphereID, sphereName,
				sphereType, deviceList, pipeList);
		assertFalse("Different uhuSphereInfo has same string representation.",uhuSphereInfo.toString().equalsIgnoreCase(uhuSphereInfoTemp.toString()));
	}

	
	private ArrayList<UhuPipeInfo> getPipeList() {

		String pipeId ="Pipe24";
		String pipeName="TestPipe";
		String pipeURL="http://test.com";
		UhuPipeInfo uhuPipeInfo = new UhuPipeInfo(pipeId, pipeName, pipeURL);
		
		ArrayList<UhuPipeInfo> pipeList = new ArrayList<UhuPipeInfo>();
		pipeList.add(uhuPipeInfo);
		
		return pipeList;
	}

	
	private ArrayList<UhuDeviceInfo> getDeviceList() {
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
		UhuServiceInfo uhuServiceInfo = new UhuServiceInfo(serviceId, serviceName, serviceType, active, visible);
		List<UhuServiceInfo> services = new ArrayList<>();
		services.add(uhuServiceInfo);
		
		UhuDeviceInfo uhuDeviceInfo = new UhuDeviceInfo(deviceId, deviceName, 
				deviceType, deviceRole, deviceActive, services);
		
		ArrayList<UhuDeviceInfo> deviceList = new ArrayList<UhuDeviceInfo>();
		deviceList.add(uhuDeviceInfo);
		
		return deviceList;
	}

}
