/**
 * 
 */
package com.bezirk.devices;

import com.bezirk.api.addressing.Location;

/**
 * @author GUR1PI
 *
 */
public final class DeviceDetails {

	private Location deviceLocation;
    private String deviceId;
    private String deviceName;
	/**
	 * @return the deviceLocation
	 */
	public Location getDeviceLocation() {
		return deviceLocation;
	}
	/**
	 * @param deviceLocation the deviceLocation to set
	 */
	public void setDeviceLocation(Location deviceLocation) {
		this.deviceLocation = deviceLocation;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

    
}
