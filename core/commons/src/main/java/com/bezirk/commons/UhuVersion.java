package com.bezirk.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private transient static final Logger logger = LoggerFactory.getLogger(UhuVersion.class);

    private static final String DEFAULT_BEZIRK_VERSION = "1.1";

    // UHU_VERSION - read from build property file
    public static final String UHU_VERSION;

    // Wire Message Version. increment it where there is a change in
    // wire message format (which would lead to crash while decoding)
    private static final String WIRE_MESSAGE_VERSION = "0.1";

    // DB Version for storing database
    // Sphere / sadl regisry changes.
    private static final String PERSISTENCE_VERSION = "0.1";

    static {
        // Fetch the version of this Bezirk middleware instance from version.properties
        final Properties uhuProperties = new Properties();
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();

        String middlewareVersion = "";

        try (final InputStream is = loader.getResourceAsStream("version.properties")) {
            if (is == null) {
                logger.error("Unable to find uhu version file. using default uhu version " +
                        DEFAULT_BEZIRK_VERSION);
            } else {
                uhuProperties.load(is);
                middlewareVersion = (String) uhuProperties.get("UHU_VERSION");
            }
        } catch (NullPointerException e) {
            logger.error("Error fetching resource stream for version.properties", e);
        } catch (IOException e) {
            logger.error("Error reading version.properties", e);
        }

        UHU_VERSION = middlewareVersion.isEmpty() ? DEFAULT_BEZIRK_VERSION : middlewareVersion;
    }

    /**
     * @return the wire message version
     */
    static public String getWireVersion() {
        return WIRE_MESSAGE_VERSION;
    }

    /**
     * @return if the version is same
     */
    static public boolean isSameWireMessageVersion(String version) {
        return WIRE_MESSAGE_VERSION.equals(version);
    }

    /**
     * this method returns list of versions. Useful to display to user
     */
    static public Map<String, String> getAllVersion() {
        Map<String, String> versions = new HashMap<String, String>();

        versions.put("UHU_VERSION", UHU_VERSION);

        versions.put("WIRE_MESSAGE_VERSION", WIRE_MESSAGE_VERSION);

        //versions.put(PERSISTENCE_VERSION.toString(),PERSISTENCE_VERSION);

        return versions;
    }

}

