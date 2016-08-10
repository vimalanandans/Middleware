//package com.bezirk.starter.helper;
//
//import com.bezirk.comms.Comms;
//import com.bezirk.datastorage.SpherePersistence;
//import com.bezirk.datastorage.SphereRegistry;
//
//import com.bezirk.device.Device;
//import com.bezirk.networking.NetworkManager;
//import com.bezirk.sphere.AndroidSphereServiceManager;
//import com.bezirk.sphere.AndroidSpherePreference;
//import com.bezirk.sphere.api.DevMode;
//import com.bezirk.sphere.api.SphereAPI;
//import com.bezirk.sphere.api.SphereConfig;
//import com.bezirk.sphere.api.SphereSecurity;
//import com.bezirk.sphere.api.SphereServiceAccess;
//import com.bezirk.sphere.security.CryptoEngine;
//import com.bezirk.starter.MainService;
//import com.bezirk.starter.MainStackPreferences;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * Handles the sphere initialization for MainService
// */
//public final class SphereHandler {
//    private static final Logger logger = LoggerFactory.getLogger(SphereHandler.class);
//
//    static SphereAPI sphereForAndroid;
//    static DevMode devMode;
//
//    /**
//     * deinitialize the sphere
//     */
//    boolean deinitSphere() {
//
//        // clear the reference
//        sphereForAndroid = null;
//
//
//        return true;
//    }
//
//    /**
//     * return the sphere security
//     */
//    public SphereSecurity getSphereSecurity() {
//        return (SphereSecurity) sphereForAndroid;
//    }
//
//    /**
//     * return the sphere security
//     */
//    public SphereServiceAccess getSphereServiceAccess() {
//        return (SphereServiceAccess) sphereForAndroid;
//    }
//
//    /**
//     * create and initialise the sphere
//     */
//    boolean initSphere(Device bezirkDevice, MainService service, SpherePersistence spherePersistence, MainStackPreferences preferences, NetworkManager networkManager) {
//
//        /** start the sphere related init*/
//        if (sphereForAndroid == null) {
//            // init the actual
//            SphereRegistry sphereRegistry = null;
//            try {
//                sphereRegistry = spherePersistence.loadSphereRegistry();
//            } catch (Exception e) {
//                logger.error("Error in loading sphere Persistence", e);
//            }
//            CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
//
//            sphereForAndroid = new AndroidSphereServiceManager(cryptoEngine, bezirkDevice, sphereRegistry, service.getApplicationContext(), preferences, networkManager);
//
//            AndroidSphereServiceManager bezirkSphereForAndroid = (AndroidSphereServiceManager) SphereHandler.sphereForAndroid;
//
//            bezirkSphereForAndroid.setSphereListener(bezirkSphereForAndroid);
//
//            SphereConfig sphereConfig = new AndroidSpherePreference(preferences);
//            sphereConfig.init();
//
//            if (!(bezirkSphereForAndroid.initSphere(spherePersistence, MainStackHandler.getBezirkComms()))) {
//                // at the moment the init sphere fails due to persistence
//                return false;
//            }
//
//            devMode = (DevMode) sphereForAndroid;
//
//
//            Comms uhuComms = MainStackHandler.getBezirkComms();
//            uhuComms.setSphereSecurity((SphereSecurity) sphereForAndroid);
//        }
//        return true;
//    }
//
//}

