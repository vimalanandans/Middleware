package com.bezirk.middleware.android.libraries.wifimanager;

/**
 * Platform independent interface for getting, setting & managing wifi related configurations required for on-boarding devices onto a particular network
 * //TODO Move interface to core
 */
public interface WifiManager {

    /**
     * Checks if wifi is enabled in the device
     *
     * @return true if wifi is enabled
     */
    public boolean isEnabled();

    /**
     * Check if the device is connected to wifi
     *
     * @return true if the device is connected to the wifi
     */
    public boolean isConnected();

    /**
     * Enable wifi on the device
     *
     * @return true if the device was enabled on the device
     */
    public boolean enable();

    /**
     * Get the network name of the currently connected wifi network
     *
     * @return network name (ssid) if the device is connected to a network, null if not connected to a network
     */
    public String getNetworkName();

    /**
     * Get the {@link SecurityType} of the currently connected wifi network
     *
     * @return {@link SecurityType} if the device is connected to a network, null otherwise
     * <p/>
     * //TODO formulate a common list of constants across platforms
     */
    public SecurityType getSecurityType();

    /**
     * Get the password of the currently connected wifi network
     *
     * @return password if the device is connected to a network, null if the password is not accessible or when not connected to a network
     */
    public String getPassword();

    /**
     * Connect to the wifi network with using networkName, password and security type for the network.
     *
     * @param networkName  ssid of the network to be connected
     * @param password     password of the network to be connected
     * @param securityType security type of the network to be connected
     * @param callback     for indicating success or failure trying to connect to the network
     * @see {@link com.bezirk.middleware.android.libraries.wifimanager.WifiManager.ConnectCallback}
     */
    public void connect(String networkName, String password, SecurityType securityType, ConnectCallback callback);

    public Iterable<Network> getNetworks();

    /**
     * Destroy the wifi manager
     * //TODO elaborate
     */
    public void destroy();


    interface ConnectCallback {
        enum Status {
            SUCCESS, INVALID_NETWORK_NAME, ADDING_NETWORK_FAILURE, FAILURE
        }

        void onComplete(Status status, String networkName);
    }

    enum SecurityType {
        WEP, WPA, WPA2, NONE
    }
}
