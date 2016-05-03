package com.bezirk.comms;

import com.bezirk.devices.BezirkDeviceForPC;
import com.bezirk.starter.BezirkConfig;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

public final class BezirkCommsPC {
    public static final String PROPS_FILE = "comms.properties";
    // Comms properties
    private static final Logger logger = LoggerFactory.getLogger(BezirkCommsPC.class);

    private BezirkCommsPC() {
        /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    }

    public static void init() {
        init(null);
    }

    public static void init(BezirkConfig bezirkConfig) {
        Properties props = new Properties();
        // First try loading from the current directory
        try {
            props = loadProperties();
        } catch (Exception e) {
            logger.error("Could not read comms properties file", e);
        }

        // overrides the value for these properties if it is set as a system
        // property
        overrideStringProperty("InterfaceName", props, bezirkConfig);
        overrideStringProperty("displayEnable", props, bezirkConfig);

        BezirkCommunications.setINTERFACE_NAME(props.getProperty("InterfaceName"));
        BezirkCommunications.setMULTICAST_ADDRESS(props.getProperty("EMulticastAddress"));
        BezirkCommunications.setMULTICAST_PORT(Integer.parseInt(props.getProperty("EMulticastPort")));
        BezirkCommunications.setUNICAST_PORT(Integer.parseInt(props
                .getProperty("EUnicastPort")));
        BezirkCommunications.setCTRL_MULTICAST_ADDRESS(props
                .getProperty("CMulticastAddress"));
        BezirkCommunications.setCTRL_MULTICAST_PORT(Integer.valueOf(props
                .getProperty("CMulticastPort")));
        BezirkCommunications.setCTRL_UNICAST_PORT(Integer.valueOf(props
                .getProperty("CUnicastPort")));
        BezirkCommunications.setMAX_BUFFER_SIZE(Integer.valueOf(props
                .getProperty("MaxBufferSize")));

        BezirkCommunications.setPOOL_SIZE(Integer.valueOf(props
                .getProperty("MessageValidatorPool")));

        BezirkCommunications.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("StartPort"))); // get the starting Port
        BezirkCommunications.setENDING_PORT_FOR_STREAMING(Integer.valueOf(props
                .getProperty("EndPort"))); // get the last port
        BezirkCommunications.setMAX_SUPPORTED_STREAMS(Integer.valueOf(props
                .getProperty("NoOfActiveThreads"))); // No of active Threads
        BezirkCommunications.setStreamingEnabled(Boolean.valueOf(props
                .getProperty("StreamingEnabled"))); // flag to check if
        // Streaming Enabled

        BezirkCommunications.setNO_OF_RETRIES(Integer.valueOf(props
                .getProperty("NoOfRetries")));

        if (BezirkCommunications.isStreamingEnabled()) {
            // port factory is part of comms manager
            // BezirkCommunications.portFactory = new
            // PortFactory(BezirkCommunications.STARTING_PORT_FOR_STREAMING,
            // BezirkCommunications.ENDING_PORT_FOR_STREAMING); // initialize the
            // PortFactory
            if (bezirkConfig == null) {
                BezirkCommunications.setDOWNLOAD_PATH(props.getProperty("FileSharePath"));
            } else {
                BezirkCommunications.setDOWNLOAD_PATH(bezirkConfig.getDataPath()
                        + File.separator + "downloads");
            }
            final File createDownloadFolder = new File(
                    BezirkCommunications.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }

        BezirkCommunications.setDEMO_SPHERE_MODE(Boolean.valueOf(props.getProperty(
                "DemoSphereMode", "false")));
        BezirkCommunications.setREMOTE_LOGGING_PORT(Integer.valueOf(props
                .getProperty("RemoteLoggingPort")));
        BezirkCommunications.setRemoteLoggingServiceEnabled(Boolean.valueOf(props
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
     */
    private static void overrideStringProperty(String propName,
                                               Properties props, BezirkConfig bezirkConfig) {
        final String value = System.getProperty(propName);

        if (BezirkValidatorUtility.checkForString(value)) {
            logger.info("found system property: " + propName + ": " + value);
            props.setProperty(propName, value);

            if ("displayEnable".equals(propName)) {
                bezirkConfig.setDisplayEnable(value);
            }
        }
    }

    public static Properties loadProperties() throws Exception {
        return BezirkDeviceForPC.loadProperties(PROPS_FILE);
    }
}
