package com.bezirk.util;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.ICommsNotification;
import com.bezirk.comms.IUhuComms;
import com.bezirk.comms.UhuCommsPC;
import com.bezirk.device.UhuDevice;
import com.bezirk.device.UhuDeviceType;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.ISadlPersistence;
import com.bezirk.persistence.ISpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.persistence.UhuRegistry;
import com.bezirk.sadl.UhuSadlManager;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuSphereListener;
import com.bezirk.sphere.api.IUhuSphereRegistration;
import com.bezirk.sphere.impl.SphereProperties;
import com.bezirk.sphere.impl.UhuSphere;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezrik.network.UhuNetworkUtilities;
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
public class MockSetUpUtilityForUhuPC {

    private final static Logger log = LoggerFactory
            .getLogger(MockSetUpUtilityForUhuPC.class);

    private static final String DBPath = "./";
    private static final String DBVersion = DBConstants.DB_VERSION;
    private static InetAddress inetAddr;
    UhuSadlManager uhuSadlManager = null;
    ISadlPersistence sadlPersistence;
    ISpherePersistence spherePersistence;
    UhuDevice upaDevice;
    CryptoEngine cryptoEngine;
    SphereRegistry sphereRegistry;
    IUhuComms uhuComms;
    ISphereConfig sphereConfig;
    private DatabaseConnectionForJava dbConnection;
    private RegistryPersistence regPersistence;

    public void setUPTestEnv() throws IOException, SQLException,
            Exception {

        dbConnection = new DatabaseConnectionForJava(DBPath);
        regPersistence = new RegistryPersistence(
                dbConnection, DBVersion);

        inetAddr = getInetAddress();
        UhuCommsPC.init();

        spherePersistence = (ISpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        sadlPersistence = (ISadlPersistence) regPersistence;
        uhuSadlManager = new UhuSadlManager(sadlPersistence);
        sphereConfig = new SphereProperties();
        sphereConfig.init();

        uhuComms = new MockComms();
        uhuComms.initComms(null, inetAddr, uhuSadlManager, null);
        uhuSadlManager.initSadlManager(uhuComms);
        uhuComms.registerNotification(Mockito.mock(ICommsNotification.class));
        uhuComms.startComms();

        setUpUpaDevice();
        UhuSphere uhuSphere = new UhuSphere(cryptoEngine, upaDevice, sphereRegistry);
        IUhuSphereListener sphereListener = Mockito.mock(IUhuSphereListener.class);
        uhuSphere.initSphere(spherePersistence, uhuComms, sphereListener, sphereConfig);
        UhuCompManager.setSphereRegistration((IUhuSphereRegistration) uhuSphere);
        UhuCompManager.setSphereForSadl(uhuSphere);
        UhuCompManager.setplatformSpecificCallback(new MockCallbackService());
    }


    /**
     * @throws UnknownHostException
     */
    private void setUpUpaDevice() throws UnknownHostException {
        upaDevice = new UhuDevice();
        String deviceIdString = InetAddress.getLocalHost().getHostName();
        upaDevice.initDevice(deviceIdString,
                UhuDeviceType.UHU_DEVICE_TYPE_PC);
        UhuCompManager.setUpaDevice(upaDevice);
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

            log.error("Unable to fetch network interface");

        }
        return null;
    }

    public InetAddress getInetAddress() {
        try {

            NetworkInterface intf = getInterface();
            if (UhuValidatorUtility.isObjectNotNull(intf)) {

                return UhuNetworkUtilities.getIpForInterface(intf);

            }

        } catch (Exception e) {

            log.error("Unable to fetch network interface");

        }
        return null;
    }

    public RegistryPersistence getRegistryPersistence() {

        return regPersistence;
    }

    public IUhuComms getUhuComms() {

        return uhuComms;
    }

    public UhuSadlManager getUhuSadlManager() throws UnknownHostException {
        return uhuSadlManager;
    }

    public UPADeviceInterface getUpaDevice() {

        return upaDevice;
    }


    public void destroyTestSetUp() throws SQLException,
            IOException, Exception {
        uhuComms.stopComms();
        uhuComms.closeComms();
        regPersistence.clearPersistence();

        UhuCompManager.setSphereRegistration(null);
        UhuCompManager.setSphereForSadl(null);
        UhuCompManager.setplatformSpecificCallback(null);
        UhuCompManager.setUpaDevice(null);

        TableUtils.dropTable(dbConnection.getDatabaseConnection(),
                UhuRegistry.class, true);
    }
}
