package com.bezirk.starter.helper;

import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.CommsFactory;
import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.features.CommsFeature;
import com.bezirk.messagehandler.AndroidZirkMessageHandler;
import com.bezirk.persistence.IDatabaseConnection;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.util.DatabaseConnectionForAndroid;
import com.bezirk.pipe.android.PipeCommsFactory;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.proxy.android.ProxyforServices;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.starter.MainService;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by AJC6KOR on 1/11/2016.
 */
class UhuStartStackHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuStartStackHelper.class);


    boolean isWifiEnabled(WifiManager wifi) {
        return wifi != null && wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    RegistryPersistence initializeRegistryPersistence(MainService service) {
        IDatabaseConnection dbConnection = new DatabaseConnectionForAndroid(service);
        String DB_VERSION = "0.0.4";
        RegistryPersistence registryPersistence = null;

        try {
            registryPersistence = new RegistryPersistence(dbConnection, DB_VERSION);
        } catch (Exception e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        return registryPersistence;
    }

    void acquireWifiLock(WifiManager wifi) {
        try {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("Log_Tag");
            lock.acquire();
        } catch (UnsupportedOperationException e) {
            LOGGER.error("UnsupportedOperationException thrown while acquiring lock", e);
        }
    }

    boolean isIPAddressValid(MainService service, WifiManager wifi, UhuAndroidNetworkUtil androidNetworkUtil) {
        String ipAddress;
        try {
            ipAddress = androidNetworkUtil.getIpAddress(wifi);
        } catch (UnknownHostException e) {
            LOGGER.error("Unable to get ip address. Is it connected to network", e);
            Toast.makeText(service.getApplicationContext(), "Unable to get ip address. Is it connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (BezirkValidatorUtility.checkForString(ipAddress)) {
            LOGGER.info("Wifi Connected to " + wifi.getConnectionInfo().getSSID());
        } else {
            LOGGER.error("Unable to get ip address. Is it connected to network");
            Toast.makeText(service.getApplicationContext(), "Unable to get ip address. Is it connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void setAndroicallback(MainService service) {
        if (UhuCompManager.getplatformSpecificCallback() == null) {
            AndroidZirkMessageHandler uhuAndroidCallback = new AndroidZirkMessageHandler(service.getApplicationContext());
            UhuCompManager.setplatformSpecificCallback(uhuAndroidCallback);
        }
    }

    IUhuComms initializeComms(InetAddress inetAddress, UhuSadlManager uhuSadlManager, ProxyforServices proxy, ICommsNotification errNotificationCallback) {
        // Instantiate pipeManager before SenderThread so that it is ready to start sending over pipes
        PipeManager pipeComms = PipeCommsFactory.createPipeComms();

        CommsFactory commsFactory = new CommsFactory();

        IUhuComms comms;

        // comms zyre jni is injected from platform specific code
        if (commsFactory.getActiveComms() == CommsFeature.COMMS_ZYRE_JNI) {
            String arch = System.getProperty("os.arch");
            LOGGER.info("phone arch " + arch);
            comms = new ZyreCommsManager();
        } else {
            //rest of the comms are returned from factory
            /** Initialize the comms. */
            comms = new CommsFactory().getComms();
        }


        // the comms manager for the proxy
        proxy.setComms(comms);

        //init the error notification
        comms.registerNotification(errNotificationCallback);

        /** initialize the communications */
        comms.initComms(null, inetAddress, uhuSadlManager, pipeComms);

        // init the comms manager for sadl
        uhuSadlManager.initSadlManager(comms);

        return comms;
    }
}
