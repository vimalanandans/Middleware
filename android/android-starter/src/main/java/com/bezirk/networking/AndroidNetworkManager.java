package com.bezirk.networking;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.bezirk.componentManager.LifecycleManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Observable;
import java.util.Observer;

public class AndroidNetworkManager extends NetworkManager implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(AndroidNetworkManager.class);
    private static final String DEFAULT_ANDROID_INTERFACE = "wlan0";
    private final SharedPreferences preferences;

    private final Context context; //for registering the receiver
    private final NetworkBroadCastReceiver networkBroadCastReceiver;
    private final WifiManager wifiManager;
    private String connectedWifiSSID;

    public AndroidNetworkManager(SharedPreferences preferences, Context context) {

        this.preferences = preferences;
        this.context = context;
        this.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        networkBroadCastReceiver = new NetworkBroadCastReceiver(wifiManager);
        init();
    }

    private void init() {
        if (wifiManager.isWifiEnabled()) {
            logger.trace("Wifi is enabled");
            ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (null == wifiInfo) {
                logger.warn("Not connected to a wifi network");
            } else {
                String currentSSID = wifiInfo.getSSID();
                logger.trace("connected to " + currentSSID);
                setConnectedWifiSSID(currentSSID); //might need this for NetworkBroadcastReceiver
            }
        } else {
            logger.warn("Wifi is not enabled");
        }
    }

    @Override
    public String getStoredInterfaceName() {
        if(null!=preferences){
            logger.debug("preferences is not null in Android network manager");
            return preferences.getString(NETWORK_INTERFACE_NAME_KEY, DEFAULT_ANDROID_INTERFACE);
        }else{
            logger.debug("preferrences is null");
            return null;
        }

    }

    @Override
    public void setStoredInterfaceName(String interfaceName) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NETWORK_INTERFACE_NAME_KEY, interfaceName);
        editor.commit();
    }

    @Override
    public InetAddress getInetAddress() {
        logger.debug("getInetAddress of AndroidNetworkManager");
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName(getStoredInterfaceName());
            if (null != networkInterface) {
                logger.debug("Network interface found for " + getStoredInterfaceName());
                return getIpForInterface(networkInterface);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Observable observable, Object data) {
        LifecycleManager lifecycleManager = (LifecycleManager) observable;
        switch (lifecycleManager.getState()) {
            case CREATED:
                //registerWifiBroadcastReceiver();
                break;
            case STARTED:
                registerWifiBroadcastReceiver();
                break;
            case STOPPED:
                unRegisterWifiBroadcastReceiver();
                break;
        }
    }

    private void registerWifiBroadcastReceiver() {
        logger.debug("Registering WifiBroadcastReceiver");
        //register to the broadcast receiver even if wifi sate is off, so that after wifi on, it will be detected.
        IntentFilter connectedFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectedFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        connectedFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        connectedFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        connectedFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        // register the zirk only once.
        context.registerReceiver(networkBroadCastReceiver, connectedFilter);
    }

    private void unRegisterWifiBroadcastReceiver() {
        logger.debug("Un-registering WifiBroadcastReceiver");
        context.unregisterReceiver(networkBroadCastReceiver);
    }

    public String getConnectedWifiSSID() {
        return connectedWifiSSID;
    }

    private void setConnectedWifiSSID(String connectedWifiSSID) {

        this.connectedWifiSSID = connectedWifiSSID;
    }

}
