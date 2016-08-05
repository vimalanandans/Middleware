package com.bezirk.remotelogging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by PSG6KOR on 8/1/2016.
 */
public class RemoteLoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(RemoteLoggingConfig.class);
    private final static String LOGGING_ENV_VARIABLE = "loggingEnabled"; // variable set in gradle/environment to enable/disable bezirk logs
    private final static boolean LOGGING_DEFAULT_VALUE = false;
    public static final String ALL_SPHERES = "ALL-SPHERES";

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
