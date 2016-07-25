package com.bezirk.util;

import com.bezirk.comms.Comms;
import com.bezirk.comms.CommsNotification;
import com.bezirk.datastorage.PubSubBrokerStorage;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.device.Device;
import com.bezirk.device.DeviceType;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.datastorage.PersistenceConstants;
import com.bezirk.datastorage.PersistenceRegistry;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.datastorage.SpherePersistence;
import com.bezirk.datastorage.SphereRegistry;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.api.SphereConfig;
import com.bezirk.sphere.impl.SphereServiceManager;
import com.bezirk.sphere.impl.JavaPrefs;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.streaming.StreamManager;
import com.bezirk.streaming.Streaming;
import com.bezrik.network.NetworkUtilities;
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

    public void setUPTestEnv() throws IOException, SQLException,
            Exception {

        dbConnection = new DatabaseConnectionForJava(DBPath);
        regPersistence = new RegistryStorage(
                dbConnection, DBVersion);

        inetAddr = getInetAddress();


        spherePersistence = (SpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        pubSubBrokerStorage = (PubSubBrokerStorage) regPersistence;
        setUpUpaDevice();
        pubSubBroker = new PubSubBroker(pubSubBrokerStorage,upaDevice);
        //sphereConfig = new SphereProperties();
        sphereConfig = new JavaPrefs();
        sphereConfig.init();


        comms = new MockComms();
        Streaming streamManager = new StreamManager(comms,pubSubBroker,getStreamDownloadPath());
        comms.initComms(null, inetAddr, pubSubBroker, null,streamManager);

        comms.registerNotification(Mockito.mock(CommsNotification.class));
        comms.startComms();


       // SphereServiceManager bezirkSphere = new SphereServiceManager(cryptoEngine, upaDevice, sphereRegistry);
        SphereListener sphereListener = Mockito.mock(SphereListener.class);
        //bezirkSphere.initSphere(spherePersistence, comms, sphereListener, sphereConfig);

        //pubSubBroker.initPubSubBroker(comms,new MockCallback(),bezirkSphere,bezirkSphere);
        pubSubBroker.initPubSubBroker(comms,new MockCallback(),null ,null);
        }
    String getStreamDownloadPath()
    {
        String downloadPath;
        // port factory is part of comms manager
        // CommsConfigurations.portFactory = new
        // StreamPortFactory(CommsConfigurations.STARTING_PORT_FOR_STREAMING,
        // CommsConfigurations.ENDING_PORT_FOR_STREAMING); // initialize the
        // StreamPortFactory

        downloadPath= File.separator+ new String ("downloads")+File.separator;
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
        upaDevice = new Device();
        String deviceIdString = InetAddress.getLocalHost().getHostName();
        upaDevice.initDevice(deviceIdString,
                DeviceType.BEZIRK_DEVICE_TYPE_PC);

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

                return NetworkUtilities.getIpForInterface(intf);

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

    public DeviceInterface getUpaDevice() {

        return upaDevice;
    }


    public void destroyTestSetUp() throws SQLException,
            IOException, Exception {
        comms.stopComms();
        comms.closeComms();
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
