package com.bezirk.sphere.impl;

import com.bezirk.sphere.api.BezirkDevMode;
import com.bezirk.sphere.api.SpherePrefs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author Rishabh Gulati
 */
public class JavaPrefs extends SpherePrefs {
    private static final Logger logger = LoggerFactory.getLogger(JavaPrefs.class);
    private Preferences preferences;

    public static void main(String[] args) {
        JavaPrefs javaPrefs = new JavaPrefs();
        javaPrefs.init();
        System.out.println("Set " + SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY + " --> " + javaPrefs.setMode(BezirkDevMode.Mode.ON));
        System.out.println("Set " + SpherePrefs.DEFAULT_SPHERE_NAME_KEY + " --> " + javaPrefs.setDefaultSphereName("Test name"));
        System.out.println("Get " + SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY + " --> " + javaPrefs.getMode());
        System.out.println("Get " + SpherePrefs.DEVELOPMENT_SPHERE_ID_KEY + " --> " + javaPrefs.getSphereId());
        System.out.println("Get " + SpherePrefs.DEVELOPMENT_SPHERE_NAME_KEY + " --> " + javaPrefs.getSphereName());
        try {
            System.out.println("Get " + SpherePrefs.DEVELOPMENT_SPHEREKEY_KEY + " --> " + new String(javaPrefs.getSphereKey(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {

        }

    }

    @Override
    public void init() {
        try {
            preferences = Preferences.userNodeForPackage(JavaPrefs.class);
            logger.debug("Sphere preferences initialized successfully");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public BezirkDevMode.Mode getMode() {
        return preferences.getBoolean(SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY, false) ? BezirkDevMode.Mode.ON : BezirkDevMode.Mode.OFF;
    }

    @Override
    public String getSphereName() {
        return preferences.get(SpherePrefs.DEVELOPMENT_SPHERE_NAME_KEY, SpherePrefs.DEVELOPMENT_SPHERE_NAME_DEFAULT_VALUE);
    }

    @Override
    public String getSphereId() {
        return preferences.get(SpherePrefs.DEVELOPMENT_SPHERE_ID_KEY, SpherePrefs.DEVELOPMENT_SPHERE_ID_DEFAULT_VALUE);
    }

    @Override
    public byte[] getSphereKey() {
        return preferences.get(SpherePrefs.DEVELOPMENT_SPHEREKEY_KEY, SpherePrefs.DEVELOPMENT_SPHEREKEY_DEFAULT_VALUE).getBytes();
    }

    @Override
    public String getDefaultSphereName() {
        return preferences.get(SpherePrefs.DEFAULT_SPHERE_NAME_KEY, SpherePrefs.DEFAULT_SPHERE_NAME_DEFAULT_VALUE);
    }

    @Override
    public boolean setDefaultSphereName(String name) {
        try {
            preferences.put(SpherePrefs.DEFAULT_SPHERE_NAME_KEY, name);
            preferences.sync();
            logger.debug("Setting " + SpherePrefs.DEFAULT_SPHERE_NAME_KEY + " --> " + name);
            return true;
        } catch (BackingStoreException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setMode(BezirkDevMode.Mode mode) {
        if (mode == BezirkDevMode.Mode.ON) {
            try {
                preferences.putBoolean(SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY, true);
                preferences.sync();
                logger.debug("Setting " + SpherePrefs.DEVELOPMENT_SPHERE_MODE_KEY + " --> " + true);
                return true;
            } catch (BackingStoreException e) {
                logger.error(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
