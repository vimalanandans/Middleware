package com.bezirk.comms;

import com.bezirk.starter.BezirkConfig;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;


public final class BezirkCommsPC {
    //public static final String PROPS_FILE = "comms.properties";
    // Comms properties
    private static final Logger logger = LoggerFactory.getLogger(BezirkCommsPC.class);

    private BezirkCommsPC() {
        /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    }

    public static void init() {
        init(null);
    }

//    public static void init(BezirkConfig bezirkConfig) {
//        Properties props = new Properties();
//        // First try loading from the current directory
//        try {
//            props = loadProperties();
//        } catch (Exception e) {
//            logger.error("Could not read comms properties file", e);
//        }
//
//        // overrides the value for these properties if it is set as a system
//        // property
//        overrideStringProperty("InterfaceName", props, bezirkConfig);
//        overrideStringProperty("displayEnable", props, bezirkConfig);
//
//        CommsConfigurations.setINTERFACE_NAME(props.getProperty("InterfaceName"));
//        CommsConfigurations.setMULTICAST_ADDRESS(props.getProperty("EMulticastAddress"));
//        CommsConfigurations.setMULTICAST_PORT(Integer.parseInt(props.getProperty("EMulticastPort")));
//        CommsConfigurations.setUNICAST_PORT(Integer.parseInt(props
//                .getProperty("EUnicastPort")));
//        CommsConfigurations.setCTRL_MULTICAST_ADDRESS(props
//                .getProperty("CMulticastAddress"));
//        CommsConfigurations.setCTRL_MULTICAST_PORT(Integer.valueOf(props
//                .getProperty("CMulticastPort")));
//        CommsConfigurations.setCTRL_UNICAST_PORT(Integer.valueOf(props
//                .getProperty("CUnicastPort")));
//        CommsConfigurations.setMAX_BUFFER_SIZE(Integer.valueOf(props
//                .getProperty("MaxBufferSize")));
//
//        CommsConfigurations.setPOOL_SIZE(Integer.valueOf(props
//                .getProperty("MessageValidatorPool")));
//
//        CommsConfigurations.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(props
//                .getProperty("StartPort"))); // get the starting Port
//        CommsConfigurations.setENDING_PORT_FOR_STREAMING(Integer.valueOf(props
//                .getProperty("EndPort"))); // get the last port
//        CommsConfigurations.setMAX_SUPPORTED_STREAMS(Integer.valueOf(props
//                .getProperty("NoOfActiveThreads"))); // No of active Threads
//        CommsConfigurations.setStreamingEnabled(Boolean.valueOf(props
//                .getProperty("StreamingEnabled"))); // flag to check if
//        // Streaming Enabled
//
//        CommsConfigurations.setNO_OF_RETRIES(Integer.valueOf(props
//                .getProperty("NoOfRetries")));
//
//        if (CommsConfigurations.isStreamingEnabled()) {
//            // port factory is part of comms manager
//            // CommsConfigurations.portFactory = new
//            // StreamPortFactory(CommsConfigurations.STARTING_PORT_FOR_STREAMING,
//            // CommsConfigurations.ENDING_PORT_FOR_STREAMING); // initialize the
//            // StreamPortFactory
//            if (bezirkConfig == null) {
//                CommsConfigurations.setDOWNLOAD_PATH(props.getProperty("FileSharePath"));
//            } else {
//                CommsConfigurations.setDOWNLOAD_PATH(bezirkConfig.getDataPath()
//                        + File.separator + "downloads");
//            }
//            final File createDownloadFolder = new File(
//                    CommsConfigurations.getDOWNLOAD_PATH());
//            if (!createDownloadFolder.exists()) {
//                if (!createDownloadFolder.mkdir()) {
//                    logger.error("Failed to create download direction: {}",
//                            createDownloadFolder.getAbsolutePath());
//                }
//            }
//        }
//
//        CommsConfigurations.setDEMO_SPHERE_MODE(Boolean.valueOf(props.getProperty(
//                "DemoSphereMode", "false")));
//        CommsConfigurations.setREMOTE_LOGGING_PORT(Integer.valueOf(props
//                .getProperty("RemoteLoggingPort")));
//        CommsConfigurations.setRemoteLoggingServiceEnabled(Boolean.valueOf(props
//                .getProperty("RemoteLoggingEnabled")));
//    }


    public static void init(BezirkConfig bezirkConfig) {
        //overrideStringProperty("InterfaceName", props, bezirkConfig);
        //overrideStringProperty("displayEnable", props, bezirkConfig);

        CommsConfigurations.setINTERFACE_NAME("en0");
      /*  CommsConfigurations.setMULTICAST_ADDRESS("224.5.6.7");
        CommsConfigurations.setMULTICAST_PORT(9999);
        CommsConfigurations.setUNICAST_PORT(8888);
        CommsConfigurations.setCTRL_MULTICAST_ADDRESS("224.5.6.7");
        CommsConfigurations.setCTRL_MULTICAST_PORT(9997);
        CommsConfigurations.setCTRL_UNICAST_PORT(8887);
        CommsConfigurations.setMAX_BUFFER_SIZE(10240);
        CommsConfigurations.setPOOL_SIZE(5);
       CommsConfigurations.setSTARTING_PORT_FOR_STREAMING(6321);
        CommsConfigurations.setENDING_PORT_FOR_STREAMING(6330);
        //CommsConfigurations.setMAX_SUPPORTED_STREAMS(5);
        //CommsConfigurations.setStreamingEnabled(true);
        //CommsConfigurations.setNO_OF_RETRIES(5);

        if (CommsConfigurations.isStreamingEnabled()) {
            // port factory is part of comms manager
            // CommsConfigurations.portFactory = new
            // StreamPortFactory(CommsConfigurations.STARTING_PORT_FOR_STREAMING,
            // CommsConfigurations.ENDING_PORT_FOR_STREAMING); // initialize the
            // StreamPortFactory
            if (bezirkConfig == null) {
                CommsConfigurations.setDOWNLOAD_PATH("D:\\Uhu_Downloads\\");
            } else {
                CommsConfigurations.setDOWNLOAD_PATH(bezirkConfig.getDataPath()
                        + File.separator + "downloads");
            }
            final File createDownloadFolder = new File(
                    CommsConfigurations.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }
/*
      //  CommsConfigurations.setDEMO_SPHERE_MODE(true);
        CommsConfigurations.setREMOTE_LOGGING_PORT(7777);
        CommsConfigurations.setRemoteLoggingServiceEnabled(false);
        */
    }

    /**
     * Allows us to override comms.properties values by reading a system property and setting it on
     * the properties object. Specifically, this method checks for a system property with the key
     * propName.  If there is such a property, we overwrite the value that may have already
     * been set in the properties object.
     * The system property can be set, e.g., via a -D option on the command line.
     *
     * @param propName
     */
    private static void overrideStringProperty(String propName,
                                               Properties props, BezirkConfig bezirkConfig) {
        final String value = System.getProperty(propName);

        if (ValidatorUtility.checkForString(value)) {
            logger.info("found system property: " + propName + ": " + value);
            props.setProperty(propName, value);

//            if ("displayEnable".equals(propName)) {
//                bezirkConfig.setDisplayEnable(value);
//            }
        }
    }

//    public static Properties loadProperties() throws Exception {
//        return DeviceForPC.loadProperties(PROPS_FILE);
//    }
}
