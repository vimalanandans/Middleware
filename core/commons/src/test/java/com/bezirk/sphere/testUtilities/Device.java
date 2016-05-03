/**
 *
 */
package com.bezirk.sphere.testUtilities;

import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.addressing.Location;

/**
 * @author Rishabh Gulati
 */
public final class Device implements BezirkDeviceInterface {

    public static final int DEVICE_NO = 0;
    public static final String DEVICE_ID = "TestDeviceId";
    public static final String DEVICE_NAME = "TestDeviceName";
    public static final String DEVICE_TYPE = "TestDeviceType";
    private static int deviceNo = DEVICE_NO;
    private final String deviceId;
    private final String deviceType;
    private String deviceName;

    public Device() {
        this.deviceId = DEVICE_ID + deviceNo;
        this.deviceName = DEVICE_NAME + deviceNo;
        this.deviceType = DEVICE_TYPE + deviceNo++;
    }

    public static void reset() {
        deviceNo = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * BezirkDeviceInterface#setDeviceName(java.lang.String)
     */
    @Override
    public boolean setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see BezirkDeviceInterface#getDeviceName()
     */
    @Override
    public String getDeviceName() {
        // TODO Auto-generated method stub
        return deviceName;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * BezirkDeviceInterface#setDeviceLocation(com.bosch.upa.
     * uhu.api.addressing.Location)
     */
    @Override
    public boolean setDeviceLocation(Location loc) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see BezirkDeviceInterface#getDeviceLocation()
     */
    @Override
    public Location getDeviceLocation() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see BezirkDeviceInterface#getDeviceId()
     */
    @Override
    public String getDeviceId() {
        // TODO Auto-generated method stub
        return deviceId;
    }

    @Override
    public boolean setDeviceType(String deviceType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getDeviceType() {
        // TODO Auto-generated method stub
        return deviceType;
    }

}
