/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
