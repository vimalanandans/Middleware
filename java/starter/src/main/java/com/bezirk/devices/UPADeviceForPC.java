package com.bezirk.devices;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;
import com.bezrik.network.UhuNetworkUtilities;

public class UPADeviceForPC implements com.bezirk.devices.UPADeviceInterface {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UPADeviceForPC.class);
    public static final String PROPS = "upadevice.properties";

    private final com.bezirk.devices.DeviceDetails deviceDetails;

    /**
        * The constructor is used for setting up the device information like deviceId and deviceName which can be used other modules like sphere
        */
    public UPADeviceForPC() {
        
        deviceDetails = new DeviceDetails();

        try {
            final Properties props = UPADeviceForPC.loadProperties();
            final String location = props.getProperty("DeviceLocation");
            String deviceName;

            deviceName = fetchDeviceName();

            deviceDetails.setDeviceId(Hex.encodeToString(UhuNetworkUtilities
                    .getLocalMACAddress()));
            deviceDetails.setDeviceName(deviceName);
            deviceDetails.setDeviceLocation(new Location(location));

        } catch (Exception e) {
            LOGGER.error("Failure to load upadevice.properties file",e);
        }

    }

    private String fetchDeviceName() {
        String deviceName;
        try {
            deviceName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            deviceName = "UHU-PC";
            LOGGER.error("Exception in fetching hostname.",e);
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
     * Used for setting the device location
     *
     * @param loc The location of the device to be set
     * @return true: if the location was set successfully
     * false: otherwise
     *
     * Note : the device location is currently not persisted into the shared preferences
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
     * If the service location is not set it uses the device location
     * @return the Device Location
     *
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

    /**
     * Load UPADevice properties from one place.  All
     * @return A Properties object loaded with properties from file
     * @throws Exception if there is a problem loading properties from the classpath
     */
    public static Properties loadProperties() throws Exception {
        return loadProperties(PROPS);
    }

    public static Properties loadProperties(String file) throws Exception {

        // Load properties file from the classpath (this avoids hard-coding the
        // filesystem path)
        final InputStream propsInputStream = com.bezirk.devices.UPADeviceInterface.class
                .getClassLoader().getResourceAsStream(file);

        if (propsInputStream == null) {
            throw new Exception(
                    "Problem loading properties file. Input stream is null for file: "
                            + file);
        }
        
        final Properties props = new Properties();

        props.load(propsInputStream);
        if (props.isEmpty()) {
            throw new Exception("Properties loaded from file are empty: "
                    + file);
        }

        return props;
    }

    public static void storeProperties(Properties props) throws IOException {
        final URL propsURL = com.bezirk.devices.UPADeviceInterface.class.getResource(PROPS);
        final FileOutputStream fos = new FileOutputStream(propsURL.getFile());
        props.store(fos, null);
    }

	public static void storeProperties(Properties props, URL url) throws IOException {
	    FileOutputStream fos = new FileOutputStream(url.getFile());
        props.store(fos, null);
        fos.close();
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