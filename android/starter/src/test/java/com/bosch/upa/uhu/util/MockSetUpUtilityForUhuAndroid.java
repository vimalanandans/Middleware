package com.bezirk.util;

/*import UPADeviceInterface;
import UhuCompManager;
import ICommsNotification;
import IUhuComms;
import UhuCommsAndroid;
import com.bosch.upa.uhu.comms.UhuCommsManager;
import UhuDevice;
import UhuDeviceType;
import UhuNetworkUtilities;
import DBConstants;
import ISadlPersistence;
import ISpherePersistence;
import RegistryPersistence;
import SphereRegistry;
import UhuRegistry;
import DatabaseConnectionForAndroid;
import UhuSadlManager;
import ISphereConfig;
import IUhuSphereListener;
import IUhuSphereRegistration;
import UhuSphere;
import CryptoEngine;
import MainService;
import UhuPreferences;
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
import java.util.Enumeration;*/


/**
 * Created by AJC6KOR on 10/27/2015.
 */


public class MockSetUpUtilityForUhuAndroid {/*

    private final static Logger log = LoggerFactory
            .getLogger(MockSetUpUtilityForUhuAndroid.class);

    private static final String DBPath = "./";
    private static final String DBVersion = DBConstants.DB_VERSION;
    private DatabaseConnectionForAndroid dbConnection;

    UhuSadlManager uhuSadlManager = null;
    private static InetAddress inetAddr;

    ISadlPersistence sadlPersistence;

    ISpherePersistence spherePersistence;

    UhuDevice upaDevice;

    CryptoEngine cryptoEngine;

    SphereRegistry sphereRegistry;

    UhuCommsManager uhuComms;

    private RegistryPersistence regPersistence;

    public void setUPTestEnv() throws IOException, SQLException,
            Exception {
        MainService service = new MainService();
        UhuPreferences preferences = new UhuPreferences(service);
        UhuCommsAndroid.init(preferences);
        dbConnection = new DatabaseConnectionForAndroid(service);
        regPersistence = new RegistryPersistence(
                dbConnection, DBVersion);

        inetAddr = getInetAddress();
        UhuCommsAndroid.init(preferences);

        spherePersistence = (ISpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        sadlPersistence = (ISadlPersistence) regPersistence;
        uhuSadlManager = new UhuSadlManager(sadlPersistence);

        uhuComms = new UhuCommsManager();
        uhuSadlManager.initSadlManager(uhuComms);
        uhuComms.registerNotification(Mockito.mock(ICommsNotification.class));
        //uhuComms.setUhuCallback(new MockCallbackService());
        uhuComms.initComms(null, inetAddr, uhuSadlManager, null);
        uhuComms.startComms();

        setUpUpaDevice();
        UhuSphere uhuSphere = new UhuSphere(cryptoEngine, upaDevice, sphereRegistry);
        IUhuSphereListener sphereListener = Mockito.mock(IUhuSphereListener.class);
        uhuSphere.initSphere(spherePersistence, uhuComms, sphereListener, Mockito.mock(ISphereConfig.class));
        UhuCompManager.setSphereRegistration((IUhuSphereRegistration) uhuSphere);
        UhuCompManager.setSphereForSadl(uhuSphere);
        UhuCompManager.setplatformSpecificCallback(new MockCallbackService());
    }



*//*
 *
 * @throws UnknownHostException
 *
 *//*

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
    }*/

}
