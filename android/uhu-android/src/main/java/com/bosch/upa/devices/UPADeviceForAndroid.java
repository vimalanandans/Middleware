package com.bosch.upa.devices;

import android.content.SharedPreferences;

/**
* @author Jan Zibuschka (jan.zibuschka@de.bosch.com)
*
* Class containing device abstraction functions
*
* TODO: Check relationship to com.bosch.upa.services.deviceWrapper / possibly merge
*
* TODO: Check package, communications probably is not right,
* 	TestDevices is not possible, as concrete TestDevices need communications
*/
public final class UPADeviceForAndroid {

    private static SharedPreferences preferences;

    private static Boolean resetWifiOnReboot=false;
    private static Boolean isWatchDawgOn=true;

    private UPADeviceForAndroid(){
        //private constructor to hide the implicit one as this is a utility class.
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(SharedPreferences preferences) {
        UPADeviceForAndroid.preferences = preferences;
    }

    public static Boolean getResetWifiOnReboot() {
        return resetWifiOnReboot;
    }

    public static void setResetWifiOnReboot(Boolean resetWifiOnReboot) {
        UPADeviceForAndroid.resetWifiOnReboot = resetWifiOnReboot;
    }

    public static Boolean getIsWatchDawgOn() {
        return isWatchDawgOn;
    }

    public static void setIsWatchDawgOn(Boolean isWatchDawgOn) {
        UPADeviceForAndroid.isWatchDawgOn = isWatchDawgOn;
    }


}
