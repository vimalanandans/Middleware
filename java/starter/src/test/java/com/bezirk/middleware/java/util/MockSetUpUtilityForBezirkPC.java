package com.bezirk.middleware.java.util;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.comms.CommsNotification;
import com.bezirk.middleware.core.datastorage.PersistenceConstants;
import com.bezirk.middleware.core.datastorage.PersistenceRegistry;
import com.bezirk.middleware.core.datastorage.PubSubBrokerStorage;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.datastorage.SpherePersistence;
import com.bezirk.middleware.core.datastorage.SphereRegistry;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.java.device.JavaDevice;
import com.bezirk.middleware.java.networking.JavaNetworkManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.java.persistence.DatabaseConnectionForJava;
import com.bezirk.middleware.core.pubsubbroker.PubSubBroker;
import com.bezirk.middleware.core.sphere.api.DevMode;
import com.bezirk.middleware.core.sphere.api.SphereConfig;
import com.bezirk.middleware.core.sphere.api.SphereListener;
import com.bezirk.middleware.java.sphere.impl.JavaPrefs;
import com.bezirk.middleware.core.sphere.security.CryptoEngine;
import com.bezirk.middleware.core.streaming.StreamManager;
import com.bezirk.middleware.core.streaming.Streaming;
import com.bezirk.middleware.core.util.ValidatorUtility;
import com.j256.ormlite.table.TableUtils;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * Mock Set up used for unit testing.
 *
 * @author AJC6KOR
 */
public class MockSetUpUtilityForBezirkPC {
    private final static Logger logger = LoggerFactory.getLogger(MockSetUpUtilityForBezirkPC.class);

    private static final String DBPath = "./";
    private static final String DBVersion = PersistenceConstants.DB_VERSION;
    private static InetAddress inetAddr;
    PubSubBroker pubSubBroker = null;
    PubSubBrokerStorage pubSubBrokerStorage;
    SpherePersistence spherePersistence;
    Device upaDevice;
    CryptoEngine cryptoEngine;
    SphereRegistry sphereRegistry;
    Comms comms;
    SphereConfig sphereConfig;
    private DatabaseConnectionForJava dbConnection;
    private RegistryStorage regPersistence;
    NetworkManager networkManager;

    public void setUPTestEnv() throws IOException, SQLException,
            Exception {
        networkManager = new JavaNetworkManager();
        dbConnection = new DatabaseConnectionForJava(DBPath);
        regPersistence = new RegistryStorage(
                dbConnection, DBVersion);

        inetAddr = getInetAddress();


        spherePersistence = (SpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        pubSubBrokerStorage = (PubSubBrokerStorage) regPersistence;
        setUpUpaDevice();

        //sphereConfig = new SphereProperties();
        sphereConfig = new JavaPrefs();
        sphereConfig.init();


        comms = new MockComms();
        Streaming streamManager = new StreamManager(comms, /*pubSubBroker, getStreamDownloadPath(), */ networkManager);
        //comms.initComms(null, inetAddr, null, streamManager);

        comms.registerNotification(Mockito.mock(CommsNotification.class));
        //comms.startComms();

        pubSubBroker = new PubSubBroker(pubSubBrokerStorage, upaDevice, networkManager, comms, new MockCallback(), null, null, streamManager, null);

        // SphereServiceManager bezirkSphere = new SphereServiceManager(cryptoEngine, upaDevice, sphereRegistry);
        SphereListener sphereListener = Mockito.mock(SphereListener.class);
        //bezirkSphere.initSphere(spherePersistence, comms, sphereListener, sphereConfig);

        //pubSubBroker.initPubSubBroker(comms,new MockCallback(),bezirkSphere,bezirkSphere);
        //pubSubBroker.initPubSubBroker(comms, new MockCallback(), null, null);
    }

    String getStreamDownloadPath() {
        String downloadPath;
        // port factory is part of comms manager
        // CommsConfigurations.portFactory = new
        // StreamPortFactory(CommsConfigurations.STARTING_PORT_FOR_STREAMING,
        // CommsConfigurations.ENDING_PORT_FOR_STREAMING); // initialize the
        // StreamPortFactory

        downloadPath = File.separator + new String("downloads") + File.separator;
        final File createDownloadFolder = new File(
                downloadPath);
        if (!createDownloadFolder.exists()) {
            if (!createDownloadFolder.mkdir()) {
                logger.error("Failed to create download direction: {}",
                        createDownloadFolder.getAbsolutePath());
            }
        }
        return downloadPath;
    }

    /**
     * @throws UnknownHostException
     */
    private void setUpUpaDevice() throws UnknownHostException {
        upaDevice = new JavaDevice();

    }

    public NetworkInterface getInterface() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {

                        return intf;
                    }

                }
            }
        } catch (SocketException e) {

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    public InetAddress getInetAddress() {
        try {

            NetworkInterface intf = getInterface();
            if (ValidatorUtility.isObjectNotNull(intf)) {

                return networkManager.getIpForInterface(intf);

            }

        } catch (Exception e) {

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    public RegistryStorage getRegistryPersistence() {

        return regPersistence;
    }

    public Comms getComms() {

        return comms;
    }

    public PubSubBroker getPubSubBroker() throws UnknownHostException {
        return pubSubBroker;
    }

    public Device getUpaDevice() {

        return upaDevice;
    }


    public void destroyTestSetUp() throws SQLException,
            IOException, Exception {
        regPersistence.clearPersistence();


        TableUtils.dropTable(dbConnection.getDatabaseConnection(),
                PersistenceRegistry.class, true);
    }

    /**
     * If dev mode is on the device has 2 associated spheres. This is required for testing events to be sent on the wire as it depends on the number of spheres in the device
     *
     * @return 2 if development sphere is on, 1 otherwise.
     */
    public int getTotalSpheres() {
        return (sphereConfig.getMode().equals(DevMode.Mode.ON)) ? 2 : 1;
    }
}
