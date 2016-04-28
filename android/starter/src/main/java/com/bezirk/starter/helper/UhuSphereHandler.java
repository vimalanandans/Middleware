package com.bezirk.starter.helper;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.IUhuComms;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereRegistration;
import com.bezirk.sphere.impl.SphereProperties;
import com.bezirk.sphere.impl.BezirkSphereForAndroid;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the sphere initialization for MainService
 * <p/>
 * Created by AJC6KOR on 9/8/2015.
 */
public final class UhuSphereHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuSphereHandler.class);
    static BezirkSphereAPI sphereForAndroid;
    static IUhuDevMode devMode;

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
    boolean initSphere(UPADeviceInterface uhuDevice, MainService service, SpherePersistence spherePersistence, UhuPreferences preferences) {

        /** start the sphere related init*/
        if (sphereForAndroid == null) {
            // init the actual
            SphereRegistry sphereRegistry = null;
            try {
                sphereRegistry = spherePersistence.loadSphereRegistry();
            } catch (Exception e) {
                LOGGER.error("Error in loading sphere Persistence", e);
            }
            CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);

            sphereForAndroid = new BezirkSphereForAndroid(cryptoEngine, uhuDevice, sphereRegistry, service.getApplicationContext(), preferences);

            BezirkSphereForAndroid uhuSphereForAndroid = (BezirkSphereForAndroid) UhuSphereHandler.sphereForAndroid;

            uhuSphereForAndroid.setUhuSphereListener(uhuSphereForAndroid);

            ISphereConfig sphereConfig = new SphereProperties(preferences);
            sphereConfig.init();

            if (!(uhuSphereForAndroid.initSphere(spherePersistence, UhuStackHandler.getUhuComms()))) {
                // at the moment the init sphere fails due to persistence
                return false;
            }

            devMode = (IUhuDevMode) sphereForAndroid;

            BezirkCompManager.setSphereUI(sphereForAndroid);
            BezirkCompManager.setSphereRegistration((BezirkSphereRegistration) sphereForAndroid);

            BezirkCompManager.setSphereForSadl((BezirkSphereForSadl) sphereForAndroid);
            IUhuComms uhuComms = UhuStackHandler.getUhuComms();
            uhuComms.setSphereForSadl((BezirkSphereForSadl) sphereForAndroid);
        }
        return true;
    }

}
