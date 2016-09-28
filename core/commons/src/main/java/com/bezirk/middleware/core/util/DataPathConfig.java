package com.bezirk.middleware.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Maintain data-path for storing bezirk database, for pure-java/PC version and android of Bezirk
 */
public class DataPathConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataPathConfig.class);
    private static final String DATA = "data"; // name of data folder
    private static final String BEZIRK = "bezirk"; // name of folder for holding bezirk related data/database(s)
    // relative data path for bezirk folder
    private static final String DATA_PATH_REL = DATA + File.separator + BEZIRK +  File.separator;

    // variable set in gradle/environment to enable/disable sphere-mgmt display
    private final static String DISPLAY_ENV_VARIABLE = "displayEnabled";
    // default value of bezirk sphere-mgmt display
    private final static boolean DISPLAY_DEFAULT_VALUE = false;

    // variable set in gradle/environment to enable/disable bezirk logs
    private final static String LOGGING_ENV_VARIABLE = "loggingEnabled";
    private final static boolean LOGGING_DEFAULT_VALUE = false; // default value of bezirk logging

    private String dataPath; // holds the absolute path of the bezirk folder

    private  static DataPathConfig dataPathConfig = new DataPathConfig();

    public DataPathConfig() {
        init();
    }

    private void init() {

        if(System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
            dataPath = File.separator+"storage/emulated/0/" + DATA_PATH_REL;
        }
        else {
            // gradle sets this when invoked
            String appHome = System.getenv().get("APP_HOME");
            if (appHome == null || appHome.isEmpty()) {
                appHome = System.getProperty("user.dir");
            }
            // set data path
            dataPath = appHome + File.separator + DATA_PATH_REL;
        }



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


    }

    /**
     * Get absolute data path of the bezirk folder
     *
     * @return absolute path of the bezirk folder
     */
    static public String getDataPath() {
        return dataPathConfig.dataPath;
    }


}