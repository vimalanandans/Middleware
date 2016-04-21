package com.bezirk.comms;

import com.bezirk.devices.UPADeviceForPC;
import com.bezirk.starter.UhuConfig;
import com.bezirk.util.UhuValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public final class UhuCommsPC {
    public static final String PROPS_FILE = "comms.properties";
    // Comms properties
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UhuCommsPC.class);

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
            LOGGER.error("Could not read comms properties file", e);
        }

        // overrides the value for these properties if it is set as a system
        // property
        overrideStringProperty("InterfaceName", props, uhuConfig);
        overrideStringProperty("displayEnable", props, uhuConfig);

        UhuComms.setINTERFACE_NAME(props.getProperty("InterfaceName"));
        UhuComms.setMULTICAST_ADDRESS(props.getProperty("EMulticastAddress"));
        UhuComms.setMULTICAST_PORT(Integer.valueOf(props
                .getProperty("EMulticastPort")));
        UhuComms.setUNICAST_PORT(Integer.valueOf(props
                .getProperty("EUnicastPort")));
        UhuComms.setCTRL_MULTICAST_ADDRESS(props
                .getProperty("CMulticastAddress"));
        UhuComms.setCTRL_MULTICAST_PORT(Integer.valueOf(props
                .getProperty("CMulticastPort")));
        UhuComms.setCTRL_UNICAST_PORT(Integer.valueOf(props
                .getProperty("CUnicastPort")));
        UhuComms.setMAX_BUFFER_SIZE(Integer.valueOf(props
                .getProperty("MaxBufferSize")));

        UhuComms.setPOOL_SIZE(Integer.valueOf(props
                .getProperty("MessageValidatorPool")));

        UhuComms.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("StartPort"))); // get the starting Port
        UhuComms.setENDING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("EndPort"))); // get the last port
        UhuComms.setMAX_SUPPORTED_STREAMS(Integer.valueOf(props
                .getProperty("NoOfActiveThreads"))); // No of active Threads
        UhuComms.setStreamingEnabled(Boolean.valueOf(props
                .getProperty("StreamingEnabled"))); // flag to check if
        // Streaming Enabled

        UhuComms.setNO_OF_RETRIES(Integer.valueOf(props
                .getProperty("NoOfRetries")));

        if (UhuComms.isStreamingEnabled()) {
            // port factory is part of comms manager
            // UhuComms.portFactory = new
            // PortFactory(UhuComms.STARTING_PORT_FOR_STREAMING,
            // UhuComms.ENDING_PORT_FOR_STREAMING); // initialize the
            // PortFactory
            if (uhuConfig == null) {
                UhuComms.setDOWNLOAD_PATH(props.getProperty("FileSharePath"));
            } else {
                UhuComms.setDOWNLOAD_PATH(uhuConfig.getDataPath()
                        + File.separator + "downloads");
            }
            final File createDownloadFolder = new File(
                    UhuComms.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                createDownloadFolder.mkdir();
            }
        }

        UhuComms.setDEMO_SPHERE_MODE(Boolean.valueOf(props.getProperty(
                "DemoSphereMode", "false")));
        UhuComms.setREMOTE_LOGGING_PORT(Integer.valueOf(props
                .getProperty("RemoteLoggingPort")));
        UhuComms.setRemoteLoggingServiceEnabled(Boolean.valueOf(props
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
        if (UhuValidatorUtility.checkForString(value)) {
            LOGGER.info("found system property: " + propName + ": " + value);
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
