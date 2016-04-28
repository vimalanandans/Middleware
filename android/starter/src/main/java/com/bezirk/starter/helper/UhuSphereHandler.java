package com.bezirk.starter.helper;

import com.bezirk.commons.UhuCompManager;
import com.bezirk.comms.IUhuComms;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.persistence.SpherePersistence;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ISphereConfig;
import com.bezirk.sphere.api.IUhuDevMode;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.sphere.api.IUhuSphereRegistration;
import com.bezirk.sphere.impl.SphereProperties;
import com.bezirk.sphere.impl.UhuSphereForAndroid;
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
    static IUhuSphereAPI sphereForAndroid;
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

            sphereForAndroid = new UhuSphereForAndroid(cryptoEngine, uhuDevice, sphereRegistry, service.getApplicationContext(), preferences);

            UhuSphereForAndroid uhuSphereForAndroid = (UhuSphereForAndroid) UhuSphereHandler.sphereForAndroid;

            uhuSphereForAndroid.setUhuSphereListener(uhuSphereForAndroid);

            ISphereConfig sphereConfig = new SphereProperties(preferences);
            sphereConfig.init();

            if (!(uhuSphereForAndroid.initSphere(spherePersistence, UhuStackHandler.getUhuComms()))) {
                // at the moment the init sphere fails due to persistence
                return false;
            }

            devMode = (IUhuDevMode) sphereForAndroid;

            UhuCompManager.setSphereUI(sphereForAndroid);
            UhuCompManager.setSphereRegistration((IUhuSphereRegistration) sphereForAndroid);

            UhuCompManager.setSphereForSadl((IUhuSphereForSadl) sphereForAndroid);
            IUhuComms uhuComms = UhuStackHandler.getUhuComms();
            uhuComms.setSphereForSadl((IUhuSphereForSadl) sphereForAndroid);
        }
        return true;
    }

}
