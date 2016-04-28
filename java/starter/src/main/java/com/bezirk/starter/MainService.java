package com.bezirk.starter;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.comms.CommsFactory;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.ZyreCommsManager;
import com.bezirk.control.messages.MessageLedger;
import com.bezirk.device.BezirkDevice;
import com.bezirk.features.CommsFeature;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.persistence.DatabaseConnection;
import com.bezirk.persistence.BezirkProxyPersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.util.BezirkValidatorUtility;
import com.bezrik.network.UhuNetworkUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;

import javax.swing.SwingUtilities;

/**
 * MainService for uhu-pc which controls the uhu stack
 */
public class MainService {
    private static final Logger logger = LoggerFactory
            .getLogger(MainService.class);
    private static final String DB_VERSION = "0.0.3";
    /**
     * Max value for the notification
     */
    private static final int MAX_ERROR_REPEAT_COUNT = 100;
    private final com.bezirk.proxy.pc.ProxyforServices proxyforServices;
    private final UhuPCNetworkUtil uhuPcNetworkUtil = new UhuPCNetworkUtil();
    private final ServiceStarterHelper serviceStarterHelper = new ServiceStarterHelper();
    IUhuSphereAPI sphereForPC;
    /**
     * List of configurations
     */
    UhuConfig uhuConfig;
    // Booleans to start and stop stack gracefully
    private Boolean startedStack = false;
    private Boolean stoppedStack = false;
    private RegistryPersistence registryPersistence;
    // Logging GUI
    private com.bezirk.logging.ui.SphereSelectGUI loggingGUI;
    private com.bezirk.sphere.ui.SphereManagementGUI frame;
    /**
     * communication interface to send and receive the data
     */
    private IUhuComms comms;
    /**
     * Keeps track of no of error messages notified. Each time a notification is
     * received, its value is incremented and after reaching
     * MAX_ERROR_REPEAT_COUNT the value is reset to 0 and notified.
     */
    private int errorCallbackCount = -1;
    /**
     * Callback interface to display the Version mismatch UI if stack receives a
     * message version that is not compatible. This will be shown only once when
     * it receives for the first time.
     *
     * @author Vijet Badigannavar
     */
    private final CommsNotification errNotificationCallback = new CommsNotification() {

        /**
         * Display warning if uhu version is mismatching.
         */
        @Override
        public void versionMismatch(final String misMatchVersionId) {
            logger.info("mismatch version " + misMatchVersionId);
            if ((++errorCallbackCount) % MAX_ERROR_REPEAT_COUNT == 0) {
                if (uhuConfig.isDisplayEnabled()) {
                    if (frame != null) {
                        frame.showWarningIcon(true, misMatchVersionId);
                    }
                } else {
                    logger.error("Mismatch in Uhu Version, Check the versions of all the devices");
                }
            }
        }

        @Override
        public void diagMsg(MessageLedger msg) {
            // TODO:
            // handle ping message and reply pong. check android code
            logger.info("diag UI and response are not implemented in uhu build");
        }

        @Override
        public void handleError(String errorMsg) {
            // TODO Auto-generated method stub
            logger.info("Comms UI Error" + errorMsg);
        }
    };

    /**
     * Configure proxy and uhuconfig for main zirk
     *
     * @param proxyforServices
     * @param uhuConfigRef
     */
    public MainService(final com.bezirk.proxy.pc.ProxyforServices proxyforServices,
                       final UhuConfig uhuConfigRef) {

        this.proxyforServices = proxyforServices;

        uhuConfig = uhuConfigRef;

        /** get the config */
        if (uhuConfig == null) {
            logger.debug("unable to find the uhu config. using default values. check uhu.xml");
            this.uhuConfig = new UhuConfig();
        }

    }

    /**
     * @return stack status
     */
    public Boolean getStartedStack() {
        return startedStack;
    }

    /**
     * Stop UhuStack
     */
    public void stopStack() {
        logger.info("UhuStarter has stopped\n");

        /*************************************
         * Step1 : Stop Comms                *
         *************************************/
        if (comms != null) {
            comms.stopComms();
            comms.closeComms();
        }

        /*************************************
         * Step2 : Deinit sphere             *
         *************************************/
        serviceStarterHelper.deinitSphere(this);

        /*************************************
         * Step3 : Shutdown RemoteLogging    *
         *************************************/
        if (BezirkComms.isRemoteLoggingServiceEnabled()
                && BezirkValidatorUtility.isObjectNotNull(loggingGUI)
                && loggingGUI.isVisible()) {

            loggingGUI.shutGUI();
        }
        /*************************************
         * Step4 : Set status of stack       *
         *************************************/
        this.stoppedStack = true;
        startedStack = false;
    }

    /**
     * Start UhuStack. Initializes sadl, sphere, comms.
     *
     * @param uhuPcCallback
     */
    public void startStack(final ZirkMessageHandler uhuPcCallback) {
        logger.info("UhuStarter has started \n");

        /**************************************************
         * Step1 : Set Platform specific call back        *
         **************************************************/
        if (null == UhuCompManager.getplatformSpecificCallback()
                && uhuPcCallback != null) {
            UhuCompManager.setplatformSpecificCallback(uhuPcCallback);
        }

        /**************************************************
         * Step2 : Configure UhuCommsPC                   *
         **************************************************/
        try {
            // Initialize Comms, which reads properties including
            // InterfaceName from config file. Any system properties will
            // override those written in the config file
            com.bezirk.comms.UhuCommsPC.init(uhuConfig);

        } catch (Exception e) {
            serviceStarterHelper.fail("Problem initializing BezirkComms", e);
        }

        /**************************************************
         * Step3 : Determine NetworkInterface             *
         **************************************************/

        NetworkInterface intf = null;
        try {
            intf = uhuPcNetworkUtil.fetchNetworkInterface(this.uhuConfig);
        } catch (Exception e) {
            serviceStarterHelper.fail("Error in fetching interface name", e);
        }

        /**************************************************
         * Step4 : Initialize Registry Persistence        *
         **************************************************/
        initializeRegistryPersistence();

        /**************************************************
         * Step5 : Create UhuSadlManager                  *
         **************************************************/
        final UhuSadlManager uhuSadlManager = new UhuSadlManager(
                registryPersistence);

        // Inject to proxyForServices
        proxyforServices.setSadlRegistry(uhuSadlManager);

        /**************************************************
         * Step6 :Initialize the comms.                   *
         **************************************************/
        final boolean isCommsInitialized = initComms(uhuPcCallback, intf,
                uhuSadlManager);
        if (!isCommsInitialized) {
            serviceStarterHelper.fail("Problem initializing Comms.", null);
        }
        /**************************************************
         * Step7 :Create and configure the BezirkDevice      *
         **************************************************/
        final BezirkDevice bezirkDevice = serviceStarterHelper
                .configureUhuDevice(this.uhuConfig);

        /**************************************************
         * Step8 :Initialize sphere                       *
         **************************************************/

        if (BezirkValidatorUtility.isObjectNotNull(bezirkDevice)) {

            sphereForPC = serviceStarterHelper.initSphere(bezirkDevice,
                    registryPersistence, comms);

            if (!BezirkValidatorUtility.isObjectNotNull(sphereForPC)) {

                serviceStarterHelper.fail("Problem initializing sphere.", null);

            }

        }

        /**************************************************
         * Step9 :Display or save Share sphere QR code    *
         * Enable LoggingGUI                              *
         **************************************************/
        displayQRCode(bezirkDevice);

        /**************************************************
         * Step10 : Set status of stack                   *
         **************************************************/
        this.stoppedStack = false;
        this.startedStack = true;
    }

    private void displayQRCode(final BezirkDevice bezirkDevice) {
        if (uhuConfig.isDisplayEnabled()) {
            // commented to test in beaglebone. uncomment it for PC
            frame = new com.bezirk.sphere.ui.SphereManagementGUI(sphereForPC);
            frame.setVisible(true);

            // Check if the Logging is enabled and start the LoggingGUI
            if (BezirkComms.isRemoteLoggingServiceEnabled()) {
                logger.info("*** REMOTE LOGGING SERVICE IS ENABLED");
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        loggingGUI = new com.bezirk.logging.ui.SphereSelectGUI(comms);
                    }
                });
            }
        } else {
            // save the qr code
            // if display is not stored then save the QR code
            ((com.bezirk.sphere.impl.UhuSphereForPC) sphereForPC).saveQRCode(uhuConfig.getDataPath(),
                    bezirkDevice.getDeviceName());
        }
    }

    /**
     * Restarts uhu stack
     */
    public void reboot() {
        // display in long period of time
        final long startTime = new Date().getTime();
        if (!this.stoppedStack) {
            this.stopStack();
        }
        this.startStack(null);
        final long endTime = new Date().getTime();
        final long totalTime = endTime - startTime;
        logger.info(">>" + "Reboot-Time:" + totalTime + "<<");
    }

    /**
     * @return registryPersistence
     */
    public BezirkProxyPersistence getUhuProxyPersistence() {
        return registryPersistence;
    }

    private void initializeRegistryPersistence() {
        final DatabaseConnection dbConnection = new com.bezirk.persistence.DatabaseConnectionForJava(
                uhuConfig.getDataPath());
        try {
            registryPersistence = new RegistryPersistence(dbConnection,
                    DB_VERSION);
        } catch (Exception e1) {
            logger.error("Error in loading Registry Persistence from:"
                    + uhuConfig.getDataPath(), e1);
            System.exit(0);
        }
    }

    private boolean initComms(final ZirkMessageHandler uhuPcCallback,
                              final NetworkInterface intf, final UhuSadlManager uhuSadlManager) {

        CommsFactory commsFactory = new CommsFactory();

        // comms zyre jni is injected from platform specific code
        if (commsFactory.getActiveComms() == CommsFeature.COMMS_ZYRE_JNI) {
            comms = new ZyreCommsManager();
        } else {
            //rest of the comms are returned from factory
            /** Initialize the comms. */
            comms = new CommsFactory().getComms();
        }

        /** initialize the communications */
        /*
         * VERY IMP NOTE: Always setup the Notification Callback first then init
         * the comms as comms also gives this callback to the listeners
         */
        comms.registerNotification(errNotificationCallback);

        /* comms triggers sadle send this data.
         * try {

            ((IUhuComms) comms).setUhuCallback(uhuPcCallback);

        } catch (Exception e) {

            logger.error("Unable to set uhu callback for the comms.", e);
            return false;
        }
        */
        final PipeManager pipeManager = serviceStarterHelper
                .createPipeManager();

        final InetAddress addr = UhuNetworkUtilities.getIpForInterface(intf);

        /*
         * CommsProperties is not used by comms manager. Properties are handled
         * by UhuCommsPC
         */
        comms.initComms(null, addr, uhuSadlManager, pipeManager);
        comms.startComms();

        // the comms manager for the proxy
        proxyforServices.setCommsManager(comms);

        // Set RTC Signalling for streaming

        // FIXME: Throwing some errors on comms object 
        /* SignalingFactory.createSignalingInstance(
                "com.bosch.upa.uhu.rtc.streaming.Signaling", comms);
         	*/

        // init the comms manager for sadl
        uhuSadlManager.initSadlManager(comms);

        return true;
    }

}
