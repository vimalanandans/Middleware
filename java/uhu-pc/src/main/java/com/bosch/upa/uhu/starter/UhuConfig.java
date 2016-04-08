package com.bosch.upa.uhu.starter;

import java.io.File;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hold the Uhu Config
 * 
 * At the moment it holds the data about Uhu PC TODO: Move this file to
 * java-common so that all the configurations are inside this module.
 * 
 */
public class UhuConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuConfig.class);

    /** Relative data path */
    public static final String DATA_PATH_REL = "data" + File.separator + "uhu";

    /**
     * This will hold the full data path, which will be relative to APP_HOME or
     */
    private String dataPath;

    /** display enable or disable */
    private String displayEnable = "true";

    public UhuConfig() {
        // load the resources
        initDataPath();
    }

    /**
     * Set the application home directory. If we are running from the binary
     * distribution, appHome is set to: /path/to/uhu-main-VERS. If we are
     * running from the developement envronment, appHome is set to the project
     * root directory.
     */
    private void initDataPath() {
        // The launcher script sets this system variable
        String appHome = System.getenv().get("APP_HOME");
        if (appHome == null || appHome.isEmpty()) {
            //appHome = "."; // current working dir means service root
            // take from temp or user folder because . doesn't work in unix
            //appHome =  System.getProperty("java.io.tmpdir");
            appHome =  System.getProperty("user.dir");

        }

        // set the datapath relative to APP_HOME or "." if APP_HOME not set
        dataPath = appHome + File.separator + DATA_PATH_REL;

        // create the data dir if it doesn't exist
        final File dataDir = new File(dataPath);
        if (dataDir.exists()) {

            LOGGER.info("Using existing dataDir:" + dataDir);

        } else {

            LOGGER.info("Creating dataDir: " + dataDir);
            dataDir.mkdirs();
        }
    }

    public void setDataPath(final String path) {
        dataPath = path;
    }

    public String getDataPath() {

        return dataPath;

    }

    public void setDisplayEnable(final String enable) {
        displayEnable = enable;
    }

    public String getDisplayEnable() {
        return displayEnable;
    }

    public boolean isDisplayEnabled() {

        return displayEnable.toLowerCase(Locale.US).contentEquals("true");
    }

}