package com.bezirk.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class JavaNetworkManager extends NetworkManager {
    private static final Logger logger = LoggerFactory.getLogger(JavaNetworkManager.class);
    private Preferences preferences;

    public JavaNetworkManager() {
        init();
    }

    void init() {
        try {
            preferences = Preferences.userNodeForPackage(JavaNetworkManager.class);
            logger.debug("Network preferences initialized successfully");
            logger.debug(NETWORK_INTERFACE_NAME_KEY + " --> " + getStoredInterfaceName());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }

    @Override
    public String getStoredInterfaceName() {

        return preferences.get(NETWORK_INTERFACE_NAME_KEY, NETWORK_INTERFACE_DEFAULT_VALUE);

    }

    @Override
    public void setStoredInterfaceName(String interfaceName) {

        try {
            preferences.put(NETWORK_INTERFACE_NAME_KEY, interfaceName);
            preferences.sync();
            logger.debug("Network preferences is stored");
        } catch (BackingStoreException e) {
            logger.error(e.getMessage());
        }
    }

}
