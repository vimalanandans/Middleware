package com.bezirk;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.proxy.messagehandler.ZirkMessageHandler;
import com.bezirk.sphere.api.PubSubSphereAccess;
import com.bezirk.sphere.api.SphereAPI;
import com.bezirk.sphere.api.SphereRegistration;
//import com.bezirk.streaming.rtc.Signaling;

/**
 * This is a Util Class for injecting the instance of
 * SQLConnectionForJava or SQLConnectionForAndroid in Bezirk-PC or
 * Bezirk-Android respectively. The injected instance will be later used in
 * the Java-Commons
 */

/**
 * FIXME: This exposes again the global access to any component.
 * Don't use this in future, avoid this by creating object to the respective components
 * and injecting the object the respective place
 * -Vimal
 * */
@Deprecated
public final class BezirkCompManager {

    /**
     * This module is no longer needed. because no one calls addmessage
     * and only the checking the record present in the message validator
     * FIXME: remove this module
     * - Vimal
     * */
    //private static MessageBookKeeper msgKeeper;


    /***
     * This is the instance variable, which is a reference to UPADeviceForAndPC
     * or UPADeviceForAndroid
     *
     */
    private static DeviceInterface upaDevice;


    private static ZirkMessageHandler platformSpecificCallback;

    private static PubSubSphereAccess pubSubSphereAccess;

    private static SphereAPI sphereAPI;

    private static SphereRegistration sphereRegistration;


    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private BezirkCompManager() {

    }

  /*
    /**
     * For Real Time Communication
     */

   /* private static Signaling signaling;
   public static Signaling getSignaling() {
        return signaling;
    }

    public static void setSignaling(Signaling signaling) {
        BezirkCompManager.signaling = signaling;
    }
*/
    /**
     * @return the upaDevice
     */
    public static DeviceInterface getUpaDevice() {
        return upaDevice;
    }

    /**
     * @param upaDevice
     *            the upaDevice to set
     */
    public static void setUpaDevice(DeviceInterface upaDevice) {
        BezirkCompManager.upaDevice = upaDevice;
    }

    public static void setplatformSpecificCallback(ZirkMessageHandler bezirkCallback) {
        BezirkCompManager.platformSpecificCallback = bezirkCallback;
    }

    public static ZirkMessageHandler getplatformSpecificCallback() {
        return BezirkCompManager.platformSpecificCallback;
    }

    // this is temporary for sadl to use the spheres
    public static PubSubSphereAccess getSphereForPubSubBroker() {
        return pubSubSphereAccess;
    }

    // this is temporary for setting up spheres for sadl
    public static void setSphereForPubSub(PubSubSphereAccess sphereForPubSub) {
        BezirkCompManager.pubSubSphereAccess = sphereForPubSub;
    }

    // this is temporary for UI to use the spheres
    public static SphereAPI getSphereUI() {
        return sphereAPI;
    }

    // this is temporary for setting up spheres for UI
    public static void setSphereUI(SphereAPI sphereAPI) {
        BezirkCompManager.sphereAPI = sphereAPI;
    }

    // this is temporary for proxyForServices to use registration
    public static SphereRegistration getSphereRegistration() {
        return sphereRegistration;
    }

    // this is temporary for setting up zirk registration with spheres for UI
    public static void setSphereRegistration(
            SphereRegistration sphereRegistration) {
        BezirkCompManager.sphereRegistration = sphereRegistration;
    }
}
