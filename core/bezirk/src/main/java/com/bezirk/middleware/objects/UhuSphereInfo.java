package com.bezirk.middleware.objects;

import java.util.ArrayList;

/**
 * @author Rishabh Gulati
 */
public class UhuSphereInfo {
    private final String sphereID;
    private final String sphereName;
    private final String sphereType;
    private final ArrayList<UhuDeviceInfo> deviceList;
    private final ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> pipeList;
    // is this device is wons this sphere.
    // This is used only in local device. hence tostring doesn't have
    private boolean isThisDeviceOwnsSphere;

    /**
     * @param sphereID
     * @param sphereName
     * @param deviceList
     * @param pipeList
     */
    public UhuSphereInfo(final String sphereID, final String sphereName,
                         final String sphereType,
                         final ArrayList<UhuDeviceInfo> deviceList,
                         final ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> pipeList) {
        this.sphereID = sphereID;
        this.sphereType = sphereType;
        this.sphereName = sphereName;
        this.deviceList = deviceList;
        this.pipeList = pipeList;
    }

    /**
     * Copy constructor
     * Requires not null {@link UhuSphereInfo}} to be passed
     */

    public UhuSphereInfo(UhuSphereInfo other) {
        this.sphereID = other.sphereID;
        this.sphereType = other.sphereType;
        this.sphereName = other.sphereName;
        this.deviceList = other.deviceList;
        this.pipeList = other.pipeList;
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
     * @return the spheretype
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

    /**
     * @return the sphere owned by this device
     */
    public void setThisDeviceOwnsSphere(boolean status) {
        isThisDeviceOwnsSphere = status;
    }

    /**
     * @return the deviceList
     */
    public final ArrayList<UhuDeviceInfo> getDeviceList() {
        return deviceList;
    }

    /**
     * @return the pipeList
     */
    public final ArrayList<com.bezirk.middleware.objects.UhuPipeInfo> getPipeList() {
        return pipeList;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UhuSphereInfo [sphereID=" + sphereID + ",\nsphereName="
                + sphereName + ",\nsphereType=" + sphereType + ",\ndeviceList=" + deviceList + ",\npipeList="
                + pipeList + "]";
    }


}
