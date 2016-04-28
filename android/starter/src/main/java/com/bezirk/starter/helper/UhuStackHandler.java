package com.bezirk.starter.helper;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.UhuCommsAndroid;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.device.BezirkDevice;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.proxy.android.ProxyforServices;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.rest.CommsRestController;
import com.bezirk.rest.HttpComms;
import com.bezirk.sadl.BezirkSadlManager;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.impl.BezirkSphereForAndroid;
import com.bezirk.starter.IUhuStackHandler;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuPreferences;
import com.bezirk.starter.UhuWifiManager;
import com.bezirk.util.BezirkValidatorUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.util.Date;

/**
 * Handles all uhu stack control operations requested  by Main Zirk
 * <p/>
 * Created by AJC6KOR on 9/8/2015.
 */
public final class UhuStackHandler implements IUhuStackHandler {
    private static final Logger logger = LoggerFactory.getLogger(UhuStackHandler.class);

    private static Boolean startedStack = false;
    private static Boolean stoppedStack;

    /**
     * communication interface to send and receive the data
     */
    private static IUhuComms comms;

    private final UhuSphereHandler sphereProcessorForMainService = new UhuSphereHandler();

    private final UhuAndroidNetworkUtil androidNetworkUtil = new UhuAndroidNetworkUtil();

    private final UhuDeviceHelper uhuDeviceHelper = new UhuDeviceHelper();

    private final ProxyforServices proxy;

    private final CommsNotification errNotificationCallback;
    private final UhuWifiManager uhuWifiManager;
    private final UhuStartStackHelper uhuStartStackHelper;
    private RegistryPersistence registryPersistence;

    public UhuStackHandler(ProxyforServices proxy, CommsNotification errorNotificationCallback) {
        this.proxy = proxy;
        this.errNotificationCallback = errorNotificationCallback;
        this.uhuWifiManager = UhuWifiManager.getInstance();
        this.uhuStartStackHelper = new UhuStartStackHelper();
    }

    /**
     * @return sphereForAndroid
     */
    public static BezirkSphereAPI getSphereForAndroid() {
        return UhuSphereHandler.sphereForAndroid;
    }

    public static IUhuDevMode getDevMode() {
        return UhuSphereHandler.devMode;
    }

    /**
     * @return commsManager
     */
    public static IUhuComms getUhuComms() {

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
            if (!UhuStackHandler.isStackStarted()) {
                // Need to acquire wifi zirk as every time u start the stack, pick the new connected wifi access point information.
                wifi = (WifiManager) service.getSystemService(Context.WIFI_SERVICE);
                if (uhuStartStackHelper.isWifiEnabled(wifi)) {
                    //means wifi is enabled..
                    uhuStartStackHelper.acquireWifiLock(wifi);

                    logger.info("Bezirk zirk start triggered \n");

                    /*************************************************************
                     * Step 0 : Register to BroadCastListener for Wifi           *
                     * change events                                             *
                     *************************************************************/
                    WifiInfo wifiInfo = wifi.getConnectionInfo();
                    //set the Wifi you have connected to ::
                    uhuWifiManager.setConnectedWifiSSID(wifiInfo.getSSID());

                    //If Wifi is not enabled send a notification to user.

                    logger.debug("UhuWifi getSupplicant State :: " + wifiInfo.getSupplicantState().name());

                    /*************************************************************
                     * Step 1 : Fetches ipAddress from Wifi connection           *
                     *************************************************************/
                    if (!uhuStartStackHelper.isIPAddressValid(service, wifi, androidNetworkUtil))
                        return;
                    Toast.makeText(service.getApplicationContext(), "Starting Bezirk....", Toast.LENGTH_SHORT).show();


                    /*************************************************************
                     * Step 2 : Set Android callback zirk                     *
                     *************************************************************/
                    uhuStartStackHelper.setAndroicallback(service);

                    /*************************************************************
                     * Step 3 :  Initialize UhuCommsForAndroid with preferences  *
                     *************************************************************/
                    UhuPreferences preferences = new UhuPreferences(service);
                    UhuCommsAndroid.init(preferences);

                    /*************************************************************
                     * Step 4 : Initialize Registry Persistence                  *
                     *************************************************************/
                    registryPersistence = uhuStartStackHelper.initializeRegistryPersistence(service);

                    /*************************************************************
                     * Step 5 : Initialize BezirkSadlManager and set sadl for proxy *
                     *************************************************************/
                    BezirkSadlManager bezirkSadlManager = new BezirkSadlManager(registryPersistence);
                    proxy.setSadlRegistry(bezirkSadlManager);

                    /*************************************************************
                     * Step 6 : Initialize BezirkCommsManager                       *
                     *************************************************************/
                    InetAddress inetAddress = androidNetworkUtil.fetchInetAddress(service);
                    comms = uhuStartStackHelper.initializeComms(inetAddress, bezirkSadlManager, proxy, errNotificationCallback);
                    if (!BezirkValidatorUtility.isObjectNotNull(comms)) {
                        logger.error("Unable to initialize comms layer. Shutting down uhu.");
                        service.stopSelf();
                    }
                    /*************************************************************
                     * Step 7 : Configure BezirkDevice with preferences             *
                     *************************************************************/
                    BezirkDevice bezirkDevice = uhuDeviceHelper.setUhuDevice(preferences, service);

                    /*************************************************************
                     * Step 8 : Initialize BezirkSphere                             *
                     *************************************************************/
                    if (BezirkValidatorUtility.isObjectNotNull(bezirkDevice) && !sphereProcessorForMainService.initSphere(bezirkDevice, service, registryPersistence, preferences)) {
                        // at the moment the init sphere fails due to persistence. hence delete it
                        // quickfix.delete the database
                        logger.error("delete DB");
                        stopStack(service);
                        service.stopSelf();
                        logger.error("Shutting down the uhu");
                        // don't proceed further without initiating the sphere
                        return;
                    }

                    /*************************************************************
                     * Step 9 : Start BezirkComms after sphere initialization       *
                     *************************************************************/
                    comms.startComms();

                    /*************************************************************
                     * Step 10 : Display "UHU ON" notification                   *
                     *************************************************************/
                    int FOREGROUND_ID = 1336;
                    service.startForeground(FOREGROUND_ID,
                            service.buildForegroundNotification("Bezirk ON"));
                    UhuStackHandler.stoppedStack = false;
                    UhuStackHandler.startedStack = true;
                } else {
                    logger.debug("Disconnected from network!!!");
                    Toast.makeText(service.getApplicationContext(), "You have to be connected to a network to start using UhU!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Stops UhuStack
     *
     * @param service MainService
     */
    public void stopStack(MainService service) {

        if (!stoppedStack) {
            logger.info("UhuStarter has stopped\n");

            if (comms != null) {
                comms.stopComms();
                comms.closeComms();
                comms = null;
            }

            // deinit the sphere
            sphereProcessorForMainService.deinitSphere();

            //Set status of stack
            this.stoppedStack = true;
            UhuStackHandler.startedStack = false;
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
     * Restarts uhu zirk by stopping the stack and starting again.
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

            msgLedger.setRecipient(new BezirkZirkEndPoint(deviceId, new BezirkZirkId("DIAG")));

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
                ((BezirkSphereForAndroid) UhuSphereHandler.sphereForAndroid).initSphere(registryPersistence, comms);
            }
        }).start();
        Toast.makeText(service.getApplicationContext(), "Bezirk Data Cleared", Toast.LENGTH_SHORT).show();
    }

    /**
     * Deletes context cache
     *
     * @param context
     */
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
        HttpComms httpServer = new CommsRestController();

        if (startStopStatus == 100) {
            httpServer.startHttpComms();
        } else if (startStopStatus == 101) {
            httpServer.stopHttpComms();
        }


        logger.debug("Started HTTP Server.. ");
    }
}