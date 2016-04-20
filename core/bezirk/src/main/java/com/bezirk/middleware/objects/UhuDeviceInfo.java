/**
 * 
 */
package com.bezirk.middleware.objects;

import java.util.List;

/**
 * @author Rishabh Gulati
 * 
 */
public class UhuDeviceInfo {

	private final String deviceId;
	private final String deviceName;
	private final String deviceType;
	private final UhuDeviceRole deviceRole;
	private final boolean deviceActive;
	private final List<UhuServiceInfo> services;
	
	public enum UhuDeviceRole {
		UHU_MEMBER, // this device has control role for this sphere
		UHU_CONTROL // this device has member role for this sphere
	}

	
	/** // We need Service Info not service id. this services will be deprecated
	 * @param deviceId
	 * @param deviceName
	 * @param deviceType
	 * @param deviceRole
	 * @param deviceActive
	 * @param services
	 */
	 // We need Service Info not service id. this services will be deprecated
	public UhuDeviceInfo(final String deviceId, final String deviceName,
			final String deviceType, final UhuDeviceRole deviceRole,
			final boolean deviceActive, final List<UhuServiceInfo> services) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.deviceType = deviceType;
		this.deviceRole = deviceRole;
		this.deviceActive = deviceActive;
		this.services = services;		 
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

	/**
	 * @return the deviceType
	 */
	public final String getDeviceType() {
		return deviceType;
	}

	/**
	 * @return the deviceRole
	 */
	public final UhuDeviceRole getDeviceRole() {
		return deviceRole;
	}

	/**
	 * @return the deviceActive
	 */
	public final boolean isDeviceActive() {
		return deviceActive;
	}

		
	/**
	 * @return the service info list
	 */
	public final List<UhuServiceInfo> getServiceList() {
		return services;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UhuDeviceInfo [deviceId=" + deviceId + ",\ndeviceName="
				+ deviceName + ",\ndeviceType=" + deviceType + ",\ndeviceRole="
				+ deviceRole + ",\ndeviceActive=" + deviceActive + ",\nservices="
				+ services + "]";
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UhuDeviceInfo other = (UhuDeviceInfo) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		return true;
	}
	
	
	
}
