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
package com.bezirk.middleware.objects;

import java.util.ArrayList;
import java.util.List;

public class BezirkDeviceInfo {
    private final String deviceId;
    private final String deviceName;
    private final String deviceType;
    private final BezirkDeviceRole deviceRole;
    private final boolean deviceActive;
    private final List<BezirkZirkInfo> zirks;

    // We need Zerk Info not zirk id. this zirks will be deprecated
    public BezirkDeviceInfo(final String deviceId, final String deviceName,
                            final String deviceType, final BezirkDeviceRole deviceRole,
                            final boolean deviceActive, final List<BezirkZirkInfo> zirks) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceRole = deviceRole;
        this.deviceActive = deviceActive;
        this.zirks = new ArrayList<>(zirks);
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
        return new ArrayList<>(zirks);
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
        BEZIRK_MEMBER,
        BEZIRK_CONTROL
    }


}
