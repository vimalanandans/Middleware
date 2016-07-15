package com.bezirk.starter.helper;

import com.bezirk.BezirkCompManager;
import com.bezirk.comms.Comms;
import com.bezirk.devices.DeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.AndroidSphereServiceManager;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.sphere.api.SphereConfig;
import com.bezirk.sphere.api.DevMode;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.sphere.SphereProperties;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.starter.MainService;
import com.bezirk.starter.BezirkPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the sphere initialization for MainService
 */
public final class BezirkSphereHandler {
    private static final Logger logger = LoggerFactory.getLogger(BezirkSphereHandler.class);

    static SphereAPI sphereForAndroid;
    static DevMode devMode;

    /**
     * deinitialize the sphere
     */
    boolean deinitSphere() {

        // clear the reference
        sphereForAndroid = null;


        return true;
    }

    /**
     * create and initialise the sphere
     */
    boolean initSphere(DeviceInterface bezirkDevice, MainService service, SpherePersistence spherePersistence, BezirkPreferences preferences) {

        /** start the sphere related init*/
        if (sphereForAndroid == null) {
            // init the actual
            SphereRegistry sphereRegistry = null;
            try {
                sphereRegistry = spherePersistence.loadSphereRegistry();
            } catch (Exception e) {
                logger.error("Error in loading sphere Persistence", e);
            }
            CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);

            sphereForAndroid = new AndroidSphereServiceManager(cryptoEngine, bezirkDevice, sphereRegistry, service.getApplicationContext(), preferences);

            AndroidSphereServiceManager bezirkSphereForAndroid = (AndroidSphereServiceManager) BezirkSphereHandler.sphereForAndroid;

            bezirkSphereForAndroid.setSphereListener(bezirkSphereForAndroid);

            SphereConfig sphereConfig = new SphereProperties(preferences);
            sphereConfig.init();

            if (!(bezirkSphereForAndroid.initSphere(spherePersistence, BezirkStackHandler.getBezirkComms()))) {
                // at the moment the init sphere fails due to persistence
                return false;
            }

            devMode = (DevMode) sphereForAndroid;

            BezirkCompManager.setSphereUI(sphereForAndroid);
            BezirkCompManager.setSphereSecurity((SphereSecurity) sphereForAndroid);

            BezirkCompManager.setSphereForPubSub((SphereServiceAccess) sphereForAndroid);
            Comms uhuComms = BezirkStackHandler.getBezirkComms();
            uhuComms.setSphereSecurity((SphereSecurity) sphereForAndroid);
        }
        return true;
    }

}
