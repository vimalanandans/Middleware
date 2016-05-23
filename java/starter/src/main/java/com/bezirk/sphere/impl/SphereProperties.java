/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.devices.BezirkDeviceForPC;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkDevMode.Mode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

/**
 * @author rishabh
 * @deprecated As of release 3.0.0-alpha+003, replaced be {@link JavaPrefs}
 */
@Deprecated
public class SphereProperties implements ISphereConfig {
    private static final Logger logger = LoggerFactory.getLogger(SphereProperties.class);

    private static final String SPHERE_PROPERTIES_FILE = "sphere.properties";
    /* Keys used in sphere.properties */
    private static final String SPHERE_NAME = "sphereName";
    private static final String SPHERE_ID = "sphereId";
    private static final String SPHERE_KEY = "sphereKey";
    private static final String SPHERE_MODE = "devMode";
    private static final String DEFAULT_SPHERE_NAME = "defaultSphereName";

    private Mode mode;
    /* Development sphere variables */
    private String sphereName;
    private String sphereId;
    private byte[] sphereKey;
    private String defaultSphereName;

    @Override
    public void init() {
        Properties properties = getProperties();
        if (validateProperties(properties)) {
            populateObjectMembers(properties);
        }
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public String getSphereName() {
        return sphereName;
    }

    @Override
    public String getSphereId() {
        return sphereId;
    }

    @Override
    public byte[] getSphereKey() {
        return sphereKey.clone();
    }

    @Override
    public String getDefaultSphereName() {
        return defaultSphereName;
    }

    @Override
    public boolean setDefaultSphereName(String name) {
        try {
            Properties sphereProperties = BezirkDeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
            sphereProperties.setProperty(DEFAULT_SPHERE_NAME, name);
            URL propsURL = SphereProperties.class.getClassLoader().getResource(SPHERE_PROPERTIES_FILE);
            BezirkDeviceForPC.storeProperties(sphereProperties, propsURL);
            // once properties are stored successfully, update the
            // defaultSphereName
            this.defaultSphereName = name;
            return true;
        } catch (Exception e) {
            logger.error("setDefaultSphereName", e);
        }
        return false;
    }

    @Override
    public boolean setMode(Mode mode) {
        // set mode if different from current mode
        if (!this.mode.equals(mode)) {
            try {
                Properties sphereProperties = BezirkDeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
                String modeToSet = (mode.equals(Mode.ON)) ? "true" : "false";
                sphereProperties.setProperty(SPHERE_MODE, modeToSet);
                URL propsURL = SphereProperties.class.getClassLoader().getResource(SPHERE_PROPERTIES_FILE);
                BezirkDeviceForPC.storeProperties(sphereProperties, propsURL);
                // once properties are stored successfully, update the
                // mode value
                this.mode = mode;
                return true;
            } catch (Exception e) {
                logger.error("setDefaultSphereName", e);
            }
        }
        return false;
    }

    /**
     * Get sphere properties from the sphere.properties file.
     *
     * @return
     */
    private Properties getProperties() {
        Properties sphereProperties = null;
        try {
            sphereProperties = BezirkDeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
            logger.info(sphereProperties.toString());
        } catch (Exception e) {
            logger.error("getProperties", e);
        }
        return sphereProperties;
    }

    /**
     * Validate sphere properties from the properties file
     *
     * @param properties
     * @return
     */
    private boolean validateProperties(Properties properties) {
        if (properties.containsKey(SPHERE_NAME) && properties.containsKey(SPHERE_ID)
                && properties.containsKey(SPHERE_KEY) && properties.getProperty(SPHERE_KEY).length() == 24
                && properties.containsKey(SPHERE_MODE) && properties.containsKey(DEFAULT_SPHERE_NAME)) {
            logger.info("sphere properties validated");
            return true;
        }
        return false;
    }

    /**
     * Populate members. Requires validation of properties using
     * {@link #validateProperties(Properties)}
     *
     * @param properties
     */
    private void populateObjectMembers(Properties properties) {
        sphereName = properties.getProperty(SPHERE_NAME);
        sphereId = properties.getProperty(SPHERE_ID);
        sphereKey = properties.getProperty(SPHERE_KEY).getBytes();
        System.out.println("size of byte array: " + sphereKey.length);
        mode = (properties.getProperty(SPHERE_MODE).equalsIgnoreCase("true")) ? Mode.ON : Mode.OFF;
        defaultSphereName = properties.getProperty(DEFAULT_SPHERE_NAME);
        logger.info("sphere name: " + sphereName + " sphereId: " + sphereId + " sphereKey: " + Arrays.toString(sphereKey)
                + " mode: " + mode + " defaultSphereName: " + defaultSphereName);
    }

}
