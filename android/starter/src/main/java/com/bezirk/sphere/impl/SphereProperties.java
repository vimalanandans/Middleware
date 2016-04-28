package com.bezirk.sphere.impl;

import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.starter.UhuPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SphereProperties implements ISphereConfig {
    private static final Logger logger = LoggerFactory.getLogger(SphereProperties.class);

    /* Keys used in sphere.properties */
    private static final String SPHERE_NAME = "sphereName";
    private static final String SPHERE_ID = "sphereId";
    private static final String SPHERE_KEY = "sphereKey";
    private static final String SPHERE_MODE = "devMode";
    private static final String DEFAULT_SPHERE_NAME = "defaultSphereName";
    UhuPreferences preferences;
    private IUhuDevMode.Mode mode;
    /* Development sphere variables */
    private String sphereName;
    private String sphereId;
    private byte[] sphereKey;
    private String defaultSphereName;

    /**
     * @param preferences: should not be null
     */
    public SphereProperties(UhuPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void init() {
        if (validatePreferences()) {
            populateObjectMembers();
        }

    }

    @Override
    public IUhuDevMode.Mode getMode() {
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
        if (preferences.putString(DEFAULT_SPHERE_NAME, name)) {
            this.defaultSphereName = name;
            return true;
        }
        return false;
    }

    @Override
    public boolean setMode(IUhuDevMode.Mode mode) {
        if (!this.mode.equals(mode)) {
            String modeToSet = (mode.equals(IUhuDevMode.Mode.ON)) ? "true" : "false";
            if (preferences.putString(SPHERE_MODE, modeToSet)) {
                this.mode = mode;
                return true;
            }
        }
        return false;
    }

    /**
     * Validate sphere preferences stored in SharedPreferences
     *
     * @return
     */
    private boolean validatePreferences() {

        String[] spherePreferences = new String[]{SPHERE_NAME, SPHERE_ID, SPHERE_KEY, SPHERE_MODE, DEFAULT_SPHERE_NAME};
        for (String spherePreference : spherePreferences) {

            if (!preferences.contains(spherePreference)) {
                return false;
            }

        }
        int int24 = 24;

        if (preferences.getString(SPHERE_KEY, null).length() == int24) {
            logger.info("sphere preferences validated");
            return true;
        }
        return false;
    }

    /**
     * Populate members. Requires validation of preferences using
     * {@link #validatePreferences()}
     *
     * @return
     */
    private void populateObjectMembers() {
        sphereName = preferences.getString(SPHERE_NAME, null);
        sphereId = preferences.getString(SPHERE_ID, null);
        sphereKey = preferences.getString(SPHERE_KEY, null).getBytes();
        mode = ("true".equalsIgnoreCase(preferences.getString(SPHERE_MODE, null))) ? IUhuDevMode.Mode.ON : IUhuDevMode.Mode.OFF;
        defaultSphereName = preferences.getString(DEFAULT_SPHERE_NAME, null);
        logger.info("sphere name: " + sphereName + " sphereId: " + sphereId + " sphereKey: " + Arrays.toString(sphereKey)
                + " mode: " + mode + " defaultSphereName: " + defaultSphereName);
    }


}
