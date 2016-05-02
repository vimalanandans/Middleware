package com.bezirk.util;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkCommsPC;
import com.bezirk.comms.CommsNotification;
import com.bezirk.comms.BezirkComms;
import com.bezirk.device.BezirkDevice;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.SadlPersistence;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.persistence.BezirkRegistry;
import com.bezirk.sadl.BezirkSadlManager;
import com.bezirk.sphere.api.BezirkSphereListener;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkSphereRegistration;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezirk.sphere.impl.SphereProperties;
import com.bezirk.sphere.security.CryptoEngine;
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
    private static final String DBVersion = DBConstants.DB_VERSION;
    private static InetAddress inetAddr;
    BezirkSadlManager bezirkSadlManager = null;
    SadlPersistence sadlPersistence;
    SpherePersistence spherePersistence;
    BezirkDevice upaDevice;
    CryptoEngine cryptoEngine;
    SphereRegistry sphereRegistry;
    BezirkComms bezirkComms;
    ISphereConfig sphereConfig;
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
        sadlPersistence = (SadlPersistence) regPersistence;
        bezirkSadlManager = new BezirkSadlManager(sadlPersistence);
        sphereConfig = new SphereProperties();
        sphereConfig.init();

        bezirkComms = new MockComms();
        bezirkComms.initComms(null, inetAddr, bezirkSadlManager, null);
        bezirkSadlManager.initSadlManager(bezirkComms);
        bezirkComms.registerNotification(Mockito.mock(CommsNotification.class));
        bezirkComms.startComms();

        setUpUpaDevice();
        BezirkSphere bezirkSphere = new BezirkSphere(cryptoEngine, upaDevice, sphereRegistry);
        BezirkSphereListener sphereListener = Mockito.mock(BezirkSphereListener.class);
        bezirkSphere.initSphere(spherePersistence, bezirkComms, sphereListener, sphereConfig);
        BezirkCompManager.setSphereRegistration((BezirkSphereRegistration) bezirkSphere);
        BezirkCompManager.setSphereForSadl(bezirkSphere);
        BezirkCompManager.setplatformSpecificCallback(new MockCallbackZirk());
    }


    /**
     * @throws UnknownHostException
     */
    private void setUpUpaDevice() throws UnknownHostException {
        upaDevice = new BezirkDevice();
        String deviceIdString = InetAddress.getLocalHost().getHostName();
        upaDevice.initDevice(deviceIdString,
                BezirkDeviceType.BEZIRK_DEVICE_TYPE_PC);
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
            if (BezirkValidatorUtility.isObjectNotNull(intf)) {

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

    public BezirkComms getBezirkComms() {

        return bezirkComms;
    }

    public BezirkSadlManager getBezirkSadlManager() throws UnknownHostException {
        return bezirkSadlManager;
    }

    public UPADeviceInterface getUpaDevice() {

        return upaDevice;
    }


    public void destroyTestSetUp() throws SQLException,
            IOException, Exception {
        bezirkComms.stopComms();
        bezirkComms.closeComms();
        regPersistence.clearPersistence();

        BezirkCompManager.setSphereRegistration(null);
        BezirkCompManager.setSphereForSadl(null);
        BezirkCompManager.setplatformSpecificCallback(null);
        BezirkCompManager.setUpaDevice(null);

        TableUtils.dropTable(dbConnection.getDatabaseConnection(),
                BezirkRegistry.class, true);
    }
}
