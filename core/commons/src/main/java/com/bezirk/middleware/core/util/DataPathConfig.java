/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
    private static final String DISPLAY_ENV_VARIABLE = "displayEnabled";
    // default value of bezirk sphere-mgmt display
    private static final boolean DISPLAY_DEFAULT_VALUE = false;

    // variable set in gradle/environment to enable/disable bezirk logs
    private static final String LOGGING_ENV_VARIABLE = "loggingEnabled";
    private static final boolean LOGGING_DEFAULT_VALUE = false; // default value of bezirk logging

    private String dataPath; // holds the absolute path of the bezirk folder

    private  static final DataPathConfig dataPathConfig = new DataPathConfig();

    public DataPathConfig() {
        init();
    }

    private void init() {
        if(System.getProperty("java.vm.name") != null &&
                System.getProperty("java.vm.name").equalsIgnoreCase("Dalvik")) {
            dataPath = File.separator+"storage/emulated/0/" + DATA_PATH_REL;
        } else {
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
    public static String getDataPath() {
        return dataPathConfig.dataPath;
    }

}
