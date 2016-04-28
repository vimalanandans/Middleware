/**
 *
 */
package com.bezirk.middleware.objects;

import java.util.List;

/**
 * @author Rishabh Gulati
 */
public class BezirkDeviceInfo {
    private final String deviceId;
    private final String deviceName;
    private final String deviceType;
    private final BezirkDeviceRole deviceRole;
    private final boolean deviceActive;
    private final List<BezirkZirkInfo> zirks;

    /**
     * // We need Zirk Info not zirk id. this zirks will be deprecated
     *
     * @param deviceId
     * @param deviceName
     * @param deviceType
     * @param deviceRole
     * @param deviceActive
     * @param zirks
     */
    // We need Zerk Info not zirk id. this zirks will be deprecated
    public BezirkDeviceInfo(final String deviceId, final String deviceName,
                            final String deviceType, final BezirkDeviceRole deviceRole,
                            final boolean deviceActive, final List<BezirkZirkInfo> zirks) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceRole = deviceRole;
        this.deviceActive = deviceActive;
        this.zirks = zirks;
    }

    /**
     * @return the deviceId
     */
    public final String getDeviceId() {
        return deviceId;
    }

    /**
     * @return the deviceName
     */
    public final String getDeviceName() {
        return deviceName;
    }

    /**
     * @return the deviceType
     */
    public final String getDeviceType() {
        return deviceType;
    }

    /**
     * @return the deviceRole
     */
    public final BezirkDeviceRole getDeviceRole() {
        return deviceRole;
    }

    /**
     * @return the deviceActive
     */
    public final boolean isDeviceActive() {
        return deviceActive;
    }

    /**
     * @return the zirk info list
     */
    public final List<BezirkZirkInfo> getZirkList() {
        return zirks;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BezirkDeviceInfo [deviceId=" + deviceId + ",\ndeviceName="
                + deviceName + ",\ndeviceType=" + deviceType + ",\ndeviceRole="
                + deviceRole + ",\ndeviceActive=" + deviceActive + ",\nzirks="
                + zirks + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((deviceId == null) ? 0 : deviceId.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BezirkDeviceInfo other = (BezirkDeviceInfo) obj;
        if (deviceId == null) {
            if (other.deviceId != null)
                return false;
        } else if (!deviceId.equals(other.deviceId))
            return false;
        return true;
    }

    public enum BezirkDeviceRole {
        BEZIRK_MEMBER, // this device has control role for this sphere
        BEZIRK_CONTROL // this device has member role for this sphere
    }


}
