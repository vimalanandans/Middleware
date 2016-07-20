/**
 *
 */
package com.bezirk.device;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.middleware.addressing.Location;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;


/**
 * @author Rishabh Gulati
 */
public class Device implements DeviceInterface {
    private static final Logger logger = LoggerFactory.getLogger(Device.class);

    private String deviceId;
    private String deviceName;
    private String deviceType;
    private Location deviceLocation;

    @Deprecated
    public boolean initDevice() {
        //check if information is already present(persisted)
        //load the information
        //else initialize and persist
        deviceId = UUID.randomUUID().toString();

        deviceName = "Device-" + deviceId.substring(deviceId.length() - 5, deviceId.length());

        deviceType = "DeviceType(Phone/tablet/PC)";

        deviceLocation = null;

        return true;
    }

    public boolean initDevice(String deviceID, String deviceType) {
        //check if information is already present(persisted)
        //load the information
        //else initialize and persist
        if (deviceID == null) {
            logger.error("device id unknown. generating its own");
            this.deviceId = UUID.randomUUID().toString();
        } else {

            this.deviceId = deviceID;
        }

        this.deviceType = deviceType;

        //remove the BEZIRK_ prefix string
        deviceName = deviceType.replace("BEZIRK_", "");

        deviceName = deviceName + "-" + deviceId.substring(deviceId.length() - 5, deviceId.length());

        deviceLocation = null;
        return true;
    }

    public boolean deinitDevice() {
        return true;
    }

    @Override
    public boolean setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return true;
    }

    @Override
    public String getDeviceName() {

        return deviceName;
    }

    @Override
    public boolean setDeviceLocation(Location loc) {
        this.deviceLocation = loc;
        return true;
    }

    @Override
    public Location getDeviceLocation() {
        return deviceLocation;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public boolean setDeviceType(String deviceType) {
        this.deviceType = deviceType;
        return true;
    }

    @Override
    public String getDeviceType() {
        return deviceType;
    }

}