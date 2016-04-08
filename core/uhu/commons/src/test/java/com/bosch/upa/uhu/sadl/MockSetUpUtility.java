package com.bosch.upa.uhu.sadl;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.CommsProperties;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.persistence.DBConstants;
import com.bosch.upa.uhu.persistence.DatabaseConnectionForJava;
import com.bosch.upa.uhu.persistence.ISadlPersistence;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.persistence.UhuRegistry;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.impl.UhuSphere;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.bosch.upa.uhu.test.sphere.testUtilities.SpherePropertiesMock;
import com.j256.ormlite.table.TableUtils;

/**
 * This utility facilitates the setup and destruction of mock environment for testing sadlManager.
 * 
 * @author AJC6KOR
 *
 */
public class MockSetUpUtility {

	private final static Logger log = LoggerFactory
			.getLogger(MockSetUpUtility.class);

	private static final String DBPath = "./";
	private static final String DBVersion = DBConstants.DB_VERSION;
	private DatabaseConnectionForJava dbConnection;
	
	UhuSadlManager uhuSadlManager = null;
	private static InetAddress inetAddr;

	ISadlPersistence sadlPersistence;

	ISpherePersistence spherePersistence;

	UPADeviceInterface upaDevice;

	CryptoEngine cryptoEngine;

	SphereRegistry sphereRegistry;

	IUhuComms uhuComms;
	
	ISphereConfig sphereConfig;
	
	void setUPTestEnv() throws IOException, SQLException,
			Exception {
		
		dbConnection = new DatabaseConnectionForJava(DBPath);
		RegistryPersistence regPersistence = new RegistryPersistence(
				dbConnection, DBVersion);

		getInetAddress();
		
		sphereConfig = new SpherePropertiesMock();
		spherePersistence = (ISpherePersistence) regPersistence;
		sphereRegistry  = new SphereRegistry();
		cryptoEngine = new CryptoEngine(sphereRegistry );
		sadlPersistence = (ISadlPersistence) regPersistence;
		uhuSadlManager = new UhuSadlManager(sadlPersistence);
		uhuComms = mock(IUhuComms.class);
		CommsProperties commsProperties = new CommsProperties();
		uhuComms.initComms(commsProperties, inetAddr, uhuSadlManager,  null);
		uhuComms.startComms();
		uhuSadlManager.initSadlManager(uhuComms);
		
		setupUpaDevice();
/*		UhuSphere sphereForSadl = new UhuSphere(cryptoEngine, upaDevice, sphereRegistry);
		UhuCompManager.setSphereForSadl(sphereForSadl );*/
	}

	 void setupUpaDevice() {
		upaDevice = new MockUPADevice();
		UhuCompManager.setUpaDevice(upaDevice);
	}

	private static InetAddress getInetAddress() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {

						inetAddr = UhuNetworkUtilities.getIpForInterface(intf);
						return inetAddr;
					}

				}
			}
		} catch (SocketException e) {

			log.error("Unable to fetch network interface");

		}
		return null;
	}

	void destroyTestSetUp() throws SQLException,
			IOException, Exception {
		((RegistryPersistence) uhuSadlManager.sadlPersistence)
				.clearPersistence();
		TableUtils.dropTable(dbConnection.getDatabaseConnection(),
				UhuRegistry.class, true);
	}
	
	void clearSadlPersistence(){
		
		uhuSadlManager.sadlPersistence = null;
	}
	void restoreSadlPersistence(){
		
		uhuSadlManager.sadlPersistence = this.sadlPersistence;
	}

}
