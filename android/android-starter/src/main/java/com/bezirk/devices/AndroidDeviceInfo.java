package com.bezirk.devices;

import android.content.SharedPreferences;

public final class AndroidDeviceInfo {
    private static SharedPreferences preferences;

    private AndroidDeviceInfo() {
        //private constructor to hide the implicit one as this is a utility class.
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static void setPreferences(SharedPreferences preferences) {
        AndroidDeviceInfo.preferences = preferences;
    }
}
