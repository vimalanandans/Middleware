package com.bezirk.starter;

public final class BezirkWifiManager {
    private static BezirkWifiManager context;
    private String connectedWifiSSID;

    private BezirkWifiManager() {
        //private constructor
    }

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