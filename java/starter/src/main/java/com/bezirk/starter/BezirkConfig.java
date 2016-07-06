package com.bezirk.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Maintain configurations like QR display, Logging, data-path for storing bezirk database, for pure-java/PC version of Bezirk
 */
public class BezirkConfig {

    private static final Logger logger = LoggerFactory.getLogger(BezirkConfig.class);
    private static final String DATA = "data"; // name of data folder
    private static final String BEZIRK = "bezirk"; // name of folder for holding bezirk related data/database(s)
    private static final String DATA_PATH_REL = DATA + File.separator + BEZIRK; // relative data path for bezirk folder

    private final static String DISPLAY_ENV_VARIABLE = "displayEnabled"; // variable set in gradle/environment to enable/disable sphere-mgmt display
    private final static boolean DISPLAY_DEFAULT_VALUE = false; // default value of bezirk sphere-mgmt display

    private final static String LOGGING_ENV_VARIABLE = "loggingEnabled"; // variable set in gradle/environment to enable/disable bezirk logs
    private final static boolean LOGGING_DEFAULT_VALUE = false; // default value of bezirk logging

    private String dataPath; // holds the absolute path of the bezirk folder

    public BezirkConfig() {
        init();
    }

    private void init() {
        // gradle sets this when invoked
        String appHome = System.getenv().get("APP_HOME");
        if (appHome == null || appHome.isEmpty()) {
            appHome = System.getProperty("user.dir");
        }

        // set data path
        dataPath = appHome + File.separator + DATA_PATH_REL;

        // create the data directory if it doesn't exist
        final File dataDir = new File(dataPath);
        if (dataDir.exists()) {
            logger.debug("Using existing data directory --> " + dataDir);
        } else {
            logger.debug("Creating data directory --> " + dataDir);
            if (!dataDir.mkdirs()) {
                logger.error("Failed to create data directory");
            }
        }

        // change bezirk logging level if enabled
        if (isLoggingEnabled()) {
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
            root.setLevel(ch.qos.logback.classic.Level.INFO);
        }
    }

    /**
     * Get absolute data path of the bezirk folder
     *
     * @return absolute path of the bezirk folder
     */
    public String getDataPath() {
        return dataPath;
    }

    /**
     * Check if display is enabled
     *
     * @return true if display is enabled by setting JVM property(displayEnabled=true) or System Environment variable(displayEnabled=true) or if default configuration{@link #DISPLAY_DEFAULT_VALUE} in Bezirk is set to true<br>
     * false otherwise
     */
    public boolean isDisplayEnabled() {
        boolean isDisplayEnabled = isEnabledInJVM(DISPLAY_ENV_VARIABLE) || isEnabledInSystem(DISPLAY_ENV_VARIABLE) || DISPLAY_DEFAULT_VALUE;
        logger.debug("Display enabled --> " + isDisplayEnabled);
        return isDisplayEnabled;
    }

    public boolean isLoggingEnabled() {
        boolean isLoggingEnabled = isEnabledInJVM(LOGGING_ENV_VARIABLE) || isEnabledInSystem(LOGGING_ENV_VARIABLE) || LOGGING_DEFAULT_VALUE;
        logger.debug("Logging enabled --> " + isLoggingEnabled);
        return isLoggingEnabled;
    }

    /**
     * Check if a property is enabled to true in JVM.
     *
     * @param propKey property set in the JVM
     * @return true if the property is set to true <br> false otherwise
     */
    private boolean isEnabledInJVM(String propKey) {
        String propValue = null;
        boolean boolPropValue = false;
        try {
            propValue = System.getProperty(propKey);
        } catch (SecurityException e) {
            logger.error("Unable to access jvm variable " + propKey + " " + e.getMessage());
        }
        if (null != propValue) {
            boolPropValue = Boolean.parseBoolean(propValue);
            logger.debug("JVM property " + propKey + " --> " + boolPropValue);
        }
        return boolPropValue;
    }

    /**
     * Check if a property is enabled to true as system variable.
     *
     * @param propKey property set as the system environment variable
     * @return true if the property is set to true <br> false otherwise
     */
    private boolean isEnabledInSystem(String propKey) {
        String propValue = null;
        boolean boolPropValue = false;
        try {
            propValue = System.getenv(propKey);
        } catch (SecurityException e) {
            logger.error("Unable to access system variable " + propKey + " " + e.getMessage());
        }
        if (null != propValue) {
            boolPropValue = Boolean.parseBoolean(propValue);
            logger.debug("System property " + propKey + " --> " + boolPropValue);
        }
        return boolPropValue;
    }
}