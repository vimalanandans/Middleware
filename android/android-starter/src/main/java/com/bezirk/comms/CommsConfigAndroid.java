package com.bezirk.comms;

import android.os.Environment;

import com.bezirk.devices.AndroidDeviceInfo;
import com.bezirk.starter.MainStackPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class contains the platform specific
 */
public final class CommsConfigAndroid {
    private static final Logger logger = LoggerFactory.getLogger(CommsConfigAndroid.class);

    /* To avoid default public constructor. */
    private CommsConfigAndroid() {
    }

    public static void init(MainStackPreferences pref) {

        //Initialize AndroidDeviceInfo preferences
        AndroidDeviceInfo.setPreferences(pref.getSharedPreferences());
        //Initializes ports , Addresses and MaxBufferSize
        //Refer res/preferences.xml for values
        CommsConfigurations.setINTERFACE_NAME(pref.getString("InterfaceName", null));
        logger.info("InterfaceName:" + CommsConfigurations.getINTERFACE_NAME());
        CommsConfigurations.setMULTICAST_ADDRESS(pref.getString("EMulticastAddress", null));
        logger.info("MulticastAddress " + CommsConfigurations.getMULTICAST_ADDRESS());
        CommsConfigurations.setMULTICAST_PORT(Integer.parseInt(pref.getString("EMulticastPort", "0")));
        logger.info("MulticastPort " + CommsConfigurations.getMULTICAST_PORT());
        CommsConfigurations.setUNICAST_PORT(Integer.parseInt(pref.getString("EUnicastPort", "0")));
        logger.info("UnicastPort " + CommsConfigurations.getUNICAST_PORT());
        CommsConfigurations.setCTRL_MULTICAST_ADDRESS(pref.getString("CMulticastAddress", null));
        logger.info("Ctrl MulticastAddress " + CommsConfigurations.getCTRL_MULTICAST_ADDRESS());
        CommsConfigurations.setCTRL_MULTICAST_PORT(Integer.parseInt(pref.getString("CMulticastPort", "0")));
        logger.info("Ctrl MulticastPort " + CommsConfigurations.getCTRL_MULTICAST_PORT());
        CommsConfigurations.setCTRL_UNICAST_PORT(Integer.parseInt(pref.getString("CUnicastPort", "0")));
        logger.info("Ctrl UnicastPort " + CommsConfigurations.getCTRL_UNICAST_PORT());
        CommsConfigurations.setMAX_BUFFER_SIZE(Integer.parseInt(pref.getString("MaxBufferSize", "0")));
        //Intialize POOL Size
        logger.info("Max Buffer Size " + CommsConfigurations.getMAX_BUFFER_SIZE());
        CommsConfigurations.setPOOL_SIZE(Integer.parseInt(pref.getString("MessageValidatorPool", "0")));

        CommsConfigurations.setSTARTING_PORT_FOR_STREAMING(Integer.parseInt(pref.getString("StartPort", "0")));
        logger.info("Starting Port for Streaming: " + CommsConfigurations.getSTARTING_PORT_FOR_STREAMING());
        CommsConfigurations.setENDING_PORT_FOR_STREAMING(Integer.parseInt(pref.getString("EndPort", "0")));
        logger.info("Ending port for Streaming " + CommsConfigurations.getENDING_PORT_FOR_STREAMING());
        CommsConfigurations.setMAX_SUPPORTED_STREAMS(Integer.parseInt(pref.getString("NoOfActiveThreads", "0")));
        logger.info("No of active threads supported " + CommsConfigurations.getMAX_SUPPORTED_STREAMS());
        CommsConfigurations.setStreamingEnabled(Boolean.parseBoolean(pref.getString("StreamingEnabled", "false")));
        logger.info("Is streaming Enabled" + CommsConfigurations.isStreamingEnabled());

        CommsConfigurations.setDOWNLOAD_PATH(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UhuDownloads/");
        if (CommsConfigurations.isStreamingEnabled()) {
            // port factory is moved to the bezirk comms manager
            // create a Downloads folder
            File createDownloadFolder = new File(CommsConfigurations.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }

        CommsConfigurations.setDEMO_SPHERE_MODE(Boolean.valueOf(pref.getString("DemoSphereMode", "false")));
        //Logging
        CommsConfigurations.setREMOTE_LOGGING_PORT(Integer.parseInt(pref.getString("RemoteLoggingPort", "7777")));
        CommsConfigurations.setRemoteLoggingServiceEnabled(Boolean.valueOf(pref.getString("RemoteLoggingEnabled", "false")));
    }
}
