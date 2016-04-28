package com.bezirk.comms;

import android.os.Environment;

import com.bezirk.devices.UPADeviceForAndroid;
import com.bezirk.starter.BezirkPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class contains the platform specific
 */
public final class BezirkCommsAndroid {
    private static final Logger logger = LoggerFactory.getLogger(BezirkCommsAndroid.class);

    /* To avoid default public constructor. */
    private BezirkCommsAndroid() {
    }

    public static void init(BezirkPreferences pref) {

        //Initialize UPADeviceForAndroid preferences
        UPADeviceForAndroid.setPreferences(pref.getSharedPreferences());
        //Initializes ports , Addresses and MaxBufferSize
        //Refer res/preferences.xml for values
        BezirkCommunications.setINTERFACE_NAME(pref.getString("InterfaceName", null));
        logger.info("InterfaceName:" + BezirkCommunications.getINTERFACE_NAME());
        BezirkCommunications.setMULTICAST_ADDRESS(pref.getString("EMulticastAddress", null));
        logger.info("MulticastAddress " + BezirkCommunications.getMULTICAST_ADDRESS());
        BezirkCommunications.setMULTICAST_PORT(Integer.parseInt(pref.getString("EMulticastPort", "0")));
        logger.info("MulticastPort " + BezirkCommunications.getMULTICAST_PORT());
        BezirkCommunications.setUNICAST_PORT(Integer.parseInt(pref.getString("EUnicastPort", "0")));
        logger.info("UnicastPort " + BezirkCommunications.getUNICAST_PORT());
        BezirkCommunications.setCTRL_MULTICAST_ADDRESS(pref.getString("CMulticastAddress", null));
        logger.info("Ctrl MulticastAddress " + BezirkCommunications.getCTRL_MULTICAST_ADDRESS());
        BezirkCommunications.setCTRL_MULTICAST_PORT(Integer.valueOf(pref.getString("CMulticastPort", "0")));
        logger.info("Ctrl MulticastPort " + BezirkCommunications.getCTRL_MULTICAST_PORT());
        BezirkCommunications.setCTRL_UNICAST_PORT(Integer.valueOf(pref.getString("CUnicastPort", "0")));
        logger.info("Ctrl UnicastPort " + BezirkCommunications.getCTRL_UNICAST_PORT());
        BezirkCommunications.setMAX_BUFFER_SIZE(Integer.valueOf(pref.getString("MaxBufferSize", "0")));
        //Intialize POOL Size
        logger.info("Max Buffer Size " + BezirkCommunications.getMAX_BUFFER_SIZE());
        BezirkCommunications.setPOOL_SIZE(Integer.valueOf(pref.getString("MessageValidatorPool", "0")));

        BezirkCommunications.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("StartPort", "0")));
        logger.info("Starting Port for Streaming: " + BezirkCommunications.getSTARTING_PORT_FOR_STREAMING());
        BezirkCommunications.setENDING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("EndPort", "0")));
        logger.info("Ending port for Streaming " + BezirkCommunications.getENDING_PORT_FOR_STREAMING());
        BezirkCommunications.setMAX_SUPPORTED_STREAMS(Integer.valueOf(pref.getString("NoOfActiveThreads", "0")));
        logger.info("No of active threads supported " + BezirkCommunications.getMAX_SUPPORTED_STREAMS());
        BezirkCommunications.setStreamingEnabled(Boolean.valueOf(pref.getString("StreamingEnabled", "false")));
        logger.info("Is streaming Enabled" + BezirkCommunications.isStreamingEnabled());

        BezirkCommunications.setDOWNLOAD_PATH(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UhuDownloads/");
        if (BezirkCommunications.isStreamingEnabled()) {
            // port factory is moved to the uhu comms manager
            // create a Downloads folder
            File createDownloadFolder = new File(BezirkCommunications.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                if (!createDownloadFolder.mkdir()) {
                    logger.error("Failed to create download direction: {}",
                            createDownloadFolder.getAbsolutePath());
                }
            }
        }

        BezirkCommunications.setDEMO_SPHERE_MODE(Boolean.valueOf(pref.getString("DemoSphereMode", "false")));
        //Logging
        BezirkCommunications.setREMOTE_LOGGING_PORT(Integer.valueOf(pref.getString("RemoteLoggingPort", "7777")));
        BezirkCommunications.setRemoteLoggingServiceEnabled(Boolean.valueOf(pref.getString("RemoteLoggingEnabled", "false")));
    }
}
