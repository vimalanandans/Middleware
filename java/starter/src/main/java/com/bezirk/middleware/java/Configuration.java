package com.bezirk.middleware.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final String LOGGING_ENV_VARIABLE = "loggingEnabled"; // variable set in gradle/environment to enable/disable bezirk logs
    private static final String REMOTE_LOGGING_ENV_VARIABLE = "remoteLogging"; // variable set in gradle/environment to enable/disable bezirk logs
    private static final boolean REMOTE_LOGGING_ENV_VALUE = false; // variable set in gradle/environment to enable/disable bezirk logs
    private static final boolean LOGGING_DEFAULT_VALUE = false; // default value of bezirk logging

    public static boolean isLoggingEnabled() {
        final boolean isLoggingEnabled = isEnabledInJVM(LOGGING_ENV_VARIABLE) || isEnabledInSystem(LOGGING_ENV_VARIABLE) || LOGGING_DEFAULT_VALUE;
        logger.debug("Logging enabled --> {}", isLoggingEnabled);
        return isLoggingEnabled;
    }

    public static boolean isRemoteLoggingEnabled() {
        final boolean isLoggingEnabled = isEnabledInJVM(REMOTE_LOGGING_ENV_VARIABLE) || isEnabledInSystem(REMOTE_LOGGING_ENV_VARIABLE) || REMOTE_LOGGING_ENV_VALUE;
        logger.debug("Remote Logging status --> {}", isLoggingEnabled);
        return isLoggingEnabled;
    }

    /**
     * Returns value of the propKey if set as a System Environment variable or passed as an JVM argument. If {@code propKey} is set as a JVM argument the method returns the value else it checks for the {@code propKey} in system variables.
     *
     * @param propKey
     * @return {@code String} value of the {@code propKey} passed if set in the JVM or System variable with key as {@code propKey}
     */
    public static String getPropertyValue(String propKey) {
        String propValue = getPropertyValueSetInJVM(propKey);
        if (propValue == null) {
            propValue = getPropertyValueSetInSystem(propKey);
        }
        return propValue;
    }

    private static String getPropertyValueSetInSystem(String propKey) {
        String propValue = null;
        try {
            propValue = System.getenv(propKey);
        } catch (SecurityException e) {
            logger.warn("Unable to access system variable " + propKey, e);
        }
        return propValue;
    }

    private static String getPropertyValueSetInJVM(String propKey) {
        String propValue = null;
        try {
            propValue = System.getProperty(propKey);
        } catch (SecurityException e) {
            logger.warn("Unable to access jvm variable " + propKey, e);
        }
        return propValue;
    }

    /**
     * Check if a property is enabled to true in JVM.
     *
     * @param propKey property set in the JVM
     * @return true if the property is set to true <br> false otherwise
     */
    private static boolean isEnabledInJVM(String propKey) {
        String propValue = null;
        boolean boolPropValue = false;
        try {
            propValue = System.getProperty(propKey);
        } catch (SecurityException e) {
            logger.warn("Unable to access jvm variable " + propKey, e);
        }
        if (null != propValue) {
            boolPropValue = Boolean.parseBoolean(propValue);
            logger.debug("JVM property {} --> {}", propKey, boolPropValue);
        }
        return boolPropValue;
    }

    /**
     * Check if a property is enabled to true as system variable.
     *
     * @param propKey property set as the system environment variable
     * @return true if the property is set to true <br> false otherwise
     */
    private static boolean isEnabledInSystem(String propKey) {
        String propValue = null;
        boolean boolPropValue = false;
        try {
            propValue = System.getenv(propKey);
        } catch (SecurityException e) {
            logger.warn("Unable to access system variable " + propKey, e);
        }
        if (null != propValue) {
            boolPropValue = Boolean.parseBoolean(propValue);
            logger.debug("System property {} --> {}", propKey, boolPropValue);
        }
        return boolPropValue;
    }
}
