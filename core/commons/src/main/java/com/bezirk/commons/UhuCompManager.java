package com.bezirk.commons;

import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.sphere.api.IUhuSphereAPI;
import com.bezirk.sphere.api.IUhuSphereForSadl;
import com.bezirk.sphere.api.IUhuSphereRegistration;
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
public final class UhuCompManager {

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

    private static IUhuSphereForSadl uhuSphereForSadl;

    private static IUhuSphereAPI uhuSphereAPI;

    private static IUhuSphereRegistration uhuSphereRegistration;

    //private static IUhuSphereMessages uhuSphereMessages;

    /**
     * For Real Time Communication
     */

    private static Signaling signaling;


    /* Utility Class. All methods are static. Adding private constructor to suppress PMD warnings.*/
    private UhuCompManager() {

    }

    public static Signaling getSignaling() {
        return signaling;
    }

    public static void setSignaling(Signaling signaling) {
        UhuCompManager.signaling = signaling;
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
        UhuCompManager.upaDevice = upaDevice;
    }


    /**
     * This is the messageKeeper which bookKeeps requests for responses
     * @param mK
     */
    /*public static void setMsgBookKeeper(MessageBookKeeper mK){
		UhuCompManager.msgKeeper = mK;
	}
	
	public static MessageBookKeeper getMsgBookKeeper(){
		return UhuCompManager.msgKeeper;
	}*/

    /**
     * setplatformSpecificCallback
     * @param uhucallback
     */
    public static void setplatformSpecificCallback(ZirkMessageHandler uhucallback) {
        UhuCompManager.platformSpecificCallback = uhucallback;
    }

    public static ZirkMessageHandler getplatformSpecificCallback() {
        return UhuCompManager.platformSpecificCallback;
    }

    // this is temporary for sadl to use the spheres
    public static IUhuSphereForSadl getSphereForSadl() {
        return uhuSphereForSadl;
    }

    // this is temporary for setting up spheres for sadl
    public static void setSphereForSadl(IUhuSphereForSadl sphereForSadl) {
        UhuCompManager.uhuSphereForSadl = sphereForSadl;
    }

    // this is temporary for UI to use the spheres
    public static IUhuSphereAPI getSphereUI() {
        return uhuSphereAPI;
    }

    // this is temporary for setting up spheres for UI
    public static void setSphereUI(IUhuSphereAPI uhuSphereAPI) {
        UhuCompManager.uhuSphereAPI = uhuSphereAPI;
    }

    // this is temporary for proxyForServices to use registration
    public static IUhuSphereRegistration getSphereRegistration() {
        return uhuSphereRegistration;
    }

    // this is temporary for setting up zirk registration with spheres for UI
    public static void setSphereRegistration(
            IUhuSphereRegistration sphereRegistration) {
        UhuCompManager.uhuSphereRegistration = sphereRegistration;
    }

    /**
     * @return the uhuSphereMessages
     */
	/*public static IUhuSphereMessages getUhuSphereMessages() {
		return uhuSphereMessages;
	}*/

    /**
     * @param uhuSphereMessages the uhuSphereMessages to set
     */
	/*public static void setUhuSphereMessages(
			IUhuSphereMessages uhuSphereMessages) {
		UhuCompManager.uhuSphereMessages = uhuSphereMessages;
	}*/


}
