package com.bezirk.starter;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.device.BezirkDevice;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.BezirkDeviceForPC;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.pipe.PipeManager;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereForPubSub;
import com.bezirk.sphere.api.BezirkSphereRegistration;
import com.bezirk.sphere.impl.BezirkQRCode;
import com.bezirk.sphere.impl.BezirkSphereForPC;
import com.bezirk.sphere.impl.JavaPrefs;
import com.bezirk.sphere.security.CryptoEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is a helper class for zirk startup which handles the sphere
 * initialization and device configuration.
 *
 * @author ajc6kor
 */
final class ServiceStarterHelper {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarterHelper.class);

    /**
     * create and initialise the sphere
     *
     * @param bezirkDevice
     * @param registryPersistence
     * @param comms
     */
    BezirkSphereAPI initSphere(final BezirkDeviceInterface bezirkDevice,
                               final RegistryPersistence registryPersistence, final BezirkComms comms) {

        // init the actual
        final SpherePersistence spherePersistence = registryPersistence;
        SphereRegistry sphereRegistry = null;
        try {
            sphereRegistry = spherePersistence.loadSphereRegistry();
        } catch (Exception e) {
            logger.error("Error in loading sphere Registry", e);
        }

        final CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
        BezirkSphereAPI sphereForPC = new BezirkSphereForPC(cryptoEngine, bezirkDevice,
                sphereRegistry);

        // BezirkSphereForAndroid implements the listener, hence set the
        // listener object as same.
        final BezirkSphereForPC bezirkSphereForPC = (BezirkSphereForPC) sphereForPC;
        bezirkSphereForPC.setBezirkSphereListener(bezirkSphereForPC);
        //ISphereConfig sphereConfig = new com.bezirk.sphere.impl.SphereProperties();
        //sphereConfig.init();
        ISphereConfig sphereConfig = new JavaPrefs();
        sphereConfig.init();
        bezirkSphereForPC.initSphere(registryPersistence, comms, sphereConfig);

        BezirkCompManager.setSphereUI(sphereForPC);
        BezirkCompManager
                .setSphereRegistration((BezirkSphereRegistration) sphereForPC);

        com.bezirk.sphere.SphereManager.setBezirkQRCode((BezirkQRCode) sphereForPC);

        final BezirkSphereForPubSub sphereForSadl = (BezirkSphereForPubSub) sphereForPC;
        BezirkCompManager.setSphereForPubSub(sphereForSadl);

        try {

            final BezirkComms bezirkComms = (BezirkComms) comms;
            bezirkComms.setSphereForSadl(sphereForSadl);

        } catch (Exception e) {

            logger.error(
                    "Comms should also implement BezirkCommsLegacy to set sphere for sadl.",
                    e);
            sphereForPC = null;
        }

        return sphereForPC;
    }

    /**
     * Loads the default location for UPA device from the properties.
     */
    private Location loadLocation() {
        return BezirkDeviceForPC.deviceLocation;
    }

    /**
     * Initializes the BezirkDevice and configures the location
     *
     * @param bezirkConfig
     * @return
     */
    BezirkDevice configureBezirkDevice(final BezirkConfig bezirkConfig) {
        final BezirkDevice bezirkDevice = new BezirkDevice();

        String deviceIdString = null;

        try {
            deviceIdString = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Exception in fetching hostname.", e);
        }

        if (bezirkConfig.isDisplayEnabled()) {
            bezirkDevice.initDevice(deviceIdString,
                    BezirkDeviceType.BEZIRK_DEVICE_TYPE_PC);
        } else {
            bezirkDevice.initDevice(deviceIdString,
                    BezirkDeviceType.BEZIRK_DEVICE_TYPE_EMBEDDED_KIT);
        }

        BezirkCompManager.setUpaDevice(bezirkDevice);

        // Load Location
        final Location deviceLocation = loadLocation();
        bezirkDevice.setDeviceLocation(deviceLocation);

        return bezirkDevice;
    }

    /**
     * deinitialize the sphere
     */
    boolean deinitSphere(final MainService service) {
        // clear the reference
        service.sphereForPC = null;

        return true;
    }

    PipeManager createPipeManager() {
        return null;
    }

    /**
     * Log the error message with an optional Throwable and then exit
     *
     * @param errorMsg  Message to logger before exiting
     * @param throwable May be null if not relevant
     */
    void fail(final String errorMsg, final Throwable throwable) {
        logger.error(errorMsg, throwable);
        System.exit(0);
    }

}
