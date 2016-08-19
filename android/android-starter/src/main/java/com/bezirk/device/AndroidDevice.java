package com.bezirk.device;

import com.bezirk.middleware.addressing.Location;

import java.util.UUID;

import android.os.Build;

public class AndroidDevice extends Device {

    public AndroidDevice() {
        super(defaultDeviceId(), defaultDeviceName(), defaultDeviceType(), defaultDeviceLocation());
    }

    public AndroidDevice(String deviceId, String deviceName, DeviceType deviceType, Location deviceLocation) {
        super(deviceId, deviceName, deviceType, deviceLocation);
    }

    private static String defaultDeviceId() {
        return UUID.randomUUID().toString();
    }

    private static String defaultDeviceName() {
        String modelNumber = Build.MODEL;
        String manufacturerName = Build.MANUFACTURER;
        if (null != modelNumber && !modelNumber.isEmpty() && null != manufacturerName && !manufacturerName.isEmpty()) {
            return manufacturerName + modelNumber;
        }
        return AndroidDevice.class.getSimpleName() + UUID.randomUUID().toString().substring(0, 5);
    }

    private static  DeviceType defaultDeviceType() {
        return DeviceType.SMARTPHONE;
    }

    private static  Location defaultDeviceLocation() {
        return new Location(null, null, null);
    }

}
