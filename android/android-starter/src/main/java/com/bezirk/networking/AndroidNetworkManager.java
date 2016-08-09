package com.bezirk.networking;

import android.content.SharedPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

public class AndroidNetworkManager extends NetworkManager {
    private static final Logger logger = LoggerFactory.getLogger(AndroidNetworkManager.class);
    private static final String DEFAULT_ANDROID_INTERFACE = "wlan0";
    private final SharedPreferences preferences;
    public AndroidNetworkManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public String getStoredInterfaceName() {
        if(null!=preferences){
            logger.debug("preferences is not null");
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


}
