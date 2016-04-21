package com.bezirk.devices;

import com.bezirk.middleware.addressing.Location;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ajc6kor
 */
public class DeviceDetailsTest {

    @Test
    public void test() {

        String deviceId = "Device123";
        Location deviceLocation = new Location("FLOOR1/ROOM1/TABLE");
        String deviceName = "DeviceA";


        com.bezirk.devices.DeviceDetails deviceDetails = new com.bezirk.devices.DeviceDetails();
        deviceDetails.setDeviceId(deviceId);
        deviceDetails.setDeviceLocation(deviceLocation);
        deviceDetails.setDeviceName(deviceName);

        assertEquals("DeviceID is not equal to the set value.", deviceId, deviceDetails.getDeviceId());
        assertEquals("Device location is not equal to the set value.", deviceLocation, deviceDetails.getDeviceLocation());
        assertEquals("Device name is not equal to the set value.", deviceName, deviceDetails.getDeviceName());

    }

}
