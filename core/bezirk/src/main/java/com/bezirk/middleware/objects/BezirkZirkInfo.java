/**
 *
 */
package com.bezirk.middleware.objects;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * @author Rishabh Gulati
 */
public class BezirkZirkInfo {
    private final String zirkId;
    private final String zirkName;
    private final String zirkType;
    private final boolean visible; // true if zirk is visible
    private boolean active;

    @Deprecated
    public BezirkZirkInfo(ZirkId zirkId, String zirkName, String zirkType, boolean active,
                          boolean visible) {
        this.zirkId = zirkId.getZirkId();
        this.zirkType = zirkType;
        this.active = active;
        this.visible = visible;
        this.zirkName = zirkName;
    }

    public BezirkZirkInfo(String zirkId, String zirkName, String zirkType, boolean active,
                          boolean visible) {
        this.zirkId = zirkId;
        this.zirkType = zirkType;
        this.active = active;
        this.visible = visible;
        this.zirkName = zirkName;
    }

    /**
     * @return the zirkId
     */
    @Deprecated
    public final ZirkId getBezirkZirkId() {
        return new ZirkId(zirkId);
    }

    public String getZirkId() {
        return zirkId;
    }

    public final String getZirkName() {
        return zirkName;
    }

    /**
     * @return the zirkType
     */
    public final String getZirkType() {
        return zirkType;
    }

    /**
     * @return the active
     */
    public final boolean isActive() {
        return active;
    }

    public void setActive(boolean status) {
        active = status;
    }

    /**
     * @return the visible
     */
    public final boolean isVisible() {
        return visible;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BezirkZirkInfo [zirkId=" + zirkId + ",\nzirkName="
                + zirkName + ",\nzirkType=" + zirkType + ",\nactive="
                + active + ",\nvisible=" + visible + "]";
    }


}
