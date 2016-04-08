package com.bosch.upa.uhu.device;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bezirk.api.addressing.Location;

/**
 *	 This testcase verifies UhuDevice by setting the values and retrieving them.
 *
 * @author AJC6KOR
 *
 */
public class UhuDeviceTest {

	@Test
	public void test() {

		Location loc = new Location("FLOOR1/ROOM1/TABLE");
		String deviceName ="DeviceA";
		String deviceType = UhuDeviceType.UHU_DEVICE_TYPE_CAR;
		String deviceId = "Device123";

		UhuDevice uhuDevice = new UhuDevice();
		uhuDevice.initDevice(deviceId, deviceType);
		uhuDevice.setDeviceLocation(loc);
		uhuDevice.setDeviceName(deviceName);
		
		assertEquals("Location is not equal to the set value.",loc, uhuDevice.getDeviceLocation());
		assertEquals("DeviceID is not equal to the set value.",deviceId, uhuDevice.getDeviceId());
		assertEquals("Device Name is not equal to the set value.",deviceName, uhuDevice.getDeviceName());	
		assertEquals("Device Type is not equal to the set value.",deviceType, uhuDevice.getDeviceType());
		
		deviceType = UhuDeviceType.UHU_DEVICE_TYPE_COFFEE;
		uhuDevice.setDeviceType(deviceType);
		
		assertEquals("Device Type is not equal to the set value.",deviceType, uhuDevice.getDeviceType());

		assertTrue("Unable to deinit device.",uhuDevice.deinitDevice());
		
		assertTrue("Unable to init device with null deviceID.",uhuDevice.initDevice(null,deviceType));
	}

}
