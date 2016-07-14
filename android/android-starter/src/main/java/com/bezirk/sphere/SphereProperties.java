package com.bezirk.sphere;

import com.bezirk.sphere.api.DevMode;
import com.bezirk.sphere.api.SpherePrefs;
import com.bezirk.starter.BezirkPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
//TODO synchronise implementation with JavaPrefs api.
public class SphereProperties extends SpherePrefs {
    private static final Logger logger = LoggerFactory.getLogger(SphereProperties.class);
    /* Keys used in sphere.properties */
//    private static final String SPHERE_NAME = "sphereName";
//    private static final String SPHERE_ID = "sphereId";
//    private static final String SPHERE_KEY = "sphereKey";
//    private static final String SPHERE_MODE = "devMode";
//    private static final String DEFAULT_SPHERE_NAME = "defaultSphereName";
    BezirkPreferences preferences;
    private DevMode.Mode mode;
    /* Development sphere variables */
    private String sphereName;
    private String sphereId;
    private byte[] sphereKey;
    private String defaultSphereName;

    /**
     * @param preferences: should not be null
     */
    public SphereProperties(BezirkPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public void init() {
        if (validatePreferences()) {
            populateObjectMembers();
        }

    }

    @Override
    public DevMode.Mode getMode() {
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
        if (preferences.putString(DEFAULT_SPHERE_NAME_KEY, name)) {
            this.defaultSphereName = name;
            return true;
        }
        return false;
    }

    @Override
    public boolean setMode(DevMode.Mode mode) {
        if (!this.mode.equals(mode)) {
            String modeToSet = (mode.equals(DevMode.Mode.ON)) ? "true" : "false";
            if (preferences.putString(DEVELOPMENT_SPHERE_MODE_KEY, modeToSet)) {
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

        String[] spherePreferences = new String[]{DEVELOPMENT_SPHERE_NAME_KEY, DEVELOPMENT_SPHERE_ID_KEY, DEVELOPMENT_SPHEREKEY_KEY, DEVELOPMENT_SPHERE_MODE_KEY, DEFAULT_SPHERE_NAME_KEY};
        for (String spherePreference : spherePreferences) {

            if (!preferences.contains(spherePreference)) {
                return false;
            }

        }
        int int16 = 16;

        if (preferences.getString(DEVELOPMENT_SPHEREKEY_KEY, null).length() == int16) {
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
        sphereName = preferences.getString(DEVELOPMENT_SPHERE_NAME_KEY, null);
        sphereId = preferences.getString(DEVELOPMENT_SPHERE_ID_KEY, null);
        sphereKey = preferences.getString(DEVELOPMENT_SPHEREKEY_KEY, null).getBytes();
        mode = ("true".equalsIgnoreCase(preferences.getString(DEVELOPMENT_SPHERE_MODE_KEY, null))) ? DevMode.Mode.ON : DevMode.Mode.OFF;
        defaultSphereName = preferences.getString(DEFAULT_SPHERE_NAME_KEY, null);
        logger.info("sphere name: " + sphereName + " sphereId: " + sphereId + " sphereKey: " + Arrays.toString(sphereKey)
                + " mode: " + mode + " defaultSphereName: " + defaultSphereName);
    }


}
