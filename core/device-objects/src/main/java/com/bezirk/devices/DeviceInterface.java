/**
 *
 */
package com.bezirk.devices;

import com.bezirk.middleware.addressing.Location;

/**
 * @author GUR1PI
 */
public interface DeviceInterface {

    /**
     * Used for changing the device name
     *
     * @param deviceName The new device name to be configured
     * @return true : if the name was changed successfully
     * false: otherwise
     */
    boolean setDeviceName(String deviceName);


    /**
     * Provides the name of the device
     *
     * @return device name of the device
     * null if the device name is not configured
     */
    String getDeviceName();


    /**
     * Used for setting the device location
     *
     * @param loc The location of the device to be set
     * @return true: if the location was set successfully
     * false: otherwise
     */
    boolean setDeviceLocation(Location loc);

    /**
     * Provides the location of the device
     *
     * @return location of the device
     * null if the device location is not configured
     */
    Location getDeviceLocation();

    boolean setDeviceType(String deviceType);

    String getDeviceType();

    /**
     * Provides the unique ID of the device
     *
     * @return device id of the device
     * null if the device id is not configured
     */
    String getDeviceId();
}
