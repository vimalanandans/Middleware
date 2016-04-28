package com.bezirk.device;

import com.bezirk.middleware.addressing.Location;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies BezirkDevice by setting the values and retrieving them.
 *
 * @author AJC6KOR
 */
public class BezirkDeviceTest {

    @Test
    public void test() {

        Location loc = new Location("FLOOR1/ROOM1/TABLE");
        String deviceName = "DeviceA";
        String deviceType = BezirkDeviceType.UHU_DEVICE_TYPE_CAR;
        String deviceId = "Device123";

        BezirkDevice bezirkDevice = new BezirkDevice();
        bezirkDevice.initDevice(deviceId, deviceType);
        bezirkDevice.setDeviceLocation(loc);
        bezirkDevice.setDeviceName(deviceName);

        assertEquals("Location is not equal to the set value.", loc, bezirkDevice.getDeviceLocation());
        assertEquals("DeviceID is not equal to the set value.", deviceId, bezirkDevice.getDeviceId());
        assertEquals("Device Name is not equal to the set value.", deviceName, bezirkDevice.getDeviceName());
        assertEquals("Device Type is not equal to the set value.", deviceType, bezirkDevice.getDeviceType());

        deviceType = BezirkDeviceType.UHU_DEVICE_TYPE_COFFEE;
        bezirkDevice.setDeviceType(deviceType);

        assertEquals("Device Type is not equal to the set value.", deviceType, bezirkDevice.getDeviceType());

        assertTrue("Unable to deinit device.", bezirkDevice.deinitDevice());

        assertTrue("Unable to init device with null deviceID.", bezirkDevice.initDevice(null, deviceType));
    }

}
