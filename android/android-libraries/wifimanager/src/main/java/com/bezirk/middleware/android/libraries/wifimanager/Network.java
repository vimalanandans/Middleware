package com.bezirk.middleware.android.libraries.wifimanager;

/**
 * Created by Rishabh Gulati on 5/5/16.
 * Robert Bosch LLC
 * rishabh.gulati@us.bosch.com
 * //TODO Move interface to core
 */
public class Network {
    private String name;
    private int authType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }
}
