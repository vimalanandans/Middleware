/**
 * 
 */
package com.bosch.upa.devices;

/**
 * This is a wrapper for sending device related information to the inviting
 * device When a device invites others by displaying something like a QR code,
 * the devices joining the Sphere would need to send the information regarding
 * the deviceIds and deviceNames associated with the services they want to share
 * This object encapsulated the deviceId and deviceName information which is
 * then stored on the inviting device
 * 
 * @author Rishabh Gulati
 * 
 */
public class DeviceInformation {

	private final String deviceId;
	private final String deviceName;
	
	public DeviceInformation(String deviceId, String deviceName) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
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
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		DeviceInformation other = (DeviceInformation) obj;
		if (deviceId == null) {
			if (other.deviceId != null){
				return false;
			}
		} else if (!deviceId.equals(other.deviceId)){
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DeviceInformation [deviceId=" + deviceId + ", deviceName="
				+ deviceName + "]";
	}
	
	
	
}
