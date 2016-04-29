package com.bezirk.sphere.testUtilities;

import com.bezirk.comms.BezirkCommsLegacy;
import com.bezirk.comms.CommsProperties;
import com.bezirk.control.messages.ControlLedger;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.discovery.SphereDiscovery;
import com.bezirk.discovery.SphereDiscoveryProcessor;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkDeviceInfo.BezirkDeviceRole;
import com.bezirk.persistence.DBConstants;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.persistence.BezirkRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.CatchProcessor;
import com.bezirk.sphere.impl.DeviceInformation;
import com.bezirk.sphere.impl.DiscoveryProcessor;
import com.bezirk.sphere.impl.ShareProcessor;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.SphereRegistryWrapper;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezrik.network.BezirkNetworkUtilities;
import com.j256.ormlite.table.TableUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static org.mockito.Mockito.*;

public class MockSetUpUtility {
    private static final Logger logger = LoggerFactory.getLogger(MockSetUpUtility.class);

    private static final String DBPath = "./";
    private static final String DBVersion = DBConstants.DB_VERSION;
    public BezirkSphere bezirkSphere = null;
    public CryptoEngine cryptoEngine;
    public UPADeviceInterface upaDevice;
    public SphereRegistry registry;
    public SpherePersistence spherePersistence;
    public SphereRegistryWrapper sphereRegistryWrapper;
    public ShareProcessor shareProcessor;
    public CatchProcessor catchProcessor;
    public DiscoveryProcessor discoveryProcessor;
    public ISphereConfig sphereConfig;
    public Sphere sphere;
    private InetAddress inetAddr;
    private DatabaseConnectionForJava dbConnection;

    public void setUPTestEnv() throws IOException, SQLException, Exception {

        dbConnection = new DatabaseConnectionForJava(DBPath);
        RegistryPersistence regPersistence = new RegistryPersistence(dbConnection, DBVersion);

        getInetAddress();

        spherePersistence = (SpherePersistence) regPersistence;
        upaDevice = new Device();
        sphereConfig = new SpherePropertiesMock();
        registry = spherePersistence.loadSphereRegistry();
        cryptoEngine = new CryptoEngine(registry);
        bezirkSphere = new BezirkSphere(cryptoEngine, upaDevice, registry);
        sphereRegistryWrapper = new SphereRegistryWrapper(registry, spherePersistence, upaDevice, cryptoEngine, null, sphereConfig);
        sphereRegistryWrapper.init();
        // create default sphere
        // bezirkSphere.createSphere(null, null);

        SphereDiscovery discovery = new SphereDiscovery(bezirkSphere);
        SphereDiscoveryProcessor.setDiscovery(discovery);
        BezirkCommsLegacy uhuComms = mock(BezirkCommsLegacy.class);
        when(uhuComms.sendMessage(any(ControlLedger.class))).thenReturn(true);
        CommsProperties commsProperties = new CommsProperties();
        uhuComms.initComms(commsProperties, inetAddr, null, null);
        uhuComms.startComms();
        bezirkSphere.initSphere(spherePersistence, uhuComms, null, sphereConfig);

        Field spField = bezirkSphere.getClass().getDeclaredField("shareProcessor");
        spField.setAccessible(true);
        shareProcessor = (ShareProcessor) spField.get(bezirkSphere);

        Field cpField = bezirkSphere.getClass().getDeclaredField("catchProcessor");
        cpField.setAccessible(true);
        catchProcessor = (CatchProcessor) cpField.get(bezirkSphere);

        Field dpField = bezirkSphere.getClass().getDeclaredField("discoveryProcessor");
        dpField.setAccessible(true);
        discoveryProcessor = (DiscoveryProcessor) dpField.get(bezirkSphere);

        // create default sphere
        // bezirkSphere.createSphere(null, null);
    }

    public void destroyTestSetUp() throws SQLException, IOException, Exception {
        TableUtils.dropTable(dbConnection.getDatabaseConnection(), BezirkRegistry.class, true);
    }

    InetAddress getInetAddress() {
        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
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

    public LinkedHashMap<String, ArrayList<BezirkZirkId>> getDeviceServicesList(SphereRegistry registry,
                                                                                String sphereId) {
        DeviceInformation deviceInformation = new DeviceInformation("DEVICE", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER);
        registry.devices.put(sphereId, deviceInformation);
        deviceInformation = new DeviceInformation("DEVICE1", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER);
        String deviceId1 = upaDevice.getDeviceId();
        registry.devices.put(deviceId1, deviceInformation);
        deviceInformation = new DeviceInformation("DEVICE2", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER);
        String deviceId2 = "Device2";
        registry.devices.put(deviceId2, deviceInformation);
        deviceInformation = new DeviceInformation("DEVICE3", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER);
        String deviceId3 = "Device3";
        registry.devices.put(deviceId3, deviceInformation);
        LinkedHashMap<String, ArrayList<BezirkZirkId>> deviceServices = new LinkedHashMap<>();
        ArrayList<BezirkZirkId> device1ServiceSet = new ArrayList<BezirkZirkId>();
        device1ServiceSet.add(new BezirkZirkId("Service11"));
        device1ServiceSet.add(new BezirkZirkId("Service12"));
        device1ServiceSet.add(new BezirkZirkId("Service13"));
        deviceServices.put(deviceId1, device1ServiceSet);
        ArrayList<BezirkZirkId> device2ServiceSet = new ArrayList<BezirkZirkId>();
        device2ServiceSet.add(new BezirkZirkId("Service21"));
        device2ServiceSet.add(new BezirkZirkId("Service22"));
        device2ServiceSet.add(new BezirkZirkId("Service23"));
        deviceServices.put(deviceId2, device2ServiceSet);
        ArrayList<BezirkZirkId> device3ServiceSet = new ArrayList<BezirkZirkId>();
        device3ServiceSet.add(new BezirkZirkId("Service31"));
        device3ServiceSet.add(new BezirkZirkId("Service32"));
        device3ServiceSet.add(new BezirkZirkId("Service33"));
        deviceServices.put(deviceId3, device3ServiceSet);

        return deviceServices;
    }

    public ArrayList<BezirkDeviceInfo> getDeviceList() {
        ArrayList<BezirkDeviceInfo> deviceList = new ArrayList<BezirkDeviceInfo>();

        List<BezirkZirkInfo> services1 = new ArrayList<BezirkZirkInfo>();
        BezirkZirkInfo serviceInfo1 = new BezirkZirkInfo("Service11", "Service11", "LOCAL", true, true);
        BezirkZirkInfo serviceInfo2 = new BezirkZirkInfo("Service12", "Service12", "LOCAL", true, true);
        BezirkZirkInfo serviceInfo3 = new BezirkZirkInfo("Service13", "Service13", "LOCAL", true, true);
        services1.add(serviceInfo1);
        services1.add(serviceInfo2);
        services1.add(serviceInfo3);

        List<BezirkZirkInfo> services2 = new ArrayList<BezirkZirkInfo>();
        serviceInfo1 = new BezirkZirkInfo("Service21", "Service21", "LOCAL", true, true);
        serviceInfo2 = new BezirkZirkInfo("Service22", "Service22", "LOCAL", true, true);
        serviceInfo3 = new BezirkZirkInfo("Service23", "Service23", "LOCAL", true, true);
        services2.add(serviceInfo1);
        services2.add(serviceInfo2);
        services2.add(serviceInfo3);

        List<BezirkZirkInfo> services3 = new ArrayList<BezirkZirkInfo>();
        serviceInfo1 = new BezirkZirkInfo("Service31", "Service31", "LOCAL", true, true);
        serviceInfo2 = new BezirkZirkInfo("Service32", "Service32", "LOCAL", true, true);
        serviceInfo3 = new BezirkZirkInfo("Service33", "Service33", "LOCAL", true, true);
        services3.add(serviceInfo1);
        services3.add(serviceInfo2);
        services3.add(serviceInfo3);

        BezirkDeviceInfo bezirkDeviceInfo1 = new BezirkDeviceInfo("Device1", "Device1", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER,
                BezirkDeviceInfo.BezirkDeviceRole.BEZIRK_CONTROL, true, services1);
        BezirkDeviceInfo bezirkDeviceInfo2 = new BezirkDeviceInfo("Device2", "Device2", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER,
                BezirkDeviceRole.BEZIRK_MEMBER, true, services2);
        BezirkDeviceInfo bezirkDeviceInfo3 = new BezirkDeviceInfo("Device3", "Device3", BezirkDeviceType.BEZIRK_DEVICE_TYPE_OTHER,
                BezirkDeviceRole.BEZIRK_MEMBER, true, services3);

        deviceList.add(bezirkDeviceInfo1);
        deviceList.add(bezirkDeviceInfo2);
        deviceList.add(bezirkDeviceInfo3);
        return deviceList;
    }

    public String getDefaultSphereId(SphereRegistry registry) {

        String defaultSphereId = null;

        Iterator<String> sphereIterator = registry.spheres.keySet().iterator();
        if (sphereIterator.hasNext()) {

            String sphereId = sphereIterator.next();

            if (registry.spheres.get(sphereId).getSphereType()
                    .equalsIgnoreCase(BezirkSphereType.BEZIRK_SPHERE_TYPE_DEFAULT)) {
                logger.info("sphere id from registry : " + sphereId);

                defaultSphereId = new String(sphereId);
            }

        }
        logger.debug("Default sphereId : " + defaultSphereId);
        return defaultSphereId;

    }
}
