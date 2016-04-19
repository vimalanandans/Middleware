/**
 * 
 */
package com.bezirk.devices;

import com.bezirk.middleware.addressing.Location;

/**
 * @author GUR1PI
 *
 */
public interface UPADeviceInterface {

	/**
	 * Used for changing the device name
	 * @param deviceName
	 * 			The new device name to be configured
	 * 
	 * @return true : if the name was changed successfully
	 * 		   false: otherwise
	 */
	public boolean setDeviceName(String deviceName);
	
	
	/**
	 * Provides the name of the device	 
	 * 		
	 * @return device name of the device
	 * 		   null if the device name is not configured
	 */
	public String getDeviceName();
	
	
	/**
	 * Used for setting the device location 
	 * @param loc
	 * 			The location of the device to be set
	 * @return true: if the location was set successfully
	 * 		   false: otherwise	
	 */
	public boolean setDeviceLocation(Location loc);
	
	/**
	 * Provides the location of the device
	 * 
	 * @return location of the device
	 * 		   null if the device location is not configured
	 */
	public Location getDeviceLocation();
	
	/**
	 * 
	 * @param deviceType
	 * @return
	 */
	public boolean setDeviceType(String deviceType);
	
	/**
	 * 
	 * @return
	 */
	public String getDeviceType();
	
	
	/**
	 * Provides the unique ID of the device 
	 * 
	 * @return device id of the device
	 * 		   null if the device id is not configured	
	 */
	public String getDeviceId();
}
