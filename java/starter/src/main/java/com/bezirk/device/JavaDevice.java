package com.bezirk.device;

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

    private static final String defaultDeviceId() {
//        final byte[] macAddress = NetworkManager
//                .getLocalMACAddress();
//
//        if (null != macAddress) {
//            return Hex.encodeToString(macAddress);
//        }
        return UUID.randomUUID().toString();
    }

    private static final String defaultDeviceName() {
        String deviceName;
        try {
            deviceName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            deviceName = JavaDevice.class.getSimpleName();
        }
        return deviceName;
    }

    private static final DeviceType defaultDeviceType() {
        return DeviceType.DEFAULT;
    }

    private static final Location defaultDeviceLocation() {
        return new Location(null, null, null);
    }

}
