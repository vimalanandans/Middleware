package com.bezirk.util;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.comms.BezirkCommsPC;
import com.bezirk.comms.CommsNotification;
import com.bezirk.device.Device;
import com.bezirk.device.DeviceType;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.persistence.PersistenceConstants;
import com.bezirk.persistence.PersistenceRegistry;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.PubSubBrokerPersistence;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereConfig;
import com.bezirk.sphere.impl.SphereServiceManager;
import com.bezirk.sphere.impl.JavaPrefs;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.streaming.StreamManager;
import com.bezirk.streaming.Streaming;
import com.bezrik.network.BezirkNetworkUtilities;
import com.j256.ormlite.table.TableUtils;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    PubSubBrokerPersistence pubSubBrokerPersistence;
    SpherePersistence spherePersistence;
    Device upaDevice;
    CryptoEngine cryptoEngine;
    SphereRegistry sphereRegistry;
    Comms comms;
    SphereConfig sphereConfig;
    private DatabaseConnectionForJava dbConnection;
    private RegistryPersistence regPersistence;

    public void setUPTestEnv() throws IOException, SQLException,
            Exception {

        dbConnection = new DatabaseConnectionForJava(DBPath);
        regPersistence = new RegistryPersistence(
                dbConnection, DBVersion);

        inetAddr = getInetAddress();
        BezirkCommsPC.init();

        spherePersistence = (SpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        pubSubBrokerPersistence = (PubSubBrokerPersistence) regPersistence;
        pubSubBroker = new PubSubBroker(pubSubBrokerPersistence);
        //sphereConfig = new SphereProperties();
        sphereConfig = new JavaPrefs();
        sphereConfig.init();


        comms = new MockComms();
        Streaming streamManager = new StreamManager(comms,pubSubBroker);
        comms.initComms(null, inetAddr, pubSubBroker, null,streamManager);

        comms.registerNotification(Mockito.mock(CommsNotification.class));
        comms.startComms();

        setUpUpaDevice();
        SphereServiceManager bezirkSphere = new SphereServiceManager(cryptoEngine, upaDevice, sphereRegistry);
        SphereListener sphereListener = Mockito.mock(SphereListener.class);
        bezirkSphere.initSphere(spherePersistence, comms, sphereListener, sphereConfig);

        pubSubBroker.initPubSubBroker(comms,new MockCallback(),bezirkSphere,bezirkSphere);
        /*BezirkCompManager.setSphereSecurity((SphereSecurity) bezirkSphere);
        BezirkCompManager.setSphereForPubSub(bezirkSphere);
        BezirkCompManager.setplatformSpecificCallback(new MockCallback());*/
    }


    /**
     * @throws UnknownHostException
     */
    private void setUpUpaDevice() throws UnknownHostException {
        upaDevice = new Device();
        String deviceIdString = InetAddress.getLocalHost().getHostName();
        upaDevice.initDevice(deviceIdString,
                DeviceType.BEZIRK_DEVICE_TYPE_PC);
        BezirkCompManager.setUpaDevice(upaDevice);
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

                return BezirkNetworkUtilities.getIpForInterface(intf);

            }

        } catch (Exception e) {

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    public RegistryPersistence getRegistryPersistence() {

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

          /*  BezirkCompManager.setSphereSecurity(null);
            BezirkCompManager.setSphereForPubSub(null);
            BezirkCompManager.setplatformSpecificCallback(null);
        */
        BezirkCompManager.setUpaDevice(null);

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
