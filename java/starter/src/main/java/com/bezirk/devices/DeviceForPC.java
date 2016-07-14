package com.bezirk.devices;

import com.bezirk.middleware.addressing.Location;
import com.bezrik.network.BezirkNetworkUtilities;

import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DeviceForPC implements DeviceInterface {
    private static final Logger logger = LoggerFactory.getLogger(DeviceForPC.class);
    private final com.bezirk.devices.DeviceDetails deviceDetails;
    public static final Location deviceLocation = new Location("Floor1/null/null");

    /**
     * The constructor is used for setting up the device information like deviceId and deviceName which can be used other modules like sphere
     */
    public DeviceForPC() {
        deviceDetails = new DeviceDetails();
        String deviceName;
        deviceName = fetchDeviceName();
        deviceDetails.setDeviceId(Hex.encodeToString(BezirkNetworkUtilities
                .getLocalMACAddress()));
        deviceDetails.setDeviceName(deviceName);
        deviceDetails.setDeviceLocation(deviceLocation);
    }

    private String fetchDeviceName() {
        String deviceName;
        try {
            deviceName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            deviceName = "BEZIRK-PC";
            logger.error("Exception in fetching hostname.", e);
        }
        return deviceName;
    }

    /**
     * Used for changing the device name
     *
     * @param deviceName The new device name to be configured
     * @return true : if the name was changed successfully
     * false: otherwise
     */
    @Override
    public boolean setDeviceName(String deviceName) {
        return false;
    }

    /**
     * Provides the name of the device
     *
     * @return device name of the device
     * null if the device name is not configured
     */
    @Override
    public String getDeviceName() {
        if (deviceDetails != null) {
            return deviceDetails.getDeviceName();
        }
        return null;
    }

    /**
     * Used for setting the device location. The device location is currently not persisted into
     * the shared preferences
     *
     * @param loc The location of the device to be set
     * @return <code>true</code> if the location was set successfully
     */
    @Override
    public boolean setDeviceLocation(Location loc) {
        if (deviceDetails != null) {
            deviceDetails.setDeviceLocation(loc);
            return true;
        }
        return false;
    }

    /**
     * If the zirk location is not set it uses the device location
     *
     * @return the Device Location
     */
    public Location getDeviceLocation() {
        if (deviceDetails != null) {
            return deviceDetails.getDeviceLocation();
        }
        return null;
    }

    /**
     * Provides the unique ID of the device
     *
     * @return device id of the device
     * null if the device id is not configured
     */
    @Override
    public String getDeviceId() {
        if (deviceDetails != null) {
            return deviceDetails.getDeviceId();
        }
        return null;
    }

    @Override
    public boolean setDeviceType(String deviceType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getDeviceType() {
        return null;
    }

}
