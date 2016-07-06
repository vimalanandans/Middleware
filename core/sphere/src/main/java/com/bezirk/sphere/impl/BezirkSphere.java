/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.comms.BezirkComms;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.discovery.SphereDiscovery;
import com.bezirk.discovery.SphereDiscoveryProcessor;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.BezirkPipeInfo;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.BezirkSphereForPubSub;
import com.bezirk.sphere.api.BezirkSphereMessages;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkDevMode;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereDiscovery;
import com.bezirk.sphere.api.BezirkSphereListener;
import com.bezirk.sphere.api.BezirkSphereRegistration;
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
import java.util.Set;

/**
 * @author rishabh
 */
public class BezirkSphere
        implements BezirkSphereAPI, BezirkSphereForPubSub, BezirkSphereRegistration, BezirkSphereDiscovery, BezirkSphereMessages, BezirkDevMode {

    private static final Logger logger = LoggerFactory.getLogger(BezirkSphere.class);
    private CryptoEngine cryptoEngine = null;
    private BezirkDeviceInterface upaDevice = null;
    private SphereRegistry registry = null;
    private BezirkSphereListener bezirkSphereListener;
    private ISphereConfig sphereConfig = null;
    private SphereCtrlMsgReceiver ctrlMsgReceiver = null;
    private ShareProcessor shareProcessor = null;
    private CatchProcessor catchProcessor = null;
    private DiscoveryProcessor discoveryProcessor = null;
    private SphereRegistryWrapper sphereRegistryWrapper = null;

    public BezirkSphere(CryptoEngine cryptoEngine, BezirkDeviceInterface upaDevice, SphereRegistry sphereRegistry) {

        if (cryptoEngine == null || upaDevice == null || sphereRegistry == null) {
            logger.error("Exiting BezirkSphere setup. A parameter to the constructor is null");
            return;
        }

        this.cryptoEngine = cryptoEngine;
        this.upaDevice = upaDevice;
        this.registry = sphereRegistry;
        this.ctrlMsgReceiver = new SphereCtrlMsgReceiver(this);
    }

    /* Initialize bezirk sphere */
    public boolean initSphere(SpherePersistence spherePersistence, BezirkComms bezirkComms,
                              BezirkSphereListener bezirkSphereListener, ISphereConfig sphereConfig) {

        if (spherePersistence == null || bezirkComms == null) {
            logger.error("Null passed to for SpherePersistence or BezirkComms");
        }
        if (bezirkSphereListener == null) {
            logger.warn("BezirkSphereListener passed as null");
        }

        if (sphereConfig == null) {
            logger.warn("sphere Configuration provided is null");
        }
        this.bezirkSphereListener = bezirkSphereListener;
        this.sphereConfig = sphereConfig;

        try {
            registry = spherePersistence.loadSphereRegistry();
            logger.info("sphere Registry loaded successfully");
        } catch (Exception e) {
            logger.error("Error in loading sphere Registry from Persistence. Uninstall the app and re-install", e);
            return false;
        }

        this.sphereRegistryWrapper = new SphereRegistryWrapper(this.registry, spherePersistence, upaDevice, cryptoEngine, bezirkSphereListener, sphereConfig);
        this.sphereRegistryWrapper.init();
        CommsUtility comms = new CommsUtility(bezirkComms);
        shareProcessor = new ShareProcessor(cryptoEngine, upaDevice, comms,
                sphereRegistryWrapper);
        catchProcessor = new CatchProcessor(cryptoEngine, upaDevice, comms,
                sphereRegistryWrapper);
        discoveryProcessor = new DiscoveryProcessor(upaDevice, comms, sphereRegistryWrapper,
                this.bezirkSphereListener);

        // init the sphere for receiving sphere discovery message
        //bezirkComms.initDiscovery(this);
        initSphereDiscovery(bezirkComms);

        ctrlMsgReceiver.initControlMessageListener(bezirkComms);
        return true;
    }

    /**
     * moved the init discovery from comms layer to sphere.
     * because this is out of comms layer
     */
    public void initSphereDiscovery(BezirkComms bezirkComms) {
        // initialize the discovery here
        SphereDiscoveryProcessor.setDiscovery(new SphereDiscovery(this));

        Thread sphereDiscThread = new Thread(new SphereDiscoveryProcessor(this, bezirkComms));

        if (sphereDiscThread != null)
            sphereDiscThread.start();

        //  add the sphere discovery stop

    }

    @Override
    public boolean registerZirk(ZirkId zirkId, String zirkName) {
        return sphereRegistryWrapper.registerService(zirkId, zirkName);
    }

    @Override
    public boolean unregisterZirk(ZirkId serviceId) {
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
    public boolean isZirkInSphere(ZirkId service, String sphereId) {
        return sphereRegistryWrapper.isServiceInSphere(service, sphereId);
    }

    @Override
    public String getZirkName(ZirkId serviceId) {
        return sphereRegistryWrapper.getServiceName(serviceId);
    }

    @Override
    public void processSphereDiscoveryRequest(DiscoveryRequest discoveryRequest) {
        discoveryProcessor.processRequest(discoveryRequest);
    }

    @Override
    public String getDeviceNameFromSphere(final String deviceId) {
        return sphereRegistryWrapper.getDeviceName(deviceId);
    }

    @Override
    public String createSphere(String sphereName, String sphereType) {
        return sphereRegistryWrapper.createSphere(sphereName, sphereType, bezirkSphereListener);
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
    public Iterable<BezirkPipeInfo> getPipesOnSphere(String sphereId) {
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
    public boolean discoverSphere(String sphereId) {
        return discoveryProcessor.discoverSphere(sphereId);
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

    /**
     * @deprecated use {@link #processDiscoveredSphereInfo(Set, String)} instead
     */
    @Deprecated
    public BezirkSphereInfo processDiscoveryResponse(Set<BezirkDiscoveredZirk> discoveredServices, String sphereId) {
        return discoveryProcessor.processDiscoveryResponse(discoveredServices, sphereId);
    }

    @Override
    public void processDiscoveredSphereInfo(Set<BezirkSphereInfo> discoveredSphereInfoSet, String sphereId) {
        discoveryProcessor.processDiscoveredSphereInfo(discoveredSphereInfoSet, sphereId);
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
    public void setBezirkSphereListener(BezirkSphereListener sphereListener) {
        this.bezirkSphereListener = sphereListener;
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
