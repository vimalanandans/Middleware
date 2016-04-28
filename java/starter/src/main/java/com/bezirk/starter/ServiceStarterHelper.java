package com.bezirk.starter;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.device.BezirkDevice;
import com.bezirk.device.BezirkDeviceType;
import com.bezirk.devices.UPADeviceForPC;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.RegistryPersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.pipe.core.PipeManager;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.sphere.api.BezirkSphereRegistration;
import com.bezirk.sphere.impl.BezirkQRCode;
import com.bezirk.sphere.impl.BezirkSphereForPC;
import com.bezirk.sphere.security.CryptoEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

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
     * @param uhuDevice
     * @param registryPersistence
     * @param comms
     */
    BezirkSphereAPI initSphere(final UPADeviceInterface uhuDevice,
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
        BezirkSphereAPI sphereForPC = new BezirkSphereForPC(cryptoEngine, uhuDevice,
                sphereRegistry);

        // UhuSphereForAndroid implements the listener, hence set the
        // listener object as same.
        final BezirkSphereForPC uhuSphereForPC = (BezirkSphereForPC) sphereForPC;
        uhuSphereForPC.setBezirkSphereListener(uhuSphereForPC);
        ISphereConfig sphereConfig = new com.bezirk.sphere.impl.SphereProperties();
        sphereConfig.init();
        uhuSphereForPC.initSphere(registryPersistence, comms, sphereConfig);

        BezirkCompManager.setSphereUI(sphereForPC);
        BezirkCompManager
                .setSphereRegistration((BezirkSphereRegistration) sphereForPC);

        com.bezirk.sphere.SphereManager.setBezirkQRCode((BezirkQRCode) sphereForPC);

        final BezirkSphereForSadl sphereForSadl = (BezirkSphereForSadl) sphereForPC;
        BezirkCompManager.setSphereForSadl(sphereForSadl);

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
        Location location = null;
        try {
            final Properties props = UPADeviceForPC.loadProperties();

            final String loc = props.getProperty("DeviceLocation", null);
            location = new Location(loc);
        } catch (Exception e) {
            logger.error("Problem reading or writing properties file: ", e);
            return null;
        }

        return location;
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
