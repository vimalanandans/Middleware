package com.bezirk.middleware.android.libraries.wifimanager;

import java.util.Date;

/**
 * Created by Rishabh Gulati on 5/5/16.
 * Robert Bosch LLC
 * rishabh.gulati@us.bosch.com
 * //TODO Move interface to core
 */
public class ConfiguredNetwork extends Network {
    private boolean visible;
    private String password;
    private Date dateAdded;
    private Date dateVerified;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Date getDateVerified() {
        return dateVerified;
    }

    public void setDateVerified(Date dateVerified) {
        this.dateVerified = dateVerified;
    }
}
