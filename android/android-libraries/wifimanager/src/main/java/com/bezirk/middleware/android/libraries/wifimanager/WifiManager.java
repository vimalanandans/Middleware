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
     * @param saveNetwork  if true the network will be saved with bezirk
     * @param callback     for indicating success or failure trying to connect to the network
     * @see com.bezirk.middleware.android.libraries.wifimanager.WifiManager.ConnectCallback
     */
    public void connect(String networkName, String password, SecurityType securityType, boolean saveNetwork, ConnectCallback callback);

    /**
     * Get wifi networks on the device
     *
     * @param callback <b>must be non null</b>
     * @see com.bezirk.middleware.android.libraries.wifimanager.WifiManager.NetworkScanCallback
     */
    public void getNetworks(NetworkScanCallback callback);

    /**
     * Get saved network configuration stored by the {@link WifiManager}
     *
     * @param networkName network name with quotes, i.e. if network name is home, provide network name as "home"
     * @return {@link SavedNetwork} if configuration available with bezirk
     */
    public SavedNetwork getSavedNetwork(String networkName);

    /**
     * Destroy the wifi manager
     * //TODO elaborate
     */
    public void destroy();


    /**
     * Callback used to indicate success/failure of connection attempt.
     *
     * @see #connect(String, String, SecurityType, boolean, ConnectCallback)
     */
    interface ConnectCallback {
        enum Status {
            SUCCESS, INVALID_NETWORK_NAME, ADDING_NETWORK_FAILURE, NETWORK_DELETE_FAILURE, FAILURE
        }

        /**
         * @param status
         * @param networkName networkName/ssid passed in {@link #connect(String, String, SecurityType, boolean, ConnectCallback)}
         */
        void onComplete(Status status, String networkName);
    }

    /**
     * Supported wifi network security types
     *
     * @see #getSecurityType()
     * @see #connect(String, String, SecurityType, boolean, ConnectCallback)
     */
    enum SecurityType {
        WEP, WPA, WPA2, NONE
    }

    /**
     * Callback used to provide list of networks previously saved by the user (i.e. password of network available with bezirk) and/or currently visible networks
     */
    interface NetworkScanCallback {
        /**
         * @param networks Iterable of type {@link Network} and/or subtype {@link SavedNetwork}
         */
        void onComplete(Iterable<Network> networks);
    }
}
