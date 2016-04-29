package com.bezirk.commons;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.sphere.api.BezirkSphereForSadl;
import com.bezirk.sphere.api.BezirkSphereAPI;
import com.bezirk.sphere.api.BezirkSphereRegistration;
import com.bezirk.streaming.rtc.Signaling;

/**
 * @author hkh5kor
 * @Date: 11-09-2014
 * <p/>
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
    private static UPADeviceInterface upaDevice;


    private static ZirkMessageHandler platformSpecificCallback;

    private static BezirkSphereForSadl bezirkSphereForSadl;

    private static BezirkSphereAPI bezirkSphereAPI;

    private static BezirkSphereRegistration bezirkSphereRegistration;

    //private static BezirkSphereMessages uhuSphereMessages;

    /**
     * For Real Time Communication
     */

    private static Signaling signaling;


    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private BezirkCompManager() {

    }

    public static Signaling getSignaling() {
        return signaling;
    }

    public static void setSignaling(Signaling signaling) {
        BezirkCompManager.signaling = signaling;
    }

    /**
     * @return the upaDevice
     */
    public static UPADeviceInterface getUpaDevice() {
        return upaDevice;
    }

    /**
     * @param upaDevice
     *            the upaDevice to set
     */
    public static void setUpaDevice(UPADeviceInterface upaDevice) {
        BezirkCompManager.upaDevice = upaDevice;
    }


    /**
     * This is the messageKeeper which bookKeeps requests for responses
     * @param mK
     */
    /*public static void setMsgBookKeeper(MessageBookKeeper mK){
		BezirkCompManager.msgKeeper = mK;
	}
	
	public static MessageBookKeeper getMsgBookKeeper(){
		return BezirkCompManager.msgKeeper;
	}*/

    /**
     * setplatformSpecificCallback
     * @param bezirkCallback
     */
    public static void setplatformSpecificCallback(ZirkMessageHandler bezirkCallback) {
        BezirkCompManager.platformSpecificCallback = bezirkCallback;
    }

    public static ZirkMessageHandler getplatformSpecificCallback() {
        return BezirkCompManager.platformSpecificCallback;
    }

    // this is temporary for sadl to use the spheres
    public static BezirkSphereForSadl getSphereForSadl() {
        return bezirkSphereForSadl;
    }

    // this is temporary for setting up spheres for sadl
    public static void setSphereForSadl(BezirkSphereForSadl sphereForSadl) {
        BezirkCompManager.bezirkSphereForSadl = sphereForSadl;
    }

    // this is temporary for UI to use the spheres
    public static BezirkSphereAPI getSphereUI() {
        return bezirkSphereAPI;
    }

    // this is temporary for setting up spheres for UI
    public static void setSphereUI(BezirkSphereAPI bezirkSphereAPI) {
        BezirkCompManager.bezirkSphereAPI = bezirkSphereAPI;
    }

    // this is temporary for proxyForServices to use registration
    public static BezirkSphereRegistration getSphereRegistration() {
        return bezirkSphereRegistration;
    }

    // this is temporary for setting up zirk registration with spheres for UI
    public static void setSphereRegistration(
            BezirkSphereRegistration sphereRegistration) {
        BezirkCompManager.bezirkSphereRegistration = sphereRegistration;
    }
}
