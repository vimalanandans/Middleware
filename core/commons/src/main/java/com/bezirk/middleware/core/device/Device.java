package com.bezirk.middleware.core.device;

import com.bezirk.middleware.addressing.Location;

public abstract class Device {
    private String deviceId;
    private String deviceName;
    private DeviceType deviceType;
    private Location deviceLocation;

    public enum DeviceType {
        DEFAULT, PC, SMARTPHONE, CAR, COFFEE, EMBEDDED
    }

    protected Device(String deviceId, String deviceName, DeviceType deviceType, Location deviceLocation) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceLocation = deviceLocation;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location deviceLocation) {
        this.deviceLocation = deviceLocation;
    }
}
