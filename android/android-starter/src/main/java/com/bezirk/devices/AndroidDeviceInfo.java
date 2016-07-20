package com.bezirk.devices;

import android.content.SharedPreferences;

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
