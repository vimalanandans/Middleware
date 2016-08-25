package com.bezirk.middleware.java.starter;

import com.bezirk.middleware.core.comms.Comms;
import com.bezirk.middleware.core.datastorage.RegistryStorage;
import com.bezirk.middleware.core.datastorage.SphereRegistry;
import com.bezirk.middleware.core.device.Device;
import com.bezirk.middleware.java.sphere.SphereManager;
import com.bezirk.middleware.core.networking.NetworkManager;
import com.bezirk.middleware.core.sphere.api.SphereAPI;
import com.bezirk.middleware.core.sphere.api.SphereConfig;
import com.bezirk.middleware.core.sphere.api.SphereSecurity;
import com.bezirk.middleware.java.sphere.impl.BezirkQRCode;
import com.bezirk.middleware.java.sphere.impl.JavaPrefs;
import com.bezirk.middleware.java.sphere.impl.PCSphereServiceManager;
import com.bezirk.middleware.core.sphere.security.CryptoEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a helper class for zirk startup which handles the sphere
 * initialization and device configuration.
 */
final class ServiceStarterHelper {
    private static final Logger logger = LoggerFactory.getLogger(ServiceStarterHelper.class);

    /**
     * create and initialise the sphere
     */
    SphereAPI initSphere(final Device bezirkDevice,
                         final RegistryStorage registryPersistence, final Comms comms, final NetworkManager networkManager) {

        // init the actual
        SphereRegistry sphereRegistry = null;
        try {
            sphereRegistry = registryPersistence.loadSphereRegistry();
        } catch (Exception e) {
            logger.error("Error in loading sphere Registry", e);
        }

        final CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
        SphereAPI sphereForPC = new PCSphereServiceManager(cryptoEngine, bezirkDevice,
                sphereRegistry, networkManager);

        // BezirkSphereForAndroid implements the listener, hence set the
        // listener object as same.
        final PCSphereServiceManager bezirkSphereForPC = (PCSphereServiceManager) sphereForPC;
        bezirkSphereForPC.setSphereListener(bezirkSphereForPC);
        //SphereConfig sphereConfig = new com.bezirk.sphere.impl.SphereProperties();
        //sphereConfig.init();
        SphereConfig sphereConfig = new JavaPrefs();
        sphereConfig.init();
        bezirkSphereForPC.initSphere(registryPersistence, comms, sphereConfig);


        SphereManager.setBezirkQRCode((BezirkQRCode) sphereForPC);

        final SphereSecurity sphereForSadl = (SphereSecurity) sphereForPC;


        try {

            final Comms bezirkComms = comms;
            /*bezirkComms.setSphereSecurity(sphereForSadl);*/

        } catch (Exception e) {

            logger.error(
                    "Comms should also implement BezirkCommsLegacy to set sphere for sadl.",
                    e);
            sphereForPC = null;
        }

        return sphereForPC;
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
