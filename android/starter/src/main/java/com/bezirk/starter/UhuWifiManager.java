package com.bezirk.starter;

/**
 *
 * singleton class, only one state
 * Created by PIK6KOR on 12/14/2015.
 */
public final class UhuWifiManager {

    private String connectedWifiSSID;
    private static UhuWifiManager context;

    private UhuWifiManager(){
        //private constructor
    }

    /**
     * get instance
     * @return
     */
    public static UhuWifiManager getInstance() {
        synchronized (UhuWifiManager.class) {
            if (context == null) {
                context = new UhuWifiManager();
            }
            return context;
        }
    }

    public String getConnectedWifiSSID() {
        return connectedWifiSSID;
    }

    public void setConnectedWifiSSID(String connectedWifiSSID) {
        this.connectedWifiSSID = connectedWifiSSID;
    }

}