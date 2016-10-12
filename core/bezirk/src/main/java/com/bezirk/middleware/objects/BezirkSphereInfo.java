package com.bezirk.middleware.objects;

import java.util.ArrayList;

public class BezirkSphereInfo {
    private final String sphereID;
    private final String sphereName;
    private final String sphereType;
    private final ArrayList<BezirkDeviceInfo> deviceList;

    // current device owns this sphere.
    private boolean isThisDeviceOwnsSphere;

    public BezirkSphereInfo(final String sphereID, final String sphereName,
                            final String sphereType,
                            final ArrayList<BezirkDeviceInfo> deviceList) {
        this.sphereID = sphereID;
        this.sphereType = sphereType;
        this.sphereName = sphereName;
        this.deviceList = new ArrayList<>(deviceList);
    }

    /**
     * Copy constructor
     * Requires not null {@link BezirkSphereInfo}} to be passed
     */

    public BezirkSphereInfo(BezirkSphereInfo other) {
        this(other.sphereID, other.sphereName, other.sphereType, other.deviceList);
    }


    /**
     * @return the sphereID
     */
    public final String getSphereID() {
        return sphereID;
    }

    /**
     * @return the sphereName
     */
    public final String getSphereName() {
        return sphereName;
    }

    /**
     * @return the <code>sphereType</code>
     */
    public final String getSphereType() {
        return sphereType;
    }

    /**
     * @return the sphere owned by this device
     */
    public boolean isThisDeviceOwnsSphere() {
        return isThisDeviceOwnsSphere;
    }

    public void setThisDeviceOwnsSphere(boolean status) {
        isThisDeviceOwnsSphere = status;
    }

    /**
     * @return the deviceList
     */
    public final ArrayList<BezirkDeviceInfo> getDeviceList() {
        return new ArrayList<>(deviceList);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BezirkSphereInfo [sphereID=" + sphereID + ",\nsphereName="
                + sphereName + ",\nsphereType=" + sphereType + ",\ndeviceList=" + deviceList +
                "]";
    }


}
