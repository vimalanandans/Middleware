package com.bosch.upa.uhu.starter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.devices.UPADeviceForPC;
import com.bosch.upa.devices.UPADeviceInterface;
import com.bezirk.api.addressing.Location;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.device.UhuDevice;
import com.bosch.upa.uhu.device.UhuDeviceType;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.RegistryPersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.pipe.core.PipeManager;
import com.bosch.upa.uhu.sphere.SphereManager;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuSphereAPI;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.sphere.api.IUhuSphereRegistration;
import com.bosch.upa.uhu.sphere.impl.IUhuQRCode;
import com.bosch.upa.uhu.sphere.impl.SphereProperties;
import com.bosch.upa.uhu.sphere.impl.UhuSphereForPC;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;

/**
 * This is a helper class for service startup which handles the sphere
 * initialization and device configuration.
 * 
 * @author ajc6kor
 * 
 */
final class ServiceStarterHelper {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ServiceStarterHelper.class);

    /**
     * create and initialise the Sphere
     * 
     * @param comms
     * @param registryPersistence
     * @param sphereForPC
     * */
    IUhuSphereAPI initSphere(final UPADeviceInterface uhuDevice,
            final RegistryPersistence registryPersistence, final IUhuComms comms) {

        // init the actual
        final ISpherePersistence spherePersistence = registryPersistence;
        SphereRegistry sphereRegistry = null;
        try {
            sphereRegistry = spherePersistence.loadSphereRegistry();
        } catch (Exception e) {
            LOGGER.error("Error in loading Sphere Registry", e);
        }

        final CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
        IUhuSphereAPI sphereForPC = new UhuSphereForPC(cryptoEngine, uhuDevice,
                sphereRegistry);

        // UhuSphereForAndroid implements the listener, hence set the
        // listener object as same.
        final UhuSphereForPC uhuSphereForPC = (UhuSphereForPC) sphereForPC;
        uhuSphereForPC.setUhuSphereListener(uhuSphereForPC);
        ISphereConfig sphereConfig = new SphereProperties();
        sphereConfig.init();
        uhuSphereForPC.initSphere(registryPersistence, comms, sphereConfig);

        UhuCompManager.setSphereUI(sphereForPC);
        UhuCompManager
                .setSphereRegistration((IUhuSphereRegistration) sphereForPC);

        SphereManager.setUhuQRCode((IUhuQRCode) sphereForPC);

        final IUhuSphereForSadl sphereForSadl = (IUhuSphereForSadl) sphereForPC;
        UhuCompManager.setSphereForSadl(sphereForSadl);

        try {

            final IUhuComms iUhuComms = (IUhuComms) comms;
            iUhuComms.setSphereForSadl(sphereForSadl);

        } catch (Exception e) {

            LOGGER.error(
                    "Comms should also implement IUhuCommsLegacy to set sphere for sadl.",
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
            LOGGER.error("Problem reading or writing properties file: ", e);
            return null;
        }

        return location;
    }

    /**
     * Initializes the UhuDevice and configures the location
     * 
     * @param uhuConfig
     * @return
     */
    UhuDevice configureUhuDevice(final UhuConfig uhuConfig) {
        final UhuDevice uhuDevice = new UhuDevice();

        String deviceIdString = null;

        try {
            deviceIdString = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOGGER.error("Exception in fetching hostname.", e);
        }

        if (uhuConfig.isDisplayEnabled()) {
            uhuDevice.initDevice(deviceIdString,
                    UhuDeviceType.UHU_DEVICE_TYPE_PC);
        } else {
            uhuDevice.initDevice(deviceIdString,
                    UhuDeviceType.UHU_DEVICE_TYPE_EMBEDDED_KIT);
        }

        UhuCompManager.setUpaDevice(uhuDevice);

        // Load Location
        final Location deviceLocation = loadLocation();
        uhuDevice.setDeviceLocation(deviceLocation);

        return uhuDevice;
    }

    /**
     * deinitialize the Sphere
     * */
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
     * @param errorMsg
     *            Message to log before exiting
     * @param throwable
     *            May be null if not relevant
     */
    void fail(final String errorMsg, final Throwable throwable) {
        LOGGER.error(errorMsg, throwable);
        System.exit(0);
    }

}
