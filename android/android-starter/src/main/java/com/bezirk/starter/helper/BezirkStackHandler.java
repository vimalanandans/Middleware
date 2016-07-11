package com.bezirk.starter.helper;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.BezirkCommsAndroid;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.device.BezirkDevice;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.proxy.android.ProxyForZirks;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubBroker;
//import com.bezirk.rest.CommsRestController;
//import com.bezirk.rest.HttpComms;
import com.bezirk.sphere.api.BezirkDevMode;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.BezirkSphereForAndroid;
import com.bezirk.starter.MainService;
import com.bezirk.starter.BezirkPreferences;
import com.bezirk.starter.BezirkWifiManager;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;

/**
 * Handles all bezirk stack control operations requested  by Main Zirk
 */
public final class BezirkStackHandler implements com.bezirk.starter.BezirkStackHandler {
    private static final Logger logger = LoggerFactory.getLogger(BezirkStackHandler.class);

    private static Boolean startedStack = false;
    private static Boolean stoppedStack;

    /**
     * communication interface to send and receive the data
     */
    private static BezirkComms comms;

    private final BezirkSphereHandler sphereProcessorForMainService = new BezirkSphereHandler();

    private final BezirkAndroidNetworkUtil androidNetworkUtil = new BezirkAndroidNetworkUtil();

    private final BezirkDeviceHelper bezirkDeviceHelper = new BezirkDeviceHelper();

    private final ProxyForZirks proxy;

    private final CommsNotification errNotificationCallback;
    private final BezirkWifiManager bezirkWifiManager;
    private final BezirkStartStackHelper bezirkStartStackHelper;
    private RegistryPersistence registryPersistence;

    public BezirkStackHandler(ProxyForZirks proxy, CommsNotification errorNotificationCallback) {
        this.proxy = proxy;
        this.errNotificationCallback = errorNotificationCallback;
        this.bezirkWifiManager = BezirkWifiManager.getInstance();
        this.bezirkStartStackHelper = new BezirkStartStackHelper();
    }

    /**
     * @return sphereForAndroid
     */
    public static BezirkSphereAPI getSphereForAndroid() {
        return BezirkSphereHandler.sphereForAndroid;
    }

    public static BezirkDevMode getDevMode() {
        return BezirkSphereHandler.devMode;
    }

    /**
     * @return commsManager
     */
    public static BezirkComms getBezirkComms() {

        return comms;
    }

    public static boolean isStackStarted() {
        return startedStack;
    }

    public static Boolean isStackStopped() {
        return stoppedStack;
    }

    /**
     * Starts all layers of Bezirk Stack
     *
     * @param service MainService
     */
    @Override
    public void startStack(MainService service) {
        synchronized (this) {

            WifiManager wifi;
            if (!com.bezirk.starter.helper.BezirkStackHandler.isStackStarted()) {
                // Need to acquire wifi zirk as every time u start the stack, pick the new connected wifi access point information.
                wifi = (WifiManager) service.getSystemService(Context.WIFI_SERVICE);
                if (bezirkStartStackHelper.isWifiEnabled(wifi)) {
                    //means wifi is enabled..
                    bezirkStartStackHelper.acquireWifiLock(wifi);

                    logger.info("Bezirk zirk start triggered \n");

                    /*************************************************************
                     * Step 0 : Register to BroadCastListener for Wifi           *
                     * change events                                             *
                     *************************************************************/
                    WifiInfo wifiInfo = wifi.getConnectionInfo();
                    //set the Wifi you have connected to ::
                    bezirkWifiManager.setConnectedWifiSSID(wifiInfo.getSSID());

                    //If Wifi is not enabled send a notification to user.

                    logger.debug(" BezirkWifi getSupplicant State :: " + wifiInfo.getSupplicantState().name());

                    /*************************************************************
                     * Step 1 : Fetches ipAddress from Wifi connection           *
                     *************************************************************/
                    if (!bezirkStartStackHelper.isIPAddressValid(service, wifi, androidNetworkUtil))
                        return;
                    Toast.makeText(service.getApplicationContext(), "Starting Bezirk....", Toast.LENGTH_SHORT).show();


                    /*************************************************************
                     * Step 2 : Set Android callback zirk                     *
                     *************************************************************/
                    bezirkStartStackHelper.setAndroicallback(service);

                    /*************************************************************
                     * Step 3 :  Initialize BezirkCommsForAndroid with preferences  *
                     *************************************************************/
                    BezirkPreferences preferences = new BezirkPreferences(service);
                    BezirkCommsAndroid.init(preferences);

                    /*************************************************************
                     * Step 4 : Initialize Registry Persistence                  *
                     *************************************************************/
                    registryPersistence = bezirkStartStackHelper.initializeRegistryPersistence(service);

                    /*************************************************************
                     * Step 5 : Initialize PubSubBroker and set sadl for proxy *
                     *************************************************************/
                    PubSubBroker pubSubBroker = new PubSubBroker(registryPersistence);
                    proxy.setSadlRegistry(pubSubBroker);

                    /*************************************************************
                     * Step 6 : Initialize BezirkCommsManager                       *
                     *************************************************************/
                    InetAddress inetAddress = androidNetworkUtil.fetchInetAddress(service);
                    comms = bezirkStartStackHelper.initializeComms(inetAddress, pubSubBroker, proxy, errNotificationCallback);
                    if (!BezirkValidatorUtility.isObjectNotNull(comms)) {
                        logger.error("Unable to initialize comms layer. Shutting down bezirk.");
                        service.stopSelf();
                    }
                    /*************************************************************
                     * Step 7 : Configure BezirkDevice with preferences             *
                     *************************************************************/
                    BezirkDevice bezirkDevice = bezirkDeviceHelper.setBezirkDevice(preferences, service);

                    /*************************************************************
                     * Step 8 : Initialize BezirkSphere                             *
                     *************************************************************/
                    if (BezirkValidatorUtility.isObjectNotNull(bezirkDevice) && !sphereProcessorForMainService.initSphere(bezirkDevice, service, registryPersistence, preferences)) {
                        // at the moment the init sphere fails due to persistence. hence delete it
                        // quickfix.delete the database
                        logger.error("delete DB");
                        stopStack(service);
                        service.stopSelf();
                        logger.error("Shutting down the bezirk");
                        // don't proceed further without initiating the sphere
                        return;
                    }

                    /*************************************************************
                     * Step 9 : Start CommsConfigurations after sphere initialization       *
                     *************************************************************/
                    comms.startComms();

                    /*************************************************************
                     * Step 10 : Display "BEZIRK ON" notification                   *
                     *************************************************************/
                    int FOREGROUND_ID = 1336;
                    service.startForeground(FOREGROUND_ID,
                            service.buildForegroundNotification("Bezirk ON"));
                    com.bezirk.starter.helper.BezirkStackHandler.stoppedStack = false;
                    com.bezirk.starter.helper.BezirkStackHandler.startedStack = true;
                } else {
                    logger.debug("Disconnected from network!!!");
                    Toast.makeText(service.getApplicationContext(),
                            "You have to be connected to a network to start using Bezirk!!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Stops BezirkStack
     *
     * @param service MainService
     */
    public void stopStack(MainService service) {

        if (!stoppedStack) {
            logger.info("BezirkStarter has stopped\n");

            if (comms != null) {
                comms.stopComms();
                comms.closeComms();
                comms = null;
            }

            // deinit the sphere
            sphereProcessorForMainService.deinitSphere();

            //Set status of stack
            this.stoppedStack = true;
            com.bezirk.starter.helper.BezirkStackHandler.startedStack = false;
            // Close the zirk for testing quick fix
            service.stopSelf();

            //close the wifi receiver
            try {
                service.unregisterReceiver(service.getBroadcastReceiver());
            } catch (IllegalArgumentException e) {

                // android does not provide api to find this, hence handling this in a try catch!!
                logger.error("Receiver was not registered!!!", e);
            }


        }
    }

    /**
     * Restarts bezirk zirk by stopping the stack and starting again.
     *
     * @param service MainService
     */
    void reboot(MainService service) {
        //display in long period of time
        Toast.makeText(service.getApplicationContext(), "Bezirk is Rebooting", Toast.LENGTH_LONG).show();
        long startTime = new Date().getTime();
        if (!stoppedStack) {
            stopStack(service);
        }
        startStack(service);
        long endTime = new Date().getTime();
        long totalTime = endTime - startTime;
        logger.info(">>" + "Reboot-Time:" + totalTime + "<<");
    }

    /* Send the diagnosis ping to comms */
    void diagPing(Intent intent) {
        MessageLedger msgLedger = new MessageLedger();

        msgLedger.setMsg(intent.getStringExtra("MSG"));

        String msgType = intent.getStringExtra("MSG_TYPE");

        if ("PING".equals(msgType)) {// sent the multicast

            msgLedger.setRecipient(null);
        } else {
            String deviceId = intent.getStringExtra("ADDRESS");

            msgLedger.setRecipient(new BezirkZirkEndPoint(deviceId, new ZirkId("DIAG")));

        }
        comms.sendMessage(msgLedger);
    }

    /* clear the persistence */
    void clearPersistence(MainService service) {

        if (BezirkValidatorUtility.isObjectNotNull(registryPersistence)) {
            try {
                registryPersistence.clearPersistence();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            Toast.makeText(service.getApplicationContext(), "Clear Data Process failed", Toast.LENGTH_SHORT).show();
        }

        // re-init sphere so that it updates cleared data
        new Thread(new Runnable() {
            @Override
            public void run() {
                ((BezirkSphereForAndroid) BezirkSphereHandler.sphereForAndroid).initSphere(registryPersistence, comms);
            }
        }).start();
        Toast.makeText(service.getApplicationContext(), "Bezirk Data Cleared", Toast.LENGTH_SHORT).show();
    }

    void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory() && !deleteDir(dir)) {

                logger.error("Error in deleting cache.");
            }
        } catch (Exception e) {

            logger.error("Error in deleting cache.", e);
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String childDir : children) {
                boolean success = deleteDir(new File(dir, childDir));
                if (!success) {
                    return false;
                }
            }
        }
        try {
            return dir != null && dir.delete();
        } catch (Exception e) {

            logger.error("Directory is not deleted.", e);
            return false;
        }

    }

    @Override
    public void restartComms() {
        comms.restartComms();
    }

    @Override
    public void startStopRestServer(int startStopStatus) {
        //if http feature is enabled, start the httpServer.//http server instance.
    /*    HttpComms httpServer = new CommsRestController();

        if (startStopStatus == 100) {
            httpServer.startHttpComms();
        } else if (startStopStatus == 101) {
            httpServer.stopHttpComms();
        }


        logger.debug("Started HTTP Server.. ");
        */
    }
}