package com.bezirk.devices;

import com.bezirk.middleware.addressing.Location;
import com.bezrik.network.BezirkNetworkUtilities;

import org.apache.shiro.codec.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

public class BezirkDeviceForPC implements BezirkDeviceInterface {
    public static final String PROPS = "upadevice.properties";
    private static final Logger logger = LoggerFactory.getLogger(BezirkDeviceForPC.class);
    private final com.bezirk.devices.DeviceDetails deviceDetails;

    /**
     * The constructor is used for setting up the device information like deviceId and deviceName which can be used other modules like sphere
     */
    public BezirkDeviceForPC() {

        deviceDetails = new DeviceDetails();

        try {
            //final Properties props = BezirkDeviceForPC.loadProperties();
            //final String location = props.getProperty("DeviceLocation");
            final String location = "Floor1/null/null";
            String deviceName;

            deviceName = fetchDeviceName();

            deviceDetails.setDeviceId(Hex.encodeToString(BezirkNetworkUtilities
                    .getLocalMACAddress()));
            deviceDetails.setDeviceName(deviceName);
            deviceDetails.setDeviceLocation(new Location(location));

        } catch (Exception e) {
            logger.error("Failure to load upadevice.properties file", e);
        }

    }

    /**
     * Load UPADevice properties from one place.  All
     *
     * @return A Properties object loaded with properties from file
     * @throws Exception if there is a problem loading properties from the classpath
     */
    public static Properties loadProperties() throws Exception {
        return loadProperties(PROPS);
    }

    public static Properties loadProperties(String file) throws Exception {

        // Load properties file from the classpath (this avoids hard-coding the
        // filesystem path)
        final InputStream propsInputStream = BezirkDeviceInterface.class
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
        final URL propsURL = BezirkDeviceInterface.class.getResource(PROPS);
        final FileOutputStream fos = new FileOutputStream(propsURL.getFile());
        props.store(fos, null);
    }

    public static void storeProperties(Properties props, URL url) throws IOException {
        FileOutputStream fos = new FileOutputStream(url.getFile());
        props.store(fos, null);
        fos.close();
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
