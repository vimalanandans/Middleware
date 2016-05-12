package com.bezirk.middleware.android.libraries.wifimanager;

import java.util.Date;

import com.bezirk.middleware.android.libraries.wifimanager.WifiManager.SecurityType;

/**
 * @author Rishabh Gulati
 */

public class SavedNetwork extends Network {
    private boolean visible;
    private String password;

    public SavedNetwork(String name, WifiManager.SecurityType securityType, String password, boolean visible) {
        super(name, securityType);
        this.password = password;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SavedNetwork{" +
                "visible=" + visible +
                ", password='" + password + '\'' +
                "} " + super.toString();
    }
}
