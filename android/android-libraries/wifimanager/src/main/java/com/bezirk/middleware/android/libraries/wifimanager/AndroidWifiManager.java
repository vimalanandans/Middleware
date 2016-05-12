package com.bezirk.middleware.android.libraries.wifimanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class provides android implementation to the platform-independent interface {@link WifiManager}
 *
 * @author Rishabh Gulati
 */
public class AndroidWifiManager implements WifiManager {

    private static final String TAG = AndroidWifiManager.class.getCanonicalName();
    private Context context;
    private android.net.wifi.WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private WifiBroadCastReceiver wifiBroadCastReceiver;
    private NetworkScanCallback networkScanCallback;
    private List<Network> visibleNetworks; //wifi networks currently visible to the android device
    private DataManager dataManager;

    /**
     * Initializes resources required for fetching wifi configuration from Android.
     *
     * @param context android application context, <b>should be non-null</b>
     */
    public AndroidWifiManager(Context context) {
        if (context == null) {
            Log.e(TAG, "Context passed is null");
            return;
        }
        this.context = context;
        this.wifiManager = (android.net.wifi.WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        dataManager = new DataManager(this.context);
        registerReceiver(context);
        visibleNetworks = getFormattedNetworks(wifiManager.getScanResults());
        Log.i(TAG, "Initialization complete");
    }

    @Override
    public boolean isEnabled() {
        return wifiManager.isWifiEnabled();
    }

    @Override
    public boolean isConnected() {
        if (isEnabled()) {
            return networkInfo.isConnected();
        }
        return false;
    }

    @Override
    public boolean enable() {
        if (!isEnabled()) {
            wifiManager.setWifiEnabled(true);
            return true;
        }
        return false;
    }

    @Override
    public String getNetworkName() {
        if (isConnected()) {
            return wifiManager.getConnectionInfo().getSSID();
        }
        return null;
    }

    @Override
    public SecurityType getSecurityType() {
        final String ssid = getNetworkName();
        if (ssid != null) {
            List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifi : wifiConfigs) {
                if (wifi.SSID.equals(ssid)) {
                    return getSecurityType(wifi);
                }
            }
        }
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    /**
     * @see <a href="http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically">Connecting to wifi network</a>
     * @see <a href="http://developer.android.com/reference/android/net/wifi/WifiConfiguration.KeyMgmt.html">KeyMgmt</a>
     */
    @Override
    public void connect(String networkName, String password, SecurityType securityType, boolean saveNetwork, final ConnectCallback callback) {

        if (null == networkName || networkName.isEmpty()) {
            Log.d(TAG, "Network name for connecting null or empty");
            callback.onComplete(ConnectCallback.Status.INVALID_NETWORK_NAME, networkName);
            return;
        }
        final String formattedNetworkName = formatString(networkName);
        final String formattedPassword = (null != password) ? formatString(password) : null;

//        if (connect(formattedNetworkName)) {
//            callback.onComplete(ConnectCallback.Status.SUCCESS, formattedNetworkName);
//            return;
//        }

        // check if network is already configured for this device
        final int existingNetworkId = getNetworkId(formattedNetworkName);
        if (existingNetworkId != -1) {
            // network already configured. Remove the network. Deleting network required to ensure connection with passed password.
            if (wifiManager.removeNetwork(existingNetworkId)) {
                Log.v(TAG, "Network " + networkName + " removed successfully");
            } else {
                Log.e(TAG, "Problem deleting network");
                callback.onComplete(ConnectCallback.Status.NETWORK_DELETE_FAILURE, networkName);
                return;
            }
        } else {
            Log.v(TAG, "Not an existing configured network " + networkName);
        }

        // create network configuration
        final WifiConfiguration conf = createWifiConfiguration(formattedNetworkName, formattedPassword, securityType);

        // add network configuration
        final int networkId = wifiManager.addNetwork(conf);

        // connect to network if network configuration was added successfully
        if (networkId != -1) {
            connect(networkId, networkName, password, securityType, 2000, saveNetwork, callback);
        } else {
            callback.onComplete(ConnectCallback.Status.ADDING_NETWORK_FAILURE, networkName);
            return;
        }
    }

    @Override
    public void getNetworks(NetworkScanCallback networkScanCallback) {
        this.networkScanCallback = networkScanCallback;
        this.networkScanCallback.onComplete(mergeNetworks());
    }

    @Override
    public SavedNetwork getSavedNetwork(String networkName) {
        return dataManager.getSavedNetwork(networkName);
    }

    @Override
    public void destroy() {
        this.connectivityManager = null;
        this.wifiManager = null;
        this.networkInfo = null;
        unRegisterReceiver(this.context);
    }


    /**
     * Merge 2 collections of networks i.e. networks saved by bezirk {@link #getSavedNetworks()}, visible networks on the android device {@link #visibleNetworks}
     *
     * @return
     */
    private List<Network> mergeNetworks() {
        List<Network> networks = new ArrayList<Network>();

        return networks;
    }

    private List<SavedNetwork> getSavedNetworks() {
        return dataManager.getSavedNetworks();
    }

    /**
     * Get list of networks already configured for the android device<br>
     * <b>Currently not used. Can be used by {@link #mergeNetworks()} to provide better ordering of networks.<b/>
     *
     * @return
     */
    private List<Network> getConfiguredNetworks() {
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        List<Network> configuredNetworks = new ArrayList<Network>();
        for (WifiConfiguration wifiConfiguration : wifiConfigs) {
            Log.d(TAG, wifiConfiguration.SSID + wifiConfiguration.hiddenSSID);
            Network configuredNetwork = new Network(wifiConfiguration.SSID, getSecurityType(wifiConfiguration));
            configuredNetworks.add(configuredNetwork);
        }
        Log.d(TAG, "Configured Networks => " + configuredNetworks.toString());
        return configuredNetworks;
    }

    /**
     * Get security type of passed wifi configuration
     *
     * @param config non-null
     * @return {@link com.bezirk.middleware.android.libraries.wifimanager.WifiManager.SecurityType}
     * @see <a href="http://stackoverflow.com/questions/28022809/how-to-get-wifi-security-none-wep-wpa-wpa2-from-android-wificonfiguration-e">Get wifi security</a>
     */
    private SecurityType getSecurityType(WifiConfiguration config) {
        SecurityType securityType = null;
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            securityType = (config.allowedProtocols.get(WifiConfiguration.Protocol.RSN)) ? SecurityType.WPA2 : SecurityType.WPA;
        } else if (config.wepKeys[0] != null) {
            securityType = SecurityType.WEP;
        } else if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE)) {
            securityType = SecurityType.NONE;
        }

        Log.v(TAG, "Network -> " + config.SSID + " SecurityType -> " + securityType);
        return securityType;
    }

    /**
     * Attempt connecting to a network already configured in {@link android.net.wifi.WifiManager}
     *
     * @param networkName
     * @return true if able to connect to the network, false otherwise
     */
    private boolean connect(final String networkName) {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(networkName)) {
                if (wifiManager.disconnect() && wifiManager.enableNetwork(i.networkId, true)) {
                    Log.i(TAG, "Connecting to already configured wifi network: " + networkName);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Connect to a recently added network using its networkId. Delay depends on when this network was added. If calling this method immediately after adding the network, provide a delay between 1000-2000(milliseconds)
     *
     * @param networkId
     * @param networkName unformatted network name, i.e. without quotes. Used for callback only.
     * @param delay       delay in milliseconds
     * @param callback
     */
    private void connect(final int networkId, final String networkName, final String networkPassword, final SecurityType securityType, final long delay, final boolean saveNetwork, final ConnectCallback callback) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (wifiManager.disconnect() && wifiManager.enableNetwork(networkId, true)) {
                    Log.d(TAG, "Connecting to wifi network: " + networkName);
                    callback.onComplete(ConnectCallback.Status.SUCCESS, networkName);
                    if (saveNetwork) {
                        dataManager.saveNetwork(networkName, networkPassword, securityType);
                    }
                } else {
                    callback.onComplete(ConnectCallback.Status.FAILURE, networkName);
                }
            }
        }, delay);
    }

    /**
     * @param networkName  network name with quotes, <b>should be not null</b>
     * @param password     password with quotes if not null. Pass null for open networks.
     * @param securityType security type of the network, <b>should be not null</b>
     * @return wifi configuration with associated parameters based on network type
     * @see {@link #formatString(String)}
     */
    private WifiConfiguration createWifiConfiguration(String networkName, String password, SecurityType securityType) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = networkName;

        switch (securityType) {
            case WEP:
                conf.wepKeys[0] = password;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case WPA:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.preSharedKey = password;
                break;
            case WPA2:
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.preSharedKey = password;
                break;
            case NONE:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
        }
        return conf;
    }

    /**
     * Returns formatted string, i.e. with quotes. For instance, if passed string is home. The formatted output is "home"
     *
     * @param s <b>should be non-null</b>
     * @return formatted string with quotes
     */
    private String formatString(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Register the broadcast receiver to listen for wifi scan results
     *
     * @param context {@link Context} passed in {@link AndroidWifiManager#AndroidWifiManager(Context)}
     */
    private void registerReceiver(Context context) {
        this.wifiBroadCastReceiver = new WifiBroadCastReceiver();
        context.registerReceiver(this.wifiBroadCastReceiver, new IntentFilter(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    /**
     * Unregister the broadcast receiver to listen for wifi scan results
     *
     * @param context {@link Context} passed in {@link AndroidWifiManager#AndroidWifiManager(Context)}
     */
    private void unRegisterReceiver(Context context) {
        context.unregisterReceiver(this.wifiBroadCastReceiver);
        this.wifiBroadCastReceiver = null;
    }

    private class WifiBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            visibleNetworks = getFormattedNetworks(wifiManager.getScanResults());
            Log.v(TAG, "got new wifi networks: " + visibleNetworks.size());
            networkScanCallback.onComplete(mergeNetworks());
        }
    }

    private List<Network> getFormattedNetworks(List<ScanResult> scanResults) {
        List<Network> formattedNetworks = new ArrayList<Network>();
        Network network;
        for (ScanResult result : scanResults) {
            network = new Network(result.SSID, getSecurityType(result.capabilities));
            formattedNetworks.add(network);
        }
        Log.d(TAG, "Formatted Networks => " + formattedNetworks.toString());
        return formattedNetworks;
    }

    /**
     * Find the security type based on {@link ScanResult#capabilities}
     *
     * @param capabilities
     * @return
     */
    private SecurityType getSecurityType(String capabilities) {
        if (capabilities.contains("WPA2")) {
            return SecurityType.WPA2;
        } else if (capabilities.contains("WPA")) {
            return SecurityType.WPA;
        } else if (capabilities.contains("WEP")) {
            return SecurityType.WEP;
        }
        //TODO: Enterprise security check
        Log.d(TAG, "wifi capabilities ---->" + capabilities);
        return SecurityType.NONE;
    }

    /**
     * Get networkId of a network`
     *
     * @param networkName name of the network whose networkId is required <b>should be formatted with quotes</b>
     * @return networkId if the network is already configured in the android device, -1 otherwise
     * @see {@link #formatString(String)}
     */
    private int getNetworkId(String networkName) {
        List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
        if (null != wifiConfigurations) {
            for (WifiConfiguration configuration : wifiConfigurations) {
                if (configuration.SSID.equals(networkName)) {
                    Log.v(TAG, "networkId of network " + networkName + " is " + configuration.networkId);
                    return configuration.networkId;
                }
            }
        }
        return -1;
    }

    //used only for testing, can be removed if using java reflection in testing
    public DataManager getDataManager(){
        return this.dataManager;
    }
}
