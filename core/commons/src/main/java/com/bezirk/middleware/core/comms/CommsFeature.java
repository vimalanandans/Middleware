package com.bezirk.middleware.core.comms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public enum CommsFeature {
    COMMS_SECURE,

    COMMS_BEZIRK,

    COMMS_ZYRE,

    // zyre-jni is platform specific (as of now). hence this needs to handled in java-build or android build
    // comms-jni will be platform specific
    COMMS_ZYRE_JNI,

    // comms jyre implementation
    COMMS_JYRE,

    WIRE_MSG_COMPRESSION,

    WIRE_MSG_ENCRYPTION,

    HTTP_BEZIRK_COMMS;

    private static final Logger logger = LoggerFactory.getLogger(CommsFeature.class);
    private static Properties properties;
    private String value;

    private void init() {
        if (properties == null) {
            properties = new Properties();
            try {
                //properties.load(Constants.class.getResourceAsStream(PATH));
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                properties.load(loader.getResourceAsStream("features.properties"));
            } catch (Exception e) {
                logger.error("Unable to load features.properties file from classpath.", e);
            }
        }
        value = (String) properties.get(this.toString());
    }

    public String getValue() {
        if (value == null) {
            init();
        }
        return value;
    }

    /**
     * assuming everything boolean.
     */
    public boolean isActive() {
        if (value == null) {
            init();
        }

        return value != null && value.equalsIgnoreCase("true");
    }
}