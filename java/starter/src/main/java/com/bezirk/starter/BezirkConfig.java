package com.bezirk.starter;

import com.sun.org.apache.xpath.internal.operations.Bool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Locale;
import java.util.ServiceConfigurationError;

/**
 * Hold the Bezirk Config
 * <p>
 * At the moment it holds the data about Bezirk PC TODO: Move this file to
 * java-common so that all the configurations are inside this module.
 * </p>
 */
public class BezirkConfig {

    /**
     * Relative data path
     */
    public static final String DATA_PATH_REL = "data" + File.separator + "bezirk";
    private static final Logger logger = LoggerFactory.getLogger(BezirkConfig.class);
    /**
     * This will hold the full data path, which will be relative to APP_HOME or
     */
    private String dataPath;
    private final static String DISPLAY_ENV_VARIABLE = "displayEnabled";
    private final static boolean DISPLAY_DEFAULT_VALUE = false; //bezirk qr code display will not be shown by default.

    /**
     * display enable or disable
     */
    //private String displayEnable = "true";
    public BezirkConfig() {
        // load the resources
        initDataPath();
    }

    /**
     * Set the application home directory. If we are running from the binary
     * distribution, appHome is set to: /path/to/bezirk-main-VERS. If we are
     * running from the development environment, appHome is set to the project
     * root directory.
     */
    private void initDataPath() {
        // The launcher script sets this system variable
        String appHome = System.getenv().get("APP_HOME");
        if (appHome == null || appHome.isEmpty()) {
            //appHome = "."; // current working dir means zirk root
            // take from temp or user folder because . doesn't work in unix
            //appHome =  System.getProperty("java.io.tmpdir");
            appHome = System.getProperty("user.dir");

        }

        // set the datapath relative to APP_HOME or "." if APP_HOME not set
        dataPath = appHome + File.separator + DATA_PATH_REL;

        // create the data dir if it doesn't exist
        final File dataDir = new File(dataPath);
        if (dataDir.exists()) {

            logger.info("Using existing dataDir:" + dataDir);

        } else {

            logger.info("Creating dataDir: {}", dataDir);
            if (!dataDir.mkdirs()) {
                logger.error("Failed to create dataDir");
            }
        }
    }

    public String getDataPath() {

        return dataPath;

    }

    public void setDataPath(final String path) {
        dataPath = path;
    }

    /**
     * @return
     * @deprecated Use {@link #isDisplayEnabled()} instead
     */
    @Deprecated
    public String getDisplayEnable() {
        return "false";
    }

    @Deprecated
    public void setDisplayEnable(final String enable) {
        logger.debug("setDisplayEnable -> " + enable);
        //displayEnable = enable;
    }

    public boolean isDisplayEnabled() {
        return getDisplayEnabledSystemValue();
    }

    /**
     * The display enabled uses the following values in order(highest -> lowest priority) to determine if the display should be enabled
     * <ul>
     * <li>Passed JVM property, ex. through gradle, gradlew -DdisplayEnabled=true run</li>
     * <li>System Environment Variable, displayEnabled=true</li>
     * <li>Default value set in Bezirk, i.e {@link #DISPLAY_DEFAULT_VALUE}</li>
     * </ul>
     *
     * @return
     */
    private boolean getDisplayEnabledSystemValue() {
        boolean isDisplayEnabled = isEnabledInJVM() || isEnabledInSystem() || DISPLAY_DEFAULT_VALUE;
        logger.debug("Display enabled --> " + isDisplayEnabled);
        return isDisplayEnabled;
    }

    private boolean isEnabledInJVM() {
        String bezirkDisplay = null;
        boolean boolBezirkDisplay = false;
        try {
            bezirkDisplay = System.getProperty(DISPLAY_ENV_VARIABLE);
        } catch (SecurityException e) {
            logger.error("Unable to access jvm variable " + DISPLAY_ENV_VARIABLE + " " + e.getMessage());
        }
        if (null != bezirkDisplay) {
            boolBezirkDisplay = Boolean.parseBoolean(bezirkDisplay);
            logger.debug("Display Enabled using JVM property " + DISPLAY_ENV_VARIABLE + " --> " + boolBezirkDisplay);
        }
        return boolBezirkDisplay;
    }

    private boolean isEnabledInSystem() {
        String bezirkDisplay = null;
        boolean boolBezirkDisplay = false;
        try {
            bezirkDisplay = System.getenv(DISPLAY_ENV_VARIABLE);
        } catch (SecurityException e) {
            logger.error("Unable to access system variable " + DISPLAY_ENV_VARIABLE + " " + e.getMessage());
        }
        if (null != bezirkDisplay) {
            boolBezirkDisplay = Boolean.parseBoolean(bezirkDisplay);
            logger.debug("Display Enabled using system property " + DISPLAY_ENV_VARIABLE + " --> " + boolBezirkDisplay);
        }
        return boolBezirkDisplay;
    }
}