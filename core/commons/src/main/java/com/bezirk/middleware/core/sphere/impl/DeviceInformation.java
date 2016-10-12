package com.bezirk.middleware.core.sphere.impl;

import java.io.Serializable;

public class DeviceInformation implements Serializable {

    private static final long serialVersionUID = -7659289630471500105L;

    private String deviceName;

    private String deviceType;

    public DeviceInformation() {
        // For Gson
    }

    public DeviceInformation(String deviceName, String deviceType) {
        super();
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    /**
     * @return the deviceName
     */
    public final String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName the deviceName to set
     */
    public final void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return the deviceType
     */
    public final String getDeviceType() {
        return deviceType;
    }

    /**
     * @param deviceType the deviceType to set
     */
    public final void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceInformation other = (DeviceInformation) obj;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (deviceType == null) {
            if (other.deviceType != null)
                return false;
        } else if (!deviceType.equals(other.deviceType))
            return false;
        return true;
    }

}
