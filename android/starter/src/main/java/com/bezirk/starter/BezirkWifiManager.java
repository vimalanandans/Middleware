package com.bezirk.starter;

/**
 * singleton class, only one state
 * Created by PIK6KOR on 12/14/2015.
 */
public final class BezirkWifiManager {

    private static BezirkWifiManager context;
    private String connectedWifiSSID;

    private BezirkWifiManager() {
        //private constructor
    }

    /**
     * get instance
     *
     * @return
     */
    public static BezirkWifiManager getInstance() {
        synchronized (BezirkWifiManager.class) {
            if (context == null) {
                context = new BezirkWifiManager();
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