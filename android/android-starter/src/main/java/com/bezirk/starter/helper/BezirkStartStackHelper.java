package com.bezirk.starter.helper;

import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CommsFactory;
import com.bezirk.comms.CommsFeature;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.datastorage.DatabaseConnection;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.networking.NetworkManager;
import com.bezirk.persistence.DatabaseConnectionForAndroid;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.starter.MainService;
import com.bezirk.streaming.StreamManager;
import com.bezirk.util.ValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

class BezirkStartStackHelper {
    private static final Logger logger = LoggerFactory.getLogger(BezirkStartStackHelper.class);

    boolean isWifiEnabled(WifiManager wifi) {
        return wifi != null && wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    RegistryStorage initializeRegistryPersistence(MainService service) {
        DatabaseConnection dbConnection = new DatabaseConnectionForAndroid(service);
        String DB_VERSION = "0.0.4";
        RegistryStorage registryPersistence = null;

        try {
            registryPersistence = new RegistryStorage(dbConnection, DB_VERSION);
        } catch (Exception e1) {
            logger.error(e1.getMessage(), e1);
        }
        return registryPersistence;
    }

    void acquireWifiLock(WifiManager wifi) {
        try {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("Log_Tag");
            lock.acquire();
        } catch (UnsupportedOperationException e) {
            logger.error("UnsupportedOperationException thrown while acquiring lock", e);
        }
    }

    boolean isIPAddressValid(MainService service, WifiManager wifi) {
        String ipAddress;
        try {
            ipAddress = getIpAddress(wifi);
        } catch (UnknownHostException e) {
            logger.error("Unable to get ip address. Is it connected to network", e);
            Toast.makeText(service.getApplicationContext(), "Unable to get ip address. Is it connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (ValidatorUtility.checkForString(ipAddress)) {
            logger.info("Wifi Connected to " + wifi.getConnectionInfo().getSSID());
        } else {
            logger.error("Unable to get ip address. Is it connected to network");
            Toast.makeText(service.getApplicationContext(), "Unable to get ip address. Is it connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getIpAddress(WifiManager wifi) throws UnknownHostException {
        // From http://stackoverflow.com/questions/16730711/get-my-wifi-ip-address-android
        int ip = wifi.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endian if needed
        if (ByteOrder.LITTLE_ENDIAN.equals(ByteOrder.nativeOrder())) {
            ip = Integer.reverseBytes(ip);
        }

        byte[] ipByteArray = BigInteger.valueOf(ip).toByteArray();

        return InetAddress.getByAddress(ipByteArray).getHostAddress();
    }


    Comms initializeComms(InetAddress inetAddress, PubSubBroker pubSubBroker, CommsNotification errNotificationCallback, NetworkManager networkManager) {
        // Instantiate pipeManager before SenderThread so that it is ready to start sending over pipes
        // PipeManager pipeComms = PipeCommsFactory.createPipeComms();

        CommsFactory commsFactory = new CommsFactory();

        Comms comms;

        // comms zyre jni is injected from platform specific code
        if (commsFactory.getActiveComms() == CommsFeature.COMMS_ZYRE_JNI) {
            String arch = System.getProperty("os.arch");
            logger.info("phone arch " + arch);
            comms = new ZyreCommsManager(networkManager);
        } else {
            //rest of the comms are returned from factory
            /** Initialize the comms. */
            comms = new CommsFactory().getComms();
        }


        // the comms manager for the proxy
        //proxy.setComms(comms);

        //init the error notification
        comms.registerNotification(errNotificationCallback);

        /** initialize the streaming */
        // StreamManager streaming = new StreamManager(comms, pubSubBroker,getStreamDownloadPath());
        /** initialize the communications */
        //  comms.initComms(null, inetAddress, pubSubBroker, null, streaming);
        comms.initComms(null, inetAddress,  null, null);


        return comms;
    }

    String getStreamDownloadPath() {
        String downloadPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bezirk-downloads/";

        // create a Downloads folder for streaming
        File createDownloadFolder = new File(downloadPath);
        if (!createDownloadFolder.exists()) {
            if (!createDownloadFolder.mkdir()) {
                logger.error("Failed to create download direction: {}",
                        createDownloadFolder.getAbsolutePath());
            }
        }
        return downloadPath;
    }

}
