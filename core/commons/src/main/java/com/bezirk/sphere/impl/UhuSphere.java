/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.comms.IUhuComms;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.discovery.SphereDiscovery;
import com.bezirk.discovery.SphereDiscoveryProcessor;
import com.bezirk.middleware.objects.BezirkDeviceInfo;
import com.bezirk.middleware.objects.BezirkZirkInfo;
import com.bezirk.middleware.objects.UhuPipeInfo;
import com.bezirk.middleware.objects.UhuSphereInfo;
import com.bezirk.persistence.ISpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.sphere.api.IUhuSphereDiscovery;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.sphere.api.IUhuSphereListener;
import com.bezirk.sphere.api.IUhuSphereMessages;
import com.bezirk.sphere.api.IUhuSphereRegistration;
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
public class UhuSphere
        implements IUhuSphereAPI, IUhuSphereForSadl, IUhuSphereRegistration, IUhuSphereDiscovery, IUhuSphereMessages, IUhuDevMode {

    private static final Logger logger = LoggerFactory.getLogger(UhuSphere.class);
    private CryptoEngine cryptoEngine = null;
    private UPADeviceInterface upaDevice = null;
    private SphereRegistry registry = null;
    private IUhuSphereListener uhuSphereListener;
    private ISphereConfig sphereConfig = null;
    private SphereCtrlMsgReceiver ctrlMsgReceiver = null;
    private ShareProcessor shareProcessor = null;
    private CatchProcessor catchProcessor = null;
    private DiscoveryProcessor discoveryProcessor = null;
    private SphereRegistryWrapper sphereRegistryWrapper = null;

    public UhuSphere(CryptoEngine cryptoEngine, UPADeviceInterface upaDevice, SphereRegistry sphereRegistry) {

        if (cryptoEngine == null || upaDevice == null || sphereRegistry == null) {
            logger.error("Exiting UhuSphere setup. A parameter to the constructor is null");
            return;
        }

        this.cryptoEngine = cryptoEngine;
        this.upaDevice = upaDevice;
        this.registry = sphereRegistry;
        this.ctrlMsgReceiver = new SphereCtrlMsgReceiver(this);
    }

    /* Initialize uhu sphere */
    public boolean initSphere(ISpherePersistence spherePersistence, IUhuComms uhuComms,
                              IUhuSphereListener uhuSphereListener, ISphereConfig sphereConfig) {

        if (spherePersistence == null || uhuComms == null) {
            logger.error("Null passed to for ISpherePersistence or IUhuComms");
        }
        if (uhuSphereListener == null) {
            logger.warn("IUhuSphereListener passed as null");
        }

        if (sphereConfig == null) {
            logger.warn("sphere Configuration provided is null");
        }
        this.uhuSphereListener = uhuSphereListener;
        this.sphereConfig = sphereConfig;

        try {
            registry = spherePersistence.loadSphereRegistry();
            logger.info("sphere Registry loaded successfully");
        } catch (Exception e) {
            logger.error("Error in loading sphere Registry from Persistence. Uninstall the app and re-install", e);
            return false;
        }

        this.sphereRegistryWrapper = new SphereRegistryWrapper(this.registry, spherePersistence, upaDevice, cryptoEngine, uhuSphereListener, sphereConfig);
        this.sphereRegistryWrapper.init();
        CommsUtility comms = new CommsUtility(uhuComms);
        shareProcessor = new ShareProcessor(cryptoEngine, upaDevice, comms,
                sphereRegistryWrapper);
        catchProcessor = new CatchProcessor(cryptoEngine, upaDevice, comms,
                sphereRegistryWrapper);
        discoveryProcessor = new DiscoveryProcessor(upaDevice, comms, sphereRegistryWrapper,
                this.uhuSphereListener);

        // init the sphere for receiving sphere discovery message
        //uhuComms.initDiscovery(this);
        initSphereDiscovery(uhuComms);

        ctrlMsgReceiver.initControlMessageListener(uhuComms);
        return true;
    }

    /**
     * moved the init discovery from comms layer to sphere.
     * because this is out of comms layer
     */
    public void initSphereDiscovery(IUhuComms uhuComms) {
        // initialize the discovery here
        SphereDiscoveryProcessor.setDiscovery(new SphereDiscovery(this));

        Thread sphereDiscThread = new Thread(new SphereDiscoveryProcessor(this, uhuComms));

        if (sphereDiscThread != null)
            sphereDiscThread.start();

        //  add the sphere discovery stop

    }

    @Override
    public boolean registerService(BezirkZirkId serviceId, String serviceName) {
        return sphereRegistryWrapper.registerService(serviceId, serviceName);
    }

    @Override
    public boolean unregisterService(BezirkZirkId serviceId) {
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
    public Iterable<String> getSphereMembership(BezirkZirkId serviceId) {
        return sphereRegistryWrapper.getSphereMembership(serviceId);
    }

    @Override
    public boolean isZirkInSphere(BezirkZirkId service, String sphereId) {
        return sphereRegistryWrapper.isServiceInSphere(service, sphereId);
    }

    @Override
    public String getZirkName(BezirkZirkId serviceId) {
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
        return sphereRegistryWrapper.createSphere(sphereName, sphereType, uhuSphereListener);
    }

    @Override
    public boolean deleteSphere(String sphereId) {
        // While implementing the delete
        // feature, ensure that sphere being deleted is not the default sphere.
        return false;
    }

    @Override
    public Iterable<UhuSphereInfo> getSpheres() {
        return sphereRegistryWrapper.getSpheres();
    }

    @Override
    public UhuSphereInfo getSphere(String sphereId) {
        return sphereRegistryWrapper.getSphereInfo(sphereId);
    }

    @Override
    public boolean isThisDeviceOwnsSphere(UhuSphereInfo sphereInfo) {
        return sphereRegistryWrapper.isThisDeviceOwnsSphere(sphereInfo);
    }

    @Override
    public Iterable<BezirkDeviceInfo> getDevicesOnSphere(String sphereId) {
        // TODO implement
        return null;
    }

    @Override
    public Iterable<UhuPipeInfo> getPipesOnSphere(String sphereId) {
        // TODO implement
        return null;
    }

    @Override
    public Iterable<BezirkDeviceInfo> getOtherDevices(String sphereId) {
        // TODO implement
        return null;
    }

    @Override
    public boolean addLocalServicesToSphere(Iterable<BezirkZirkId> serviceIds, String sphereId) {
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
    public boolean expelServiceFromSphere(String serviceId, String sphereId) {
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
    public UhuSphereInfo processDiscoveryResponse(Set<BezirkDiscoveredZirk> discoveredServices, String sphereId) {
        return discoveryProcessor.processDiscoveryResponse(discoveredServices, sphereId);
    }

    @Override
    public void processDiscoveredSphereInfo(Set<UhuSphereInfo> discoveredSphereInfoSet, String sphereId) {
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
    public void setUhuSphereListener(IUhuSphereListener sphereListener) {
        this.uhuSphereListener = sphereListener;
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
