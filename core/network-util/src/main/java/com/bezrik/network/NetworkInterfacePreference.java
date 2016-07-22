package com.bezrik.network;

/**
 * To store the user selected network preferences
 */
public interface NetworkInterfacePreference {
    static final String NETWORK_INTERFACE_NAME_KEY = "NETWORK_INTERFACE_PREFERENCE";
    static final String NETWORK_INTERFACE_DEFAULT_VALUE = ""; // long live linux

    /**
     * Initialize the relevant properties of the class like sphereId, sphereName
     * and sphereKey
     */
    //public void init();

    /**
     * Get the stored interface name
     *
     * */
    public String getStoredInterfaceName();

    /**
     * Set the stored interface name
     *
     * */
    public void setStoredInterfaceName(String interfaceName);

}
