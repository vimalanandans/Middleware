package com.bezirk.networking;

import android.content.SharedPreferences;

public class AndroidNetworkManager extends NetworkManager {
    private static final String DEFAULT_ANDROID_INTERFACE = "wlan0";
    private final SharedPreferences preferences;

    public AndroidNetworkManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getStoredInterfaceName() {
        return preferences.getString(NETWORK_INTERFACE_NAME_KEY, DEFAULT_ANDROID_INTERFACE);
    }

    @Override
    public void setStoredInterfaceName(String interfaceName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NETWORK_INTERFACE_NAME_KEY, interfaceName);
        editor.commit();
    }
}
