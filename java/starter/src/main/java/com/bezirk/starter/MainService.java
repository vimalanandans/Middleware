//package com.bezirk.starter;
//
//import com.bezirk.comms.Comms;
//import com.bezirk.comms.CommsFactory;
//import com.bezirk.comms.CommsNotification;
//import com.bezirk.comms.ZyreCommsManager;
//import com.bezirk.control.messages.MessageLedger;
//import com.bezirk.datastorage.RegistryStorage;
//import com.bezirk.device.Device;
//import com.bezirk.comms.CommsFeature;
//import com.bezirk.datastorage.ProxyPersistence;
//import com.bezirk.proxy.ProxyServer;
//import com.bezirk.proxy.MessageHandler;
//import com.bezirk.pubsubbroker.PubSubBroker;
//import com.bezirk.sphere.api.SphereSecurity;
//import com.bezirk.sphere.api.SphereServiceAccess;
//import com.bezirk.ui.remotelogging.RemoteLogSphereSelectGUI;
//import com.bezirk.datastorage.DatabaseConnection;
//import com.bezirk.sphere.api.SphereAPI;
//import com.bezirk.streaming.StreamManager;
//import com.bezirk.streaming.Streaming;
//import com.bezirk.util.ValidatorUtility;
//import com.bezirk.networking.NetworkManager;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.net.InetAddress;
//import java.net.NetworkInterface;
//import java.util.Date;
//
///**
// * MainService for bezirk-pc which controls the bezirk stack
// */
//public class MainService {
//    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
//    private static final String DB_VERSION = "0.0.3";
//    /**
//     * Max value for the notification
//     */
//    private static final int MAX_ERROR_REPEAT_COUNT = 100;
//    private final ProxyServer proxyServer;
//    private final NetworkUtil networkUtil = new NetworkUtil();
//    private final ServiceStarterHelper serviceStarterHelper = new ServiceStarterHelper();
//    SphereAPI sphereManager;
//    /**
//     * List of configurations
//     */
//    DataPathConfig bezirkConfig;
//    // Booleans to start and stop stack gracefully
//    private Boolean startedStack = false;
//    private Boolean stoppedStack = false;
//    private RegistryStorage registryPersistence;
//    // Logging GUI
//    private RemoteLogSphereSelectGUI loggingGUI;
//    private com.bezirk.ui.spheremanagement.SphereManagementGUI frame;
//    /**
//     * communication interface to send and receive the data
//     */
//    private Comms comms;
//    /**
//     * Keeps track of no of error messages notified. Each time a notification is
//     * received, its value is incremented and after reaching
//     * MAX_ERROR_REPEAT_COUNT the value is reset to 0 and notified.
//     */
//    private int errorCallbackCount = -1;
//    /**
//     * Callback interface to display the Version mismatch UI if stack receives a
//     * message version that is not compatible. This will be shown only once when
//     * it receives for the first time.
//     */
//    private final CommsNotification errNotificationCallback = new CommsNotification() {
//
//        /**
//         * Display warning if bezirk version is mismatching.
//         */
//        @Override
//        public void versionMismatch(final String misMatchVersionId) {
//            logger.info("mismatch version " + misMatchVersionId);
//            if ((++errorCallbackCount) % MAX_ERROR_REPEAT_COUNT == 0) {
//                if (bezirkConfig.isDisplayEnabled()) {
//                    if (frame != null) {
//                        frame.showWarningIcon(true, misMatchVersionId);
//                    }
//                } else {
//                    logger.error("Mismatch in Bezirk Version, Check the versions of all the devices");
//                }
//            }
//        }
//
//        @Override
//        public void diagMsg(MessageLedger msg) {
//            // TODO:
//            // handle ping message and reply pong. check android code
//            logger.info("diag UI and response are not implemented in bezirk build");
//        }
//
//        @Override
//        public void handleError(String errorMsg) {
//            // TODO Auto-generated method stub
//            logger.info("Comms UI Error" + errorMsg);
//        }
//    };
//
//    /**
//     * Configure proxy and <code>bezirkConfig</code> for main zirk
//     *
//     * @param proxyServer
//     * @param bezirkConfigRef
//     */
//    public MainService(final ProxyServer proxyServer,
//                       final DataPathConfig bezirkConfigRef) {
//
//        this.proxyServer = proxyServer;
//
//        bezirkConfig = bezirkConfigRef;
//
//        /** get the config */
//        if (bezirkConfig == null) {
//            logger.debug("unable to find the bezirk config. using default values. check bezirk.xml");
//            this.bezirkConfig = new DataPathConfig();
//        }
//
//    }
//
//    /**
//     * @return stack status
//     */
//    public Boolean getStartedStack() {
//        return startedStack;
//    }
//
//    /**
//     * Stop BeirkStack
//     */
//    public void stopStack() {
//        logger.info("BezirkStarter has stopped\n");
//
//        /*************************************
//         * Step1 : Stop Comms                *
//         *************************************/
//        if (comms != null) {
//            comms.stopComms();
//            comms.closeComms();
//        }
//
//        /*************************************
//         * Step2 : Deinit sphere             *
//         *************************************/
//        serviceStarterHelper.deinitSphere(this);
//
//        //TODO Manage the remote loggin ui inside ui component
//        /*************************************
//         * Step3 : Shutdown RemoteLogging    *
//         *************************************/
//        /*if (CommsConfigurations.isRemoteLoggingServiceEnabled()
//                && ValidatorUtility.isObjectNotNull(loggingGUI)
//                && loggingGUI.isVisible()) {
//            loggingGUI.shutGUI();
//        }*/
//        /*************************************
//         * Step4 : Set status of stack       *
//         *************************************/
//        this.stoppedStack = true;
//        startedStack = false;
//    }
//
//    /**
//     * Start BezirkStack. Initializes sadl, sphere, comms.
//     *
//     * @param messageHandler
//     */
//    public void startStack(final MessageHandler messageHandler) {
//        logger.info("BezirkStarter has started");
//
//
//
//        /**************************************************
//         * Step1 : Determine NetworkInterface             *
//         **************************************************/
//
//        NetworkInterface intf = null;
//        try {
//            intf = networkUtil.fetchNetworkInterface(this.bezirkConfig);
//        } catch (Exception e) {
//            serviceStarterHelper.fail("Error in fetching interface name", e);
//        }
//
//        /**************************************************
//         * Step2 : Initialize Registry Persistence        *
//         **************************************************/
//        initializeRegistryPersistence();
//
//        /**************************************************
//         * Step3 :Create and configure the Device      *
//         **************************************************/
//        final Device bezirkDevice = serviceStarterHelper
//                .configureBezirkDevice(this.bezirkConfig);
//
//        /**************************************************
//         * Step4 : Create PubSubBroker                  *
//         **************************************************/
//        final PubSubBroker pubSubBroker = new PubSubBroker(
//                registryPersistence, bezirkDevice);
//
//        // Inject to proxyServer
//        proxyServer.setPubSubBrokerService(pubSubBroker);
//
//        /**************************************************
//         * Step5 :Initialize the comms.                   *
//         **************************************************/
//        final boolean isCommsInitialized = initComms(messageHandler, intf,
//                pubSubBroker);
//        if (!isCommsInitialized) {
//            serviceStarterHelper.fail("Problem initializing Comms.", null);
//        }
//
//
//        /**************************************************
//         * Step6 :Initialize sphere                       *
//         **************************************************/
//
//        if (ValidatorUtility.isObjectNotNull(bezirkDevice)) {
//
//          /*  sphereManager = serviceStarterHelper.initSphere(bezirkDevice,
//                    registryPersistence, comms);
//            if (!ValidatorUtility.isObjectNotNull(sphereManager)) {
//                serviceStarterHelper.fail("Problem initializing sphere.", null);
//            }
//            */
//
//        }
//
//        // init the comms manager for pubsubbroker
//        // pubSubBroker.initPubSubBroker(comms,  messageHandler,
//        //       (SphereServiceAccess) sphereManager,(SphereSecurity) sphereManager);
//        pubSubBroker.initPubSubBroker(comms,  messageHandler, null, null);
//
//
//        /**************************************************
//         * Step7 :Display or save Share sphere QR code    *
//         * Enable LoggingGUI                              *
//         **************************************************/
//        displayQRCode(bezirkDevice);
//
//        /**************************************************
//         * Step10 : Set status of stack                   *
//         **************************************************/
//        this.stoppedStack = false;
//        this.startedStack = true;
//    }
//
//    private void displayQRCode(final Device bezirkDevice) {
//        if (bezirkConfig.isDisplayEnabled()) {
//            // commented to test in beaglebone. uncomment it for PC
//            //frame = new com.bezirk.ui.spheremanagement.SphereManagementGUI(sphereManager, bezirkDevice);
//            frame = new com.bezirk.ui.spheremanagement.SphereManagementGUI(null, bezirkDevice);
//            frame.setVisible(true);
//
//            //FIXME: check for the remote logging and launch the ui
//            // Check if the Logging is enabled and start the LoggingGUI
////            if (CommsConfigurations.isRemoteLoggingServiceEnabled()) {
////                logger.info("*** REMOTE LOGGING SERVICE IS ENABLED");
////                SwingUtilities.invokeLater(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        loggingGUI = new RemoteLogSphereSelectGUI(comms);
////                    }
////                });
////            }
////        } else {
////            /**
////             * Rishabh: Commenting out saving of QR as it is not used.
////             */
////            // save the qr code
////            // if display is not stored then save the QR code
////            //((PCSphereServiceManager) sphereManager).saveQRCode(bezirkConfig.getDataPath(),
////            //        bezirkDevice.getDeviceName());
//        }
//    }
//
//    /**
//     * Restarts bezirk stack
//     */
//    public void reboot() {
//        // display in long period of time
//        final long startTime = new Date().getTime();
//        if (!this.stoppedStack) {
//            this.stopStack();
//        }
//        this.startStack(null);
//        final long endTime = new Date().getTime();
//        final long totalTime = endTime - startTime;
//        logger.info(">>" + "Reboot-Time:" + totalTime + "<<");
//    }
//
//    /**
//     * @return registryPersistence
//     */
//    public ProxyPersistence getBezirkProxyPersistence() {
//        return registryPersistence;
//    }
//
//    private void initializeRegistryPersistence() {
//        final DatabaseConnection dbConnection = new com.bezirk.persistence.DatabaseConnectionForJava(
//                bezirkConfig.getDataPath());
//        try {
//            registryPersistence = new RegistryStorage(dbConnection,
//                    DB_VERSION);
//        } catch (Exception e1) {
//            logger.error("Error in loading Registry Persistence from:"
//                    + bezirkConfig.getDataPath(), e1);
//            System.exit(0);
//        }
//    }
//
//    private boolean initComms(final MessageHandler bezirkPcCallback,
//                              final NetworkInterface intf, final PubSubBroker pubSubBroker) {
//
//        CommsFactory commsFactory = new CommsFactory();
//
//        // comms zyre jni is injected from platform specific code
//        if (commsFactory.getActiveComms() == CommsFeature.COMMS_ZYRE_JNI) {
//            comms = new ZyreCommsManager();
//        } else {
//            //rest of the comms are returned from factory
//            /** Initialize the comms. */
//            comms = new CommsFactory().getComms();
//        }
//
//        /** initialize the communications */
//        /*
//         * VERY IMP NOTE: Always setup the Notification Callback first then init
//         * the comms as comms also gives this callback to the listeners
//         */
//        comms.registerNotification(errNotificationCallback);
//
//        /* comms triggers sadle send this data.
//         * try {
//            ((Comms) comms).setBezirkCallback(bezirkPcCallback);
//        } catch (Exception e) {
//            logger.error("Unable to set bezirk callback for the comms.", e);
//            return false;
//        }
//        */
//        final InetAddress addr = NetworkManager.getIpForInterface(intf);
//
//        /*
//         * CommsProperties is not used by comms manager. Properties are handled
//         * by BezirkCommsPC
//         */
//        // Streaming streamManager = new StreamManager(comms, pubSubBroker, getStreamDownloadPath());
//        //comms.initComms(null, addr, pubSubBroker, (SphereSecurity) sphereManager, streamManager);
//        comms.initComms(null, addr, pubSubBroker, (SphereSecurity) null, null);
//
//
//
//        comms.startComms();
//
//        // the comms manager for the proxy
//        //proxyServer.setComms(comms);
//
//        // Set RTC Signalling for streaming
//
//        // FIXME: Throwing some errors on comms object
//        /* SignalingFactory.createSignalingInstance(
//                "com.bosch.upa.uhu.rtc.streaming.Signaling", comms);
//         	*/
//
//
//        return true;
//    }
//
//    String getStreamDownloadPath()
//    {
//        String downloadPath;
//        // port factory is part of comms manager
//        // CommsConfigurations.portFactory = new
//        // StreamPortFactory(CommsConfigurations.STARTING_PORT_FOR_STREAMING,
//        // CommsConfigurations.ENDING_PORT_FOR_STREAMING); // initialize the
//        // StreamPortFactory
//        if (bezirkConfig == null) {
//            downloadPath= File.separator + "downloads" + File.separator;
//        } else {
//            downloadPath= File.separator + bezirkConfig.getDataPath() + File.separator;
//        }
//        final File createDownloadFolder = new File(
//                downloadPath);
//        if (!createDownloadFolder.exists()) {
//            if (!createDownloadFolder.mkdir()) {
//                logger.error("Failed to create download direction: {}",
//                        createDownloadFolder.getAbsolutePath());
//            }
//        }
//        return downloadPath;
//    }
//}