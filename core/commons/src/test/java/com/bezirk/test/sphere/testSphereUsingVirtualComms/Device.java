/**
 * This class creates a UPADeviceInterface object for a virtual device.
 */
package com.bezirk.test.sphere.testSphereUsingVirtualComms;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.addressing.Location;

/**
 * @author Karthik
 *
 */
public final class Device implements UPADeviceInterface {

	private final String deviceId;
	private String deviceName;
	private final String deviceType;
	private final int deviceNumber;
	public static final String DEVICE_ID = "TestDeviceId_";
	public static final String DEVICE_NAME = "TestDeviceName_";
	public static final String DEVICE_TYPE = "TestDeviceType_";

	public Device(int deviceNumber) {
		this.deviceNumber = deviceNumber;
		this.deviceId = DEVICE_ID + deviceNumber;
		this.deviceName = DEVICE_NAME + deviceNumber;
		this.deviceType = DEVICE_TYPE + deviceNumber++;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * UPADeviceInterface#setDeviceName(java.lang.String)
	 */
	@Override
	public boolean setDeviceName(String deviceName) {
		this.deviceName = deviceName;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see UPADeviceInterface#getDeviceName()
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
	 * UPADeviceInterface#setDeviceLocation(com.bosch.upa.
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
	 * @see UPADeviceInterface#getDeviceLocation()
	 */
	@Override
	public Location getDeviceLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see UPADeviceInterface#getDeviceId()
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
