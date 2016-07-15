package com.bezirk;

import com.bezirk.devices.DeviceInterface;
import com.bezirk.proxy.messagehandler.MessageHandler;
import com.bezirk.sphere.api.SphereSecurity;
import com.bezirk.sphere.api.SphereServiceAccess;
import com.bezirk.sphere.api.SphereAPI;
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


    private static MessageHandler platformSpecificCallback;

    private static SphereServiceAccess sphereServiceAccess;

    private static SphereAPI sphereAPI;

    private static SphereSecurity sphereSecurity;


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
//
//    public static void setplatformSpecificCallback(MessageHandler bezirkCallback) {
//        BezirkCompManager.platformSpecificCallback = bezirkCallback;
//    }
//
//    public static MessageHandler getplatformSpecificCallback() {
//        return BezirkCompManager.platformSpecificCallback;
//    }

    // this is temporary for UI to use the spheres
    public static SphereAPI getSphereUI() {
        return sphereAPI;
    }

    // this is temporary for setting up spheres for UI
    public static void setSphereUI(SphereAPI sphereAPI) {
        BezirkCompManager.sphereAPI = sphereAPI;
    }

}
