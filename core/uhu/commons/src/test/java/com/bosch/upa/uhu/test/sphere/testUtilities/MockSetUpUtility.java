package com.bosch.upa.uhu.test.sphere.testUtilities;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo;
import com.bosch.upa.uhu.api.objects.UhuDeviceInfo.UhuDeviceRole;
import com.bosch.upa.uhu.api.objects.UhuServiceInfo;
import com.bosch.upa.uhu.comms.CommsProperties;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.comms.IUhuCommsLegacy;
import com.bosch.upa.uhu.control.messages.ControlLedger;
import com.bosch.upa.uhu.control.messages.ControlMessage;
import com.bosch.upa.uhu.device.UhuDeviceType;
import com.bosch.upa.uhu.discovery.SphereDiscovery;
import com.bosch.upa.uhu.discovery.SphereDiscoveryProcessor;
import com.bosch.upa.uhu.network.UhuNetworkUtilities;
import com.bosch.upa.uhu.persistence.DBConstants;
import com.bosch.upa.uhu.persistence.DatabaseConnectionForJava;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.persistence.UhuRegistry;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.CatchProcessor;
import com.bosch.upa.uhu.sphere.impl.DeviceInformation;
import com.bosch.upa.uhu.sphere.impl.DiscoveryProcessor;
import com.bosch.upa.uhu.sphere.impl.ShareProcessor;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.SphereRegistryWrapper;
import com.bosch.upa.uhu.sphere.impl.UhuSphere;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.j256.ormlite.table.TableUtils;
import com.sun.org.apache.xpath.internal.WhitespaceStrippingElementMatcher;

public class MockSetUpUtility {

	private static final Logger log = LoggerFactory.getLogger(MockSetUpUtility.class);
	private InetAddress inetAddr;

	private static final String DBPath = "./";
	private static final String DBVersion = DBConstants.DB_VERSION;
	private DatabaseConnectionForJava dbConnection;

	public UhuSphere uhuSphere = null;
	public CryptoEngine cryptoEngine;
	public UPADeviceInterface upaDevice;
	public SphereRegistry registry;
	public ISpherePersistence spherePersistence;
	public SphereRegistryWrapper sphereRegistryWrapper;
	public ShareProcessor shareProcessor;
	public CatchProcessor catchProcessor;
	public DiscoveryProcessor discoveryProcessor;
	public ISphereConfig sphereConfig;
	public Sphere sphere;
	

	public void setUPTestEnv() throws IOException, SQLException, Exception {

		dbConnection = new DatabaseConnectionForJava(DBPath);
		RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);

		getInetAddress();

		spherePersistence = (ISpherePersistence) regPersistence;
		upaDevice = new Device();
		sphereConfig = new SpherePropertiesMock();
		registry = spherePersistence.loadSphereRegistry();
		cryptoEngine = new CryptoEngine(registry);
		uhuSphere = new UhuSphere(cryptoEngine, upaDevice, registry);
		sphereRegistryWrapper = new SphereRegistryWrapper(registry, spherePersistence, upaDevice, cryptoEngine, null, sphereConfig);		
		sphereRegistryWrapper.init();
		// create default sphere
		// uhuSphere.createSphere(null, null);

		SphereDiscovery discovery = new SphereDiscovery(uhuSphere);
		SphereDiscoveryProcessor.setDiscovery(discovery);
		IUhuCommsLegacy uhuComms = mock(IUhuCommsLegacy.class);
		when(uhuComms.sendMessage(any(ControlLedger.class))).thenReturn(true);
		CommsProperties commsProperties = new CommsProperties();
		uhuComms.initComms(commsProperties, inetAddr, null, null);
		uhuComms.startComms();
		uhuSphere.initSphere(spherePersistence, uhuComms, null, sphereConfig);
				
		Field spField = uhuSphere.getClass().getDeclaredField("shareProcessor");
		spField.setAccessible(true);
		shareProcessor = (ShareProcessor) spField.get(uhuSphere);
		
		Field cpField = uhuSphere.getClass().getDeclaredField("catchProcessor");
		cpField.setAccessible(true);
		catchProcessor = (CatchProcessor) cpField.get(uhuSphere);
		
		Field dpField = uhuSphere.getClass().getDeclaredField("discoveryProcessor");
        dpField.setAccessible(true);
        discoveryProcessor = (DiscoveryProcessor) dpField.get(uhuSphere);
		
		// create default sphere
		// uhuSphere.createSphere(null, null);
	}

	public void destroyTestSetUp() throws SQLException, IOException, Exception {
		TableUtils.dropTable(dbConnection.getDatabaseConnection(), UhuRegistry.class, true);
	}

	InetAddress getInetAddress() {
		try {

			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
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

	public LinkedHashMap<String, ArrayList<UhuServiceId>> getDeviceServicesList(SphereRegistry registry,
			String sphereId) {
		DeviceInformation deviceInformation = new DeviceInformation("DEVICE", UhuDeviceType.UHU_DEVICE_TYPE_OTHER);
		registry.devices.put(sphereId, deviceInformation);
		deviceInformation = new DeviceInformation("DEVICE1", UhuDeviceType.UHU_DEVICE_TYPE_OTHER);
		String deviceId1 = upaDevice.getDeviceId();
		registry.devices.put(deviceId1, deviceInformation);
		deviceInformation = new DeviceInformation("DEVICE2", UhuDeviceType.UHU_DEVICE_TYPE_OTHER);
		String deviceId2 = "Device2";
		registry.devices.put(deviceId2, deviceInformation);
		deviceInformation = new DeviceInformation("DEVICE3", UhuDeviceType.UHU_DEVICE_TYPE_OTHER);
		String deviceId3 = "Device3";
		registry.devices.put(deviceId3, deviceInformation);
		LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<>();
		ArrayList<UhuServiceId> device1ServiceSet = new ArrayList<UhuServiceId>();
		device1ServiceSet.add(new UhuServiceId("Service11"));
		device1ServiceSet.add(new UhuServiceId("Service12"));
		device1ServiceSet.add(new UhuServiceId("Service13"));
		deviceServices.put(deviceId1, device1ServiceSet);
		ArrayList<UhuServiceId> device2ServiceSet = new ArrayList<UhuServiceId>();
		device2ServiceSet.add(new UhuServiceId("Service21"));
		device2ServiceSet.add(new UhuServiceId("Service22"));
		device2ServiceSet.add(new UhuServiceId("Service23"));
		deviceServices.put(deviceId2, device2ServiceSet);
		ArrayList<UhuServiceId> device3ServiceSet = new ArrayList<UhuServiceId>();
		device3ServiceSet.add(new UhuServiceId("Service31"));
		device3ServiceSet.add(new UhuServiceId("Service32"));
		device3ServiceSet.add(new UhuServiceId("Service33"));
		deviceServices.put(deviceId3, device3ServiceSet);

		return deviceServices;
	}

	public ArrayList<UhuDeviceInfo> getDeviceList() {
		ArrayList<UhuDeviceInfo> deviceList = new ArrayList<UhuDeviceInfo>();

		List<UhuServiceInfo> services1 = new ArrayList<UhuServiceInfo>();
		UhuServiceInfo serviceInfo1 = new UhuServiceInfo("Service11", "Service11", "LOCAL", true, true);
		UhuServiceInfo serviceInfo2 = new UhuServiceInfo("Service12", "Service12", "LOCAL", true, true);
		UhuServiceInfo serviceInfo3 = new UhuServiceInfo("Service13", "Service13", "LOCAL", true, true);
		services1.add(serviceInfo1);
		services1.add(serviceInfo2);
		services1.add(serviceInfo3);

		List<UhuServiceInfo> services2 = new ArrayList<UhuServiceInfo>();
		serviceInfo1 = new UhuServiceInfo("Service21", "Service21", "LOCAL", true, true);
		serviceInfo2 = new UhuServiceInfo("Service22", "Service22", "LOCAL", true, true);
		serviceInfo3 = new UhuServiceInfo("Service23", "Service23", "LOCAL", true, true);
		services2.add(serviceInfo1);
		services2.add(serviceInfo2);
		services2.add(serviceInfo3);

		List<UhuServiceInfo> services3 = new ArrayList<UhuServiceInfo>();
		serviceInfo1 = new UhuServiceInfo("Service31", "Service31", "LOCAL", true, true);
		serviceInfo2 = new UhuServiceInfo("Service32", "Service32", "LOCAL", true, true);
		serviceInfo3 = new UhuServiceInfo("Service33", "Service33", "LOCAL", true, true);
		services3.add(serviceInfo1);
		services3.add(serviceInfo2);
		services3.add(serviceInfo3);

		UhuDeviceInfo uhuDeviceInfo1 = new UhuDeviceInfo("Device1", "Device1", UhuDeviceType.UHU_DEVICE_TYPE_OTHER,
				UhuDeviceRole.UHU_CONTROL, true, services1);
		UhuDeviceInfo uhuDeviceInfo2 = new UhuDeviceInfo("Device2", "Device2", UhuDeviceType.UHU_DEVICE_TYPE_OTHER,
				UhuDeviceRole.UHU_MEMBER, true, services2);
		UhuDeviceInfo uhuDeviceInfo3 = new UhuDeviceInfo("Device3", "Device3", UhuDeviceType.UHU_DEVICE_TYPE_OTHER,
				UhuDeviceRole.UHU_MEMBER, true, services3);

		deviceList.add(uhuDeviceInfo1);
		deviceList.add(uhuDeviceInfo2);
		deviceList.add(uhuDeviceInfo3);
		return deviceList;
	}

	public String getDefaultSphereId(SphereRegistry registry) {

		String defaultSphereId = null;

		Iterator<String> sphereIterator = registry.spheres.keySet().iterator();
		if (sphereIterator.hasNext()) {

			String sphereId = sphereIterator.next();

			if (registry.spheres.get(sphereId).getSphereType()
					.equalsIgnoreCase(UhuSphereType.UHU_SPHERE_TYPE_DEFAULT)) {
				log.info("sphere id from registry : " + sphereId);

				defaultSphereId = new String(sphereId);
			}

		}
		log.debug("Default sphereId : " + defaultSphereId);
		return defaultSphereId;

	}
}
