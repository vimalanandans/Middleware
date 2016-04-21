package com.bezirk.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Uhu versions holds all the versions of uhu
 * will be used by all the components to
 * that uses the version to be compatible and process accordingly.
 * the main uhu version is read from build file
 */
public class UhuVersion {
    private transient static final Logger log = LoggerFactory.getLogger(UhuVersion.class);

    // UHU_VERSION - read from build property file
    public static String UHU_VERSION = "1.1";

    // Wire Message Version. increment it where there is a change in
    // wire message format (which would lead to crash while decoding)
    private static String WIRE_MESSAGE_VERSION = "0.1";

    // DB Version for storing database
    // Sphere / sadl regisry changes.
    private static String PERSISTENCE_VERSION = "0.1";


    static {
        loadUhuVersion();
    }

    static void loadUhuVersion() {
        try {
            Properties uhuProperties = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream is = loader.getResourceAsStream("version.properties");
            if (is == null) {
                log.error("Unable to find uhu version file. using default uhu version " + UHU_VERSION);
                return;
            }
            uhuProperties.load(is);
            UHU_VERSION = (String) uhuProperties.get("UHU_VERSION");
        } catch (Exception e) {
            log.error("Error in reading the Config file", e);
        }
    }

    /**
     * return the wire message version
     */
    static public String getWireVersion() {
        return WIRE_MESSAGE_VERSION;
    }

    /**
     * returns if the version is same
     */
    static public boolean isSameWireMessageVersion(String version) {
        return WIRE_MESSAGE_VERSION.equals(version);
    }

    /**
     * this method returns list of versions. Useful to display to user
     */
    static public Map<String, String> getAllVersion() {
        Map<String, String> versions = new HashMap<String, String>();

        versions.put(UHU_VERSION.toString(), UHU_VERSION);

        versions.put(WIRE_MESSAGE_VERSION.toString(), WIRE_MESSAGE_VERSION);

        //versions.put(PERSISTENCE_VERSION.toString(),PERSISTENCE_VERSION);

        return versions;
    }

}

