/**
 * 
 */
package com.bosch.upa.uhu.sphere.impl;

import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceForPC;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuDevMode.Mode;

/**
 * @author rishabh
 *
 */
public class SphereProperties implements ISphereConfig {

    private Mode mode;

    /* Development sphere variables */
    private String sphereName;
    private String sphereId;
    private byte[] sphereKey;

    private String defaultSphereName;
    private static final String SPHERE_PROPERTIES_FILE = "sphere.properties";

    /* Keys used in sphere.properties */
    private static final String SPHERE_NAME = "sphereName";
    private static final String SPHERE_ID = "sphereId";
    private static final String SPHERE_KEY = "sphereKey";
    private static final String SPHERE_MODE = "devMode";
    private static final String DEFAULT_SPHERE_NAME = "defaultSphereName";

    private static final Logger log = LoggerFactory.getLogger(SphereProperties.class);

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
        Properties sphereProperties = null;
        try {
            sphereProperties = UPADeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
            sphereProperties.setProperty(DEFAULT_SPHERE_NAME, name);
            URL propsURL = SphereProperties.class.getClassLoader().getResource(SPHERE_PROPERTIES_FILE);
            UPADeviceForPC.storeProperties(sphereProperties, propsURL);
            // once properties are stored successfully, update the
            // defaultSphereName
            this.defaultSphereName = name;
            return true;
        } catch (Exception e) {
            log.error("setDefaultSphereName", e);
        }
        return false;
    }

    @Override
    public boolean setMode(Mode mode) {
        // set mode if different from current mode
        if (!this.mode.equals(mode)) {
            Properties sphereProperties = null;
            try {
                sphereProperties = UPADeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
                String modeToSet = (mode.equals(Mode.ON)) ? "true" : "false";
                sphereProperties.setProperty(SPHERE_MODE, modeToSet);
                URL propsURL = SphereProperties.class.getClassLoader().getResource(SPHERE_PROPERTIES_FILE);
                UPADeviceForPC.storeProperties(sphereProperties, propsURL);
                // once properties are stored successfully, update the
                // mode value
                this.mode = mode;
                return true;
            } catch (Exception e) {
                log.error("setDefaultSphereName", e);
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
            sphereProperties = UPADeviceForPC.loadProperties(SPHERE_PROPERTIES_FILE);
            log.info(sphereProperties.toString());
        } catch (Exception e) {
            log.error("getProperties", e);
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
            log.info("Sphere properties validated");
            return true;
        }
        return false;
    }

    /**
     * Populate members. Requires validation of properties using
     * {@link #validateProperties(Properties)}
     * 
     * @param properties
     * @return
     */
    private void populateObjectMembers(Properties properties) {
        sphereName = properties.getProperty(SPHERE_NAME);
        sphereId = properties.getProperty(SPHERE_ID);
        sphereKey = properties.getProperty(SPHERE_KEY).getBytes();
        System.out.println("size of byte array: " + sphereKey.length);
        mode = (properties.getProperty(SPHERE_MODE).equalsIgnoreCase("true")) ? Mode.ON : Mode.OFF;
        defaultSphereName = properties.getProperty(DEFAULT_SPHERE_NAME);
        log.info("Sphere name: " + sphereName + " sphereId: " + sphereId + " sphereKey: " + Arrays.toString(sphereKey)
                + " mode: " + mode + " defaultSphereName: " + defaultSphereName);
    }

}
