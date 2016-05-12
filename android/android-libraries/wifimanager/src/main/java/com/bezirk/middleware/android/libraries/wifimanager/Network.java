package com.bezirk.middleware.android.libraries.wifimanager;

import com.bezirk.middleware.android.libraries.wifimanager.WifiManager.SecurityType;

/**
 * @author Rishabh Gulati
 */

public class Network {
    private String name; //networkName/ssid of the network, without quotes
    private SecurityType securityType;

    public Network(String name, SecurityType securityType) {
        this.name = name;
        this.securityType = securityType;
    }

    public String getName() {
        return name;
    }

    public SecurityType getSecurityType() {
        return securityType;
    }

    @Override
    public String toString() {
        return "Network{" +
                "name='" + name + '\'' +
                ", securityType=" + securityType +
                '}';
    }
}
