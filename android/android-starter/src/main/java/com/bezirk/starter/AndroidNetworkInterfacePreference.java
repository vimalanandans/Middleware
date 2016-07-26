package com.bezirk.starter;

import com.bezrik.network.NetworkInterfacePreference;


public class AndroidNetworkInterfacePreference implements NetworkInterfacePreference {

    public static final String defaultAndroidInterface = "wlan0";
    final MainStackPreferences preferences;

    public AndroidNetworkInterfacePreference(MainStackPreferences preferences) {
        this.preferences = preferences;

    }


    @Override
    public String getStoredInterfaceName() {
        return preferences.getString(NETWORK_INTERFACE_NAME_KEY, defaultAndroidInterface);
    }

    @Override
    public void setStoredInterfaceName(String interfaceName) {
        preferences.putString(NETWORK_INTERFACE_NAME_KEY, interfaceName);
    }
}
