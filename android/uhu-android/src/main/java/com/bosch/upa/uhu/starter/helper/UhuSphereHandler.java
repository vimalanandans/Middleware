package com.bosch.upa.uhu.starter.helper;

import com.bosch.upa.devices.UPADeviceInterface;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.comms.IUhuComms;
import com.bosch.upa.uhu.persistence.ISpherePersistence;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.sphere.api.ISphereConfig;
import com.bosch.upa.uhu.sphere.api.IUhuDevMode;
import com.bosch.upa.uhu.sphere.api.IUhuSphereAPI;
import com.bosch.upa.uhu.sphere.api.IUhuSphereForSadl;
import com.bosch.upa.uhu.sphere.api.IUhuSphereRegistration;
import com.bosch.upa.uhu.sphere.impl.SphereProperties;
import com.bosch.upa.uhu.sphere.impl.UhuSphereForAndroid;
import com.bosch.upa.uhu.sphere.security.CryptoEngine;
import com.bosch.upa.uhu.starter.MainService;
import com.bosch.upa.uhu.starter.UhuPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the sphere initialization for MainService
 *
 * Created by AJC6KOR on 9/8/2015.
 */
public final class UhuSphereHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UhuSphereHandler.class);
    static IUhuSphereAPI sphereForAndroid;
    static IUhuDevMode devMode;

    /**
     * deinitialize the Sphere
     * */
    boolean deinitSphere()
    {

        // clear the reference
        sphereForAndroid = null;


        return true;
    }

    /**
     * create and initialise the Sphere
     * */
    boolean initSphere(UPADeviceInterface uhuDevice,MainService service, ISpherePersistence spherePersistence, UhuPreferences preferences) {

        /** start the sphere related init*/
        if(sphereForAndroid == null) {
            // init the actual
            SphereRegistry sphereRegistry = null;
            try {
                sphereRegistry = spherePersistence.loadSphereRegistry();
            } catch (Exception e) {
                LOGGER.error("Error in loading Sphere Persistence",e);
            }
            CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);

            sphereForAndroid = new UhuSphereForAndroid(cryptoEngine, uhuDevice, sphereRegistry,service.getApplicationContext(), preferences);

            UhuSphereForAndroid uhuSphereForAndroid = (UhuSphereForAndroid) UhuSphereHandler.sphereForAndroid;

            uhuSphereForAndroid.setUhuSphereListener(uhuSphereForAndroid);

            ISphereConfig sphereConfig = new SphereProperties(preferences);
            sphereConfig.init();

            if(!(uhuSphereForAndroid.initSphere(spherePersistence, UhuStackHandler.getUhuComms()))){
                // at the moment the init sphere fails due to persistence
                return false;
            }

            devMode = (IUhuDevMode)sphereForAndroid;

            UhuCompManager.setSphereUI(sphereForAndroid);
            UhuCompManager.setSphereRegistration((IUhuSphereRegistration) sphereForAndroid);

            UhuCompManager.setSphereForSadl((IUhuSphereForSadl) sphereForAndroid);
            IUhuComms uhuComms =  UhuStackHandler.getUhuComms();
            uhuComms.setSphereForSadl((IUhuSphereForSadl) sphereForAndroid);
        }
        return true;
    }

}
