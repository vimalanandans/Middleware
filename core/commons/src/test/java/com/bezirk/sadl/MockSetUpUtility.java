package com.bezirk.sadl;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.CommsProperties;
import com.bezirk.comms.BezirkComms;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.SadlPersistence;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.persistence.BezirkRegistry;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.sphere.testUtilities.SpherePropertiesMock;
import com.bezrik.network.BezirkNetworkUtilities;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;

import static org.mockito.Mockito.mock;

/**
 * This utility facilitates the setup and destruction of mock environment for testing sadlManager.
 *
 * @author AJC6KOR
 */
public class MockSetUpUtility {
    private final static Logger logger = LoggerFactory.getLogger(MockSetUpUtility.class);

    private static final String DBPath = "./";
    private static final String DBVersion = DBConstants.DB_VERSION;
    private static InetAddress inetAddr;
    BezirkSadlManager bezirkSadlManager = null;
    SadlPersistence sadlPersistence;
    SpherePersistence spherePersistence;
    UPADeviceInterface upaDevice;
    CryptoEngine cryptoEngine;
    SphereRegistry sphereRegistry;
    BezirkComms uhuComms;
    ISphereConfig sphereConfig;
    private DatabaseConnectionForJava dbConnection;

    private static InetAddress getInetAddress() {
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

                        inetAddr = BezirkNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    void setUPTestEnv() throws IOException, SQLException,
            Exception {

        dbConnection = new DatabaseConnectionForJava(DBPath);
        RegistryPersistence regPersistence = new RegistryPersistence(
                dbConnection, DBVersion);

        getInetAddress();

        sphereConfig = new SpherePropertiesMock();
        spherePersistence = (SpherePersistence) regPersistence;
        sphereRegistry = new SphereRegistry();
        cryptoEngine = new CryptoEngine(sphereRegistry);
        sadlPersistence = (SadlPersistence) regPersistence;
        bezirkSadlManager = new BezirkSadlManager(sadlPersistence);
        uhuComms = mock(BezirkComms.class);
        CommsProperties commsProperties = new CommsProperties();
        uhuComms.initComms(commsProperties, inetAddr, bezirkSadlManager, null);
        uhuComms.startComms();
        bezirkSadlManager.initSadlManager(uhuComms);

        setupUpaDevice();
/*		BezirkSphere sphereForSadl = new BezirkSphere(cryptoEngine, upaDevice, sphereRegistry);
        BezirkCompManager.setSphereForSadl(sphereForSadl );*/
    }

    void setupUpaDevice() {
        upaDevice = new MockUPADevice();
        BezirkCompManager.setUpaDevice(upaDevice);
    }

    void destroyTestSetUp() throws SQLException,
            IOException, Exception {
        ((RegistryPersistence) bezirkSadlManager.sadlPersistence)
                .clearPersistence();
        TableUtils.dropTable(dbConnection.getDatabaseConnection(),
                BezirkRegistry.class, true);
    }

    void clearSadlPersistence() {

        bezirkSadlManager.sadlPersistence = null;
    }

    void restoreSadlPersistence() {

        bezirkSadlManager.sadlPersistence = this.sadlPersistence;
    }

}
