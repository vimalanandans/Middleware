package com.bezirk.comms;

import com.bezirk.devices.UPADeviceForPC;
import com.bezirk.starter.UhuConfig;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public final class UhuCommsPC {
    public static final String PROPS_FILE = "comms.properties";
    // Comms properties
    private static final Logger logger = LoggerFactory.getLogger(UhuCommsPC.class);

    private UhuCommsPC() {
        /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    }

    public static void init() {
        init(null);
    }

    public static void init(UhuConfig uhuConfig) {
        Properties props = new Properties();
        // First try loading from the current directory
        try {
            props = loadProperties();
        } catch (Exception e) {
            logger.error("Could not read comms properties file", e);
        }

        // overrides the value for these properties if it is set as a system
        // property
        overrideStringProperty("InterfaceName", props, uhuConfig);
        overrideStringProperty("displayEnable", props, uhuConfig);

        BezirkComms.setINTERFACE_NAME(props.getProperty("InterfaceName"));
        BezirkComms.setMULTICAST_ADDRESS(props.getProperty("EMulticastAddress"));
        BezirkComms.setMULTICAST_PORT(Integer.parseInt(props.getProperty("EMulticastPort")));
        BezirkComms.setUNICAST_PORT(Integer.parseInt(props
                .getProperty("EUnicastPort")));
        BezirkComms.setCTRL_MULTICAST_ADDRESS(props
                .getProperty("CMulticastAddress"));
        BezirkComms.setCTRL_MULTICAST_PORT(Integer.valueOf(props
                .getProperty("CMulticastPort")));
        BezirkComms.setCTRL_UNICAST_PORT(Integer.valueOf(props
                .getProperty("CUnicastPort")));
        BezirkComms.setMAX_BUFFER_SIZE(Integer.valueOf(props
                .getProperty("MaxBufferSize")));

        BezirkComms.setPOOL_SIZE(Integer.valueOf(props
                .getProperty("MessageValidatorPool")));

        BezirkComms.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("StartPort"))); // get the starting Port
        BezirkComms.setENDING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("EndPort"))); // get the last port
        BezirkComms.setMAX_SUPPORTED_STREAMS(Integer.valueOf(props
                .getProperty("NoOfActiveThreads"))); // No of active Threads
        BezirkComms.setStreamingEnabled(Boolean.valueOf(props
                .getProperty("StreamingEnabled"))); // flag to check if
        // Streaming Enabled

        BezirkComms.setNO_OF_RETRIES(Integer.valueOf(props
                .getProperty("NoOfRetries")));

        if (BezirkComms.isStreamingEnabled()) {
            // port factory is part of comms manager
            // BezirkComms.portFactory = new
            // PortFactory(BezirkComms.STARTING_PORT_FOR_STREAMING,
            // BezirkComms.ENDING_PORT_FOR_STREAMING); // initialize the
            // PortFactory
            if (uhuConfig == null) {
                BezirkComms.setDOWNLOAD_PATH(props.getProperty("FileSharePath"));
            } else {
                BezirkComms.setDOWNLOAD_PATH(uhuConfig.getDataPath()
                        + File.separator + "downloads");
            }
            final File createDownloadFolder = new File(
                    BezirkComms.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }

        BezirkComms.setDEMO_SPHERE_MODE(Boolean.valueOf(props.getProperty(
                "DemoSphereMode", "false")));
        BezirkComms.setREMOTE_LOGGING_PORT(Integer.valueOf(props
                .getProperty("RemoteLoggingPort")));
        BezirkComms.setRemoteLoggingServiceEnabled(Boolean.valueOf(props
                .getProperty("RemoteLoggingEnabled")));
    }

    /**
     * Allows us to override comms.properties values by reading a system property and setting it on
     * the properties object. Specifically, this method checks for a system property with the key
     * propName.  If there is such a property, we overwrite the value that may have already
     * been set in the properties object.
     * The system property can be set, e.g., via a -D option on the command line.
     *
     * @param propName
     * @return
     */
    private static void overrideStringProperty(String propName,
                                               Properties props, UhuConfig uhuConfig) {
        final String value = System.getProperty(propName);
        if (BezirkValidatorUtility.checkForString(value)) {
            logger.info("found system property: " + propName + ": " + value);
            props.setProperty(propName, value);

            if ("displayEnable".equals(propName)) {
                uhuConfig.setDisplayEnable(value);
            }
        } else {
            return;

        }
    }

    public static Properties loadProperties() throws Exception {
        return UPADeviceForPC.loadProperties(PROPS_FILE);
    }
}
