package com.bezirk.devices;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author ajc6kor
 */
public class DeviceInformationTest {

    @Test
    public void test() {

        String deviceId = "Device123";
        String deviceName = "DeviceA";
        com.bezirk.devices.DeviceInformation deviceInformation = new com.bezirk.devices.DeviceInformation(deviceId, deviceName);

        assertEquals("DeviceID not equal to the set value.", deviceId, deviceInformation.getDeviceId());
        assertEquals("Device name not equal to the set value.", deviceName, deviceInformation.getDeviceName());
        assertTrue("DeviceInfo is not considered equal to itself.", deviceInformation.equals(deviceInformation));
        assertNotEquals("DeviceInfo is considered equal to null.", null, deviceInformation);


        deviceName = "DeviceB";
        com.bezirk.devices.DeviceInformation deviceInfo = new com.bezirk.devices.DeviceInformation(deviceId, deviceName);
        assertEquals("DeviceInfos with same device ID has different hashCode.", deviceInformation.hashCode(), deviceInfo.hashCode());
        assertTrue("DeviceInfos with same deviceID are not considered equal.", deviceInformation.equals(deviceInfo));

        deviceId = null;
        deviceInfo = new com.bezirk.devices.DeviceInformation(deviceId, deviceName);
        assertNotEquals("DeviceInfos with different device ID has same hashCode.", deviceInformation.hashCode(), deviceInfo.hashCode());
        assertFalse("DeviceInfos with different deviceID are considered equal.", deviceInformation.equals(deviceInfo));
        assertFalse("DeviceInfos with different deviceID are considered equal.", deviceInfo.equals(deviceInformation));

        deviceId = "Device23";
        deviceInfo = new com.bezirk.devices.DeviceInformation(deviceId, deviceName);
        assertNotEquals("DeviceInfos with different device ID has same hashCode.", deviceInformation.hashCode(), deviceInfo.hashCode());
        assertFalse("DeviceInfos with different deviceID are considered equal.", deviceInformation.equals(deviceInfo));

        assertFalse("DeviceInfo is considered equal to deviceId.", deviceInformation.equals(deviceId));

        deviceInformation = new com.bezirk.devices.DeviceInformation(null, deviceName);
        deviceInfo = new com.bezirk.devices.DeviceInformation(null, deviceName);

        assertTrue("DeviceInfos with same deviceID are not considered equal.", deviceInformation.equals(deviceInfo));
        assertEquals("Similar deviceInfos have different string representations.", deviceInformation.toString(), deviceInfo.toString());

    }

}
