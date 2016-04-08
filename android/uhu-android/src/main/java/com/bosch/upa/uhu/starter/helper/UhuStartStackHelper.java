package com.bosch.upa.uhu.starter.helper;

import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.CommsFactory;
import com.bosch.upa.uhu.comms.ICommsNotification;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bezirk.comms.ZyreCommsManager;
import com.bosch.upa.uhu.features.CommsFeature;
import com.bosch.upa.uhu.messagehandler.AndroidServiceMessageHandler;
import com.bosch.upa.uhu.persistence.IDatabaseConnection;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.persistence.util.DatabaseConnectionForAndroid;
import com.bosch.upa.uhu.pipe.android.PipeCommsFactory;
import com.bosch.upa.uhu.pipe.core.PipeManager;
import com.bosch.upa.uhu.proxy.android.ProxyforServices;
import com.bosch.upa.uhu.sadl.UhuSadlManager;
import com.bosch.upa.uhu.starter.MainService;
import com.bosch.upa.uhu.util.UhuValidatorUtility;

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
        String DB_VERSION = "0.0.3";
        RegistryPersistence registryPersistence=null;

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

    boolean isIPAddressValid(MainService service,WifiManager wifi,UhuAndroidNetworkUtil androidNetworkUtil) {
        String ipAddress;
        try {
            ipAddress = androidNetworkUtil.getIpAddress(wifi);
        }catch (UnknownHostException e){
            LOGGER.error("Unable to get ip address. Is it connected to network", e);
            Toast.makeText(service.getApplicationContext(), "Unable to get ip address. Is it connected to network", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (UhuValidatorUtility.checkForString(ipAddress)) {
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
            AndroidServiceMessageHandler uhuAndroidCallback = new AndroidServiceMessageHandler(service.getApplicationContext());
            UhuCompManager.setplatformSpecificCallback(uhuAndroidCallback);
        }
    }

    IUhuComms initializeComms(InetAddress inetAddress, UhuSadlManager uhuSadlManager,ProxyforServices proxy,ICommsNotification errNotificationCallback) {
        // Instantiate pipeManager before SenderThread so that it is ready to start sending over pipes
        PipeManager pipeComms = PipeCommsFactory.createPipeComms();

        CommsFactory commsFactory = new CommsFactory();

        IUhuComms comms;

        // comms zyre jni is injected from platform specific code
        if(commsFactory.getActiveComms() == CommsFeature.COMMS_ZYRE_JNI)
        {
            String arch = System.getProperty("os.arch");
            LOGGER.info("phone arch "+arch);
            comms = new ZyreCommsManager();
        }
        else{
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
