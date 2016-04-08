package com.bosch.upa.uhu.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Enumeration;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.ICommsNotification;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.UhuCommsManager;
import com.bosch.upa.uhu.comms.UhuCommsPC;
import com.bosch.upa.uhu.device.UhuDevice;
import com.bosch.upa.uhu.device.UhuDeviceType;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.persistence.DBConstants;
import com.bosch.upa.uhu.persistence.DatabaseConnectionForJava;
import com.bosch.upa.uhu.persistence.ISadlPersistence;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.persistence.UhuRegistry;
import com.bosch.upa.uhu.sadl.UhuSadlManager;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuSphereListener;
import com.bosch.upa.uhu.sphere.api.IUhuSphereRegistration;
import com.bosch.upa.uhu.sphere.impl.SphereProperties;
import com.bosch.upa.uhu.sphere.impl.UhuSphere;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.j256.ormlite.table.TableUtils;

/**
 *  Mock Set up used for unit testing.
 * 
 * @author AJC6KOR
 *
 */
public class MockSetUpUtilityForUhuPC {
	
	private final static Logger log = LoggerFactory
			.getLogger(MockSetUpUtilityForUhuPC.class);

	private static final String DBPath = "./";
	private static final String DBVersion = DBConstants.DB_VERSION;
	private DatabaseConnectionForJava dbConnection;
	
	UhuSadlManager uhuSadlManager = null;
	private static InetAddress inetAddr;

	ISadlPersistence sadlPersistence;

	ISpherePersistence spherePersistence;

	UhuDevice upaDevice;

	CryptoEngine cryptoEngine;

	SphereRegistry sphereRegistry;

	IUhuComms uhuComms;
    
	ISphereConfig sphereConfig;
	private RegistryPersistence regPersistence;

	public void setUPTestEnv() throws IOException, SQLException,
			Exception {
		
		dbConnection = new DatabaseConnectionForJava(DBPath);
		regPersistence = new RegistryPersistence(
				dbConnection, DBVersion);
		
		inetAddr = getInetAddress();
		UhuCommsPC.init();
		
		spherePersistence = (ISpherePersistence) regPersistence;
		sphereRegistry  = new SphereRegistry();
		cryptoEngine = new CryptoEngine(sphereRegistry );
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
		uhuSphere.initSphere(spherePersistence, uhuComms,sphereListener,sphereConfig);
		UhuCompManager.setSphereRegistration((IUhuSphereRegistration) uhuSphere);
		UhuCompManager.setSphereForSadl(uhuSphere);
		UhuCompManager.setplatformSpecificCallback(new MockCallbackService());
	}


	
	/**
	 * @throws UnknownHostException 
	 * 
	 */
	private void setUpUpaDevice() throws UnknownHostException {
		upaDevice = new UhuDevice();
		String deviceIdString=InetAddress.getLocalHost().getHostName();
		upaDevice.initDevice(deviceIdString,
				UhuDeviceType.UHU_DEVICE_TYPE_PC);
		UhuCompManager.setUpaDevice(upaDevice);
	}

	public NetworkInterface getInterface() {
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
			if(UhuValidatorUtility.isObjectNotNull(intf)){
				
				return UhuNetworkUtilities.getIpForInterface(intf);
			
			}
			
		} catch (Exception e) {

			log.error("Unable to fetch network interface");

		}
		return null;
	}

	public RegistryPersistence getRegistryPersistence(){
		
		return regPersistence;
	}
	
	public IUhuComms getUhuComms(){
		
		return uhuComms;
	}
	
	public UhuSadlManager getUhuSadlManager() throws UnknownHostException{
		return uhuSadlManager;
	}
	
	public UPADeviceInterface getUpaDevice(){
		
		return upaDevice ;
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
