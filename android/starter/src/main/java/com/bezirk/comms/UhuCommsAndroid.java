package com.bezirk.comms;

import android.os.Environment;

import com.bezirk.devices.UPADeviceForAndroid;
import com.bezirk.starter.UhuPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by Mansimar Aneja ANM1PI on 9/5/2014.
 * This class contains the platform specific
 */
public final class UhuCommsAndroid {
    private static final Logger logger = LoggerFactory.getLogger(UhuCommsAndroid.class);

    /* To avoid default public constructor. */
    private UhuCommsAndroid() {
    }

    public static void init(UhuPreferences pref) {

        //Initialize UPADeviceForAndroid preferences
        UPADeviceForAndroid.setPreferences(pref.getSharedPreferences());
        //Initializes ports , Addresses and MaxBufferSize
        //Refer res/preferences.xml for values
        BezirkComms.setINTERFACE_NAME(pref.getString("InterfaceName", null));
        logger.info("InterfaceName:" + BezirkComms.getINTERFACE_NAME());
        BezirkComms.setMULTICAST_ADDRESS(pref.getString("EMulticastAddress", null));
        logger.info("MulticastAddress " + BezirkComms.getMULTICAST_ADDRESS());
        BezirkComms.setMULTICAST_PORT(Integer.parseInt(pref.getString("EMulticastPort", "0")));
        logger.info("MulticastPort " + BezirkComms.getMULTICAST_PORT());
        BezirkComms.setUNICAST_PORT(Integer.parseInt(pref.getString("EUnicastPort", "0")));
        logger.info("UnicastPort " + BezirkComms.getUNICAST_PORT());
        BezirkComms.setCTRL_MULTICAST_ADDRESS(pref.getString("CMulticastAddress", null));
        logger.info("Ctrl MulticastAddress " + BezirkComms.getCTRL_MULTICAST_ADDRESS());
        BezirkComms.setCTRL_MULTICAST_PORT(Integer.valueOf(pref.getString("CMulticastPort", "0")));
        logger.info("Ctrl MulticastPort " + BezirkComms.getCTRL_MULTICAST_PORT());
        BezirkComms.setCTRL_UNICAST_PORT(Integer.valueOf(pref.getString("CUnicastPort", "0")));
        logger.info("Ctrl UnicastPort " + BezirkComms.getCTRL_UNICAST_PORT());
        BezirkComms.setMAX_BUFFER_SIZE(Integer.valueOf(pref.getString("MaxBufferSize", "0")));
        //Intialize POOL Size
        logger.info("Max Buffer Size " + BezirkComms.getMAX_BUFFER_SIZE());
        BezirkComms.setPOOL_SIZE(Integer.valueOf(pref.getString("MessageValidatorPool", "0")));

        BezirkComms.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("StartPort", "0")));
        logger.info("Starting Port for Streaming: " + BezirkComms.getSTARTING_PORT_FOR_STREAMING());
        BezirkComms.setENDING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("EndPort", "0")));
        logger.info("Ending port for Streaming " + BezirkComms.getENDING_PORT_FOR_STREAMING());
        BezirkComms.setMAX_SUPPORTED_STREAMS(Integer.valueOf(pref.getString("NoOfActiveThreads", "0")));
        logger.info("No of active threads supported " + BezirkComms.getMAX_SUPPORTED_STREAMS());
        BezirkComms.setStreamingEnabled(Boolean.valueOf(pref.getString("StreamingEnabled", "false")));
        logger.info("Is streaming Enabled" + BezirkComms.isStreamingEnabled());

        BezirkComms.setDOWNLOAD_PATH(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UhuDownloads/");
        if (BezirkComms.isStreamingEnabled()) {
            // port factory is moved to the uhu comms manager
            // create a Downloads folder
            File createDownloadFolder = new File(BezirkComms.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }

        BezirkComms.setDEMO_SPHERE_MODE(Boolean.valueOf(pref.getString("DemoSphereMode", "false")));
        //Logging
        BezirkComms.setREMOTE_LOGGING_PORT(Integer.valueOf(pref.getString("RemoteLoggingPort", "7777")));
        BezirkComms.setRemoteLoggingServiceEnabled(Boolean.valueOf(pref.getString("RemoteLoggingEnabled", "false")));
    }
}
