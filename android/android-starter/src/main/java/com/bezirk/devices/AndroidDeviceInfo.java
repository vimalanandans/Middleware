package com.bezirk.devices;

import android.content.SharedPreferences;

/**
 * Class containing device abstraction functions
 * <p>
 * TODO: Check relationship to com.bosch.upa.services.deviceWrapper / possibly merge
 * </p><p>
 * TODO: Check package, communications probably is not right,
 * TestDevices is not possible, as concrete TestDevices need communications
 * </p>
 */
public final class AndroidDeviceInfo {

    private static SharedPreferences preferences;

    private static Boolean resetWifiOnReboot = false;
    private static Boolean isWatchDawgOn = true;

    private AndroidDeviceInfo() {
        //private constructor to hide the implicit one as this is a utility class.
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(SharedPreferences preferences) {
        AndroidDeviceInfo.preferences = preferences;
    }

    public static Boolean getResetWifiOnReboot() {
        return resetWifiOnReboot;
    }

    public static void setResetWifiOnReboot(Boolean resetWifiOnReboot) {
        AndroidDeviceInfo.resetWifiOnReboot = resetWifiOnReboot;
    }

    public static Boolean getIsWatchDawgOn() {
        return isWatchDawgOn;
    }

    public static void setIsWatchDawgOn(Boolean isWatchDawgOn) {
        AndroidDeviceInfo.isWatchDawgOn = isWatchDawgOn;
    }


}
