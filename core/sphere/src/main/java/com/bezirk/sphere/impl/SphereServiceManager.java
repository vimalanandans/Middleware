package com.bezirk.sphere.impl;

import com.bezirk.comms.Comms;
import com.bezirk.datastorage.SpherePersistence;
import com.bezirk.datastorage.SphereRegistry;
import com.bezirk.device.Device;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.networking.NetworkManager;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.sphere.api.SphereConfig;
import com.bezirk.sphere.api.SphereListener;
import com.bezirk.sphere.api.SphereMessages;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.sphere.messages.CatchRequest;
import com.bezirk.sphere.messages.CatchResponse;
import com.bezirk.sphere.messages.ShareRequest;
import com.bezirk.sphere.messages.ShareResponse;
import com.bezirk.sphere.security.CryptoEngine;
import com.google.zxing.common.BitMatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class SphereServiceManager
        implements SphereAPI, SphereServiceAccess, SphereSecurity, SphereMessages, DevMode {

    private static final Logger logger = LoggerFactory.getLogger(SphereServiceManager.class);

    private final CryptoEngine cryptoEngine;
    private final Device device;
    private SphereRegistry registry;
    private final NetworkManager networkManager;
    private final SphereCtrlMsgReceiver ctrlMsgReceiver;

    private SphereListener sphereListener;
    private SphereConfig sphereConfig = null;
    private ShareProcessor shareProcessor = null;
    private CatchProcessor catchProcessor = null;
    private SphereRegistryWrapper sphereRegistryWrapper = null;

    public SphereServiceManager(CryptoEngine cryptoEngine, Device device, SphereRegistry sphereRegistry, NetworkManager networkManager) {
        if (cryptoEngine == null || device == null || sphereRegistry == null || networkManager == null) {
            logger.error("Cannot initialize SphereServiceManager. A parameter to the constructor is null. " +
                    "cryptoEngine = {}, device = {}, sphereRegistry = {}, networkManager = {}",
                    cryptoEngine, device, sphereRegistry, networkManager);
            throw new IllegalArgumentException("SphereServiceManager parameters cannot be null.");
        }

        this.cryptoEngine = cryptoEngine;
        this.device = device;
        this.registry = sphereRegistry;
        this.networkManager = networkManager;
        this.ctrlMsgReceiver = new SphereCtrlMsgReceiver(this);
    }

    /* Initialize bezirk sphere */
    public boolean initSphere(SpherePersistence spherePersistence, Comms bezirkComms,
                              SphereListener sphereListener, SphereConfig sphereConfig) {

        if (spherePersistence == null || bezirkComms == null) {
            logger.error("Null passed to for SpherePersistence or Comms");
        }
        if (sphereListener == null) {
            logger.warn("SphereListener passed as null");
        }

        if (sphereConfig == null) {
            logger.warn("sphere Configuration provided is null");
        }
        this.sphereListener = sphereListener;
        this.sphereConfig = sphereConfig;

        try {
            // TODO: Why is this also set in the ctor?
            registry = spherePersistence.loadSphereRegistry();
            logger.info("sphere Registry loaded successfully");
        } catch (Exception e) {
            logger.error("Error in loading sphere Registry from Persistence. Uninstall the app and re-install", e);
            return false;
        }

        this.sphereRegistryWrapper = new SphereRegistryWrapper(this.registry, spherePersistence, device, cryptoEngine, sphereListener, sphereConfig);
        this.sphereRegistryWrapper.init();
        CommsUtility comms = new CommsUtility(bezirkComms);
        shareProcessor = new ShareProcessor(cryptoEngine, device, comms,
                sphereRegistryWrapper, networkManager);
        catchProcessor = new CatchProcessor(cryptoEngine, device, comms,
                sphereRegistryWrapper, networkManager);

        ctrlMsgReceiver.initControlMessageListener(bezirkComms);
        return true;
    }

    @Override
    public boolean registerService(ZirkId zirkId, String zirkName) {
        return sphereRegistryWrapper.registerService(zirkId, zirkName);
    }

    @Override
    public boolean unregisterService(ZirkId serviceId) {
        // TODO Implement
        return false;
    }

    @Override
    public byte[] encryptSphereContent(String sphereId, String serializedContent) {
        return cryptoEngine.encryptSphereContent(sphereId, serializedContent);
    }

    @Override
    public String decryptSphereContent(String sphereId, byte[] serializedContent) {
        return cryptoEngine.decryptSphereContent(sphereId, serializedContent);
    }

    @Override
    public void encryptSphereContent(InputStream in, OutputStream out, String sphereId) {
        cryptoEngine.encryptSphereContent(in, out, sphereId);
    }

    @Override
    public void decryptSphereContent(InputStream in, OutputStream out, String sphereId) {
        cryptoEngine.decryptSphereContent(in, out, sphereId);
    }

    @Override
    public Iterable<String> getSphereMembership(ZirkId zirkId) {
        return sphereRegistryWrapper.getSphereMembership(zirkId);
    }

    @Override
    public boolean isServiceInSphere(ZirkId service, String sphereId) {
        return sphereRegistryWrapper.isServiceInSphere(service, sphereId);
    }

    @Override
    public String getServiceName(ZirkId serviceId) {
        return sphereRegistryWrapper.getServiceName(serviceId);
    }

    @Override
    public String getDeviceNameFromSphere(final String deviceId) {
        return sphereRegistryWrapper.getDeviceName(deviceId);
    }

    @Override
    public String createSphere(String sphereName, String sphereType) {
        return sphereRegistryWrapper.createSphere(sphereName, sphereType, sphereListener);
    }

    @Override
    public boolean deleteSphere(String sphereId) {
        // While implementing the delete
        // feature, ensure that sphere being deleted is not the default sphere.
        return false;
    }

    @Override
    public Iterable<BezirkSphereInfo> getSpheres() {
        return sphereRegistryWrapper.getSpheres();
    }

    @Override
    public BezirkSphereInfo getSphere(String sphereId) {
        return sphereRegistryWrapper.getSphereInfo(sphereId);
    }

    @Override
    public boolean isThisDeviceOwnsSphere(BezirkSphereInfo sphereInfo) {
        return sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo);
    }

    @Override
    public Iterable<BezirkDeviceInfo> getDevicesOnSphere(String sphereId) {
        // TODO implement
        return null;
    }

    @Override
    public Iterable<BezirkDeviceInfo> getOtherDevices(String sphereId) {
        // TODO implement
        return null;
    }

    @Override
    public boolean addLocalServicesToSphere(Iterable<ZirkId> serviceIds, String sphereId) {
        return sphereRegistryWrapper.addLocalServicesToSphere(serviceIds, sphereId);
    }

    @Override
    public boolean addLocalServicesToSphere(String sphereId, Iterable<BezirkZirkInfo> serviceInfo) {
        return sphereRegistryWrapper.addLocalServicesToSphere(sphereId, serviceInfo);
    }

    @Override
    public boolean addLocalServicesToSphere(String sphereId) {
        return sphereRegistryWrapper.addLocalServicesToSphere(sphereId);
    }

    @Override
    public boolean serviceLeaveRequest(String serviceId, String sphereId) {
        // TODO implement
        return false;
    }

    @Override
    public boolean expelServiceFromSphere(String zirkId, String sphereId) {
        // TODO implement
        return false;
    }

    @Override
    public boolean expelDeviceFromSphere(String deviceId, String sphereId) {
        // TODO implement
        return false;
    }

    @Override
    public BitMatrix getQRCodeMatrix(String sphereId) {
        return sphereRegistryWrapper.getQRCodeMatrix(sphereId);
    }

    @Override
    public BitMatrix getQRCodeMatrix(String sphereId, int width, int height) {
        return sphereRegistryWrapper.getQRCodeMatrix(sphereId, width, height);
    }

    @Override
    public boolean processShareQRCode(String qrcodeString, String sphereId) {
        return shareProcessor.processShareCode(qrcodeString, sphereId);
    }

    @Override
    public boolean processCatchQRCodeRequest(String qrcodeString, String joinSphereId) {
        return catchProcessor.processCatchCode(qrcodeString, joinSphereId);
    }

    @Override
    public List<BezirkZirkInfo> getServiceInfo() {
        return sphereRegistryWrapper.getServiceInfo();
    }

//    public boolean createDefaultSphere(String defaultSphereName) {
//        return sphereRegistryWrapper.createDefaultSphere(defaultSphereName);
//    }

    public boolean processCatchShortCode(String shortCode) {
        return processCatchQRCodeRequest(shortCode, sphereRegistryWrapper.getDefaultSphereId());
    }

    /**
     * this returns the short string for qr code
     */
    public String getShareCode(String sphereId) {
        return sphereRegistryWrapper.getShareCode(sphereId);
    }

    public String getShareCodeString(String sphereId) {
        return sphereRegistryWrapper.getShareCodeString(sphereId);
    }

    public boolean prepareAndSendCatchRequest(String catchSphereId, String tempShareSphereId) {
        return catchProcessor.processCatchCode(catchSphereId, tempShareSphereId);
    }

    @Override
    public boolean processCatchRequestExt(CatchRequest request) {
        return catchProcessor.processRequest(request);
    }

    @Override
    public boolean processCatchResponse(CatchResponse catchResponseMsg) {
        return catchProcessor.processResponse(catchResponseMsg);
    }

    @Override
    public void processShareResponse(ShareResponse response) {
        shareProcessor.processResponse(response);
    }

    @Override
    public void processShareRequest(ShareRequest shareRequest) {
        shareProcessor.processRequest(shareRequest);
    }

    protected String getDefaultSphereId() {
        return sphereRegistryWrapper.getDefaultSphereId();
    }

    /**
     * set the listener object
     */
    public void setSphereListener(SphereListener sphereListener) {
        this.sphereListener = sphereListener;
    }

    @Override
    public boolean switchMode(Mode mode) {
        return sphereRegistryWrapper.switchMode(mode);
    }

    @Override
    public Mode getStatus() {
        return sphereConfig.getMode();
    }

}
