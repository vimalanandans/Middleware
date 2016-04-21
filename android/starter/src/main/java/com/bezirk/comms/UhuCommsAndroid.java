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
    private static final Logger log = LoggerFactory.getLogger(UhuCommsAndroid.class);

    /* To avoid default public constructor. */
    private UhuCommsAndroid() {
    }

    public static void init(UhuPreferences pref) {

        //Initialize UPADeviceForAndroid preferences
        UPADeviceForAndroid.setPreferences(pref.getSharedPreferences());
        //Initializes ports , Addresses and MaxBufferSize
        //Refer res/preferences.xml for values
        UhuComms.setINTERFACE_NAME(pref.getString("InterfaceName", null));
        log.info("InterfaceName:" + UhuComms.getINTERFACE_NAME());
        UhuComms.setMULTICAST_ADDRESS(pref.getString("EMulticastAddress", null));
        log.info("MulticastAddress " + UhuComms.getMULTICAST_ADDRESS());
        UhuComms.setMULTICAST_PORT(Integer.valueOf(pref.getString("EMulticastPort", "0")));
        log.info("MulticastPort " + UhuComms.getMULTICAST_PORT());
        UhuComms.setUNICAST_PORT(Integer.valueOf(pref.getString("EUnicastPort", "0")));
        log.info("UnicastPort " + UhuComms.getUNICAST_PORT());
        UhuComms.setCTRL_MULTICAST_ADDRESS(pref.getString("CMulticastAddress", null));
        log.info("Ctrl MulticastAddress " + UhuComms.getCTRL_MULTICAST_ADDRESS());
        UhuComms.setCTRL_MULTICAST_PORT(Integer.valueOf(pref.getString("CMulticastPort", "0")));
        log.info("Ctrl MulticastPort " + UhuComms.getCTRL_MULTICAST_PORT());
        UhuComms.setCTRL_UNICAST_PORT(Integer.valueOf(pref.getString("CUnicastPort", "0")));
        log.info("Ctrl UnicastPort " + UhuComms.getCTRL_UNICAST_PORT());
        UhuComms.setMAX_BUFFER_SIZE(Integer.valueOf(pref.getString("MaxBufferSize", "0")));
        //Intialize POOL Size
        log.info("Max Buffer Size " + UhuComms.getMAX_BUFFER_SIZE());
        UhuComms.setPOOL_SIZE(Integer.valueOf(pref.getString("MessageValidatorPool", "0")));

        UhuComms.setSTARTING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("StartPort", "0")));
        log.info("Starting Port for Streaming: " + UhuComms.getSTARTING_PORT_FOR_STREAMING());
        UhuComms.setENDING_PORT_FOR_STREAMING(Integer.valueOf(pref.getString("EndPort", "0")));
        log.info("Ending port for Streaming " + UhuComms.getENDING_PORT_FOR_STREAMING());
        UhuComms.setMAX_SUPPORTED_STREAMS(Integer.valueOf(pref.getString("NoOfActiveThreads", "0")));
        log.info("No of active threads supported " + UhuComms.getMAX_SUPPORTED_STREAMS());
        UhuComms.setStreamingEnabled(Boolean.valueOf(pref.getString("StreamingEnabled", "false")));
        log.info("Is streaming Enabled" + UhuComms.isStreamingEnabled());

        UhuComms.setDOWNLOAD_PATH(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UhuDownloads/");
        if (UhuComms.isStreamingEnabled()) {
            // port factory is moved to the uhu comms manager
            // create a Downloads folder
            File createDownloadFolder = new File(UhuComms.getDOWNLOAD_PATH());
            if (!createDownloadFolder.exists()) {
                createDownloadFolder.mkdir();
            }
        }

        UhuComms.setDEMO_SPHERE_MODE(Boolean.valueOf(pref.getString("DemoSphereMode", "false")));
        //Logging
        UhuComms.setREMOTE_LOGGING_PORT(Integer.valueOf(pref.getString("RemoteLoggingPort", "7777")));
        UhuComms.setRemoteLoggingServiceEnabled(Boolean.valueOf(pref.getString("RemoteLoggingEnabled", "false")));
    }
}
