package com.bezirk.middleware.java.device;

import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.addressing.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class JavaDevice extends Device {
    private static final Logger logger = LoggerFactory.getLogger(JavaDevice.class);

    public JavaDevice() {
        super(defaultDeviceId(), defaultDeviceName(), defaultDeviceType(), defaultDeviceLocation());
    }

    public JavaDevice(String deviceId, String deviceName, DeviceType deviceType, Location deviceLocation) {
        super(deviceId, deviceName, deviceType, deviceLocation);
    }

    private static String defaultDeviceId() {
        return UUID.randomUUID().toString();
    }

    private static String defaultDeviceName() {
        String deviceName;
        try {
            deviceName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Failed to get device host name, defaulting to JavaDevice class name", e);
            deviceName = JavaDevice.class.getSimpleName();
        }
        return deviceName;
    }

    private static DeviceType defaultDeviceType() {
        return DeviceType.DEFAULT;
    }

    private static Location defaultDeviceLocation() {
        return new Location(null, null, null);
    }

}
