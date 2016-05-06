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
    private List<ScanResult> availableNetworks;

    /**
     * Initializes resources required for fetching wifi configuration from Android.
     *
     * @param context application context should be non-null
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
        registerReceiver(context);
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
        Log.i(TAG, "Network -> " + config.SSID + " SecurityType -> " + securityType);
        return securityType;
    }

    @Override
    public String getPassword() {
        return null;
    }

    //TODO: Add case for passing -1 as authtype

    /**
     * @see <a href="http://stackoverflow.com/questions/8818290/how-to-connect-to-a-specific-wifi-network-in-android-programmatically">Connecting to wifi network</a>
     * @see <a href="http://developer.android.com/reference/android/net/wifi/WifiConfiguration.KeyMgmt.html">KeyMgmt</a>
     */

    public void connect(String SSID, String password, int authType, final ConnectCallback callback) {
        final String formattedSSID;
        String formattedPassword = null;
        final int networkId;

        if (SSID == null || SSID.isEmpty() || !(authType == 0 || authType == 1)) {
            callback.onComplete(ConnectCallback.Status.INVALID_NETWORK_NAME, SSID);
            return;
        }
        formattedSSID = "\"" + SSID + "\"";
        if (password != null && !password.isEmpty()) {
            formattedPassword = "\"" + password + "\"";
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = formattedSSID;   // Please note the quotes. String should contain ssid in quotes

        // For WEP & open networks
        if (authType == 0) {
            if (formattedPassword == null) { //open network
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            } else { //wep network
                conf.wepKeys[0] = formattedPassword;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            }
        }
        //For WPA network
        else if (authType == 1) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            conf.preSharedKey = formattedPassword;
        }

        //add configuration to the wifi manager
        networkId = wifiManager.addNetwork(conf);
        if (networkId == -1) {
            callback.onComplete(ConnectCallback.Status.ADDING_NETWORK_FAILURE, formattedSSID);
            return;
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    //Enable the network id, for android to connect
                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    for (WifiConfiguration i : list) {
                        if (i.SSID != null && i.SSID.equals(formattedSSID)) {

                            if (wifiManager.disconnect() && wifiManager.enableNetwork(i.networkId, true)) {
                                Log.d(TAG, "Connecting to wifi network: " + formattedSSID);
                                callback.onComplete(ConnectCallback.Status.SUCCESS, formattedSSID);
                            } else {
                                callback.onComplete(ConnectCallback.Status.FAILURE, formattedSSID);
                            }
                        }
                    }
                }
            }, 2000);
        }
    }

    @Override
    public void connect(String networkName, String password, SecurityType securityType, final ConnectCallback callback) {

        if (null == networkName || networkName.isEmpty()) {
            Log.d(TAG, "Network name for connecting null or empty");
            callback.onComplete(ConnectCallback.Status.INVALID_NETWORK_NAME, networkName);
            return;
        }
        final String formattedNetworkName = formatString(networkName);
        final String formattedPassword = (null != password) ? formatString(password) : null;

        if (connect(formattedNetworkName)) {
            callback.onComplete(ConnectCallback.Status.SUCCESS, formattedNetworkName);
            return;
        }

        // create network configuration
        final WifiConfiguration conf = createWifiConfiguration(formattedNetworkName, formattedPassword, securityType);

        // add network configuration
        final int networkId = wifiManager.addNetwork(conf);

        if (networkId == -1) {
            callback.onComplete(ConnectCallback.Status.ADDING_NETWORK_FAILURE, formattedNetworkName);
            return;
        }

        connect(networkId, formattedNetworkName, 2000, callback);
    }

    /**
     * Attempt connecting to a network already configured in {@link android.net.wifi.WifiManager}
     *
     * @param networkName
     * @return true if available to connect to the network, false otherwise
     */
    private boolean connect(final String networkName) {
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(networkName)) {
                if (wifiManager.disconnect() && wifiManager.enableNetwork(i.networkId, true)) {
                    Log.d(TAG, "Connecting to already configured wifi network: " + networkName);
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
     * @param networkName
     * @param delay       delay in milliseconds
     * @param callback
     */

    private void connect(final int networkId, final String networkName, final long delay, final ConnectCallback callback) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (wifiManager.disconnect() && wifiManager.enableNetwork(networkId, true)) {
                    Log.d(TAG, "Connecting to wifi network: " + networkName);
                    callback.onComplete(ConnectCallback.Status.SUCCESS, networkName);
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

//    /**
//     * Validates the network name passed and returns formatted network name, i.e. with quotes. For instance, if network name is home. The formatted output is "home"
//     *
//     * @return Formatted output if passed network name is valid, null otherwise
//     */
//    private String formatNetworkName(String networkName) {
//        String formattedNetworkName = null;
//        if (null != networkName || networkName.isEmpty()) {
//            Log.d(TAG, "Invalid network name");
//            return formattedNetworkName;
//        }
//        formattedNetworkName = "\"" + networkName + "\"";
//        return formattedNetworkName;
//    }

    /**
     * Returns formatted string, i.e. with quotes. For instance, if network name is home. The formatted output is "home"
     *
     * @param s should be non-null
     * @return formatted string with quotes
     */
    private String formatString(String s) {
        return "\"" + s + "\"";
    }

    /**
     * Get an iterable of networks currently available and/or previously saved by the user.
     * Performs merging of two separate lists:
     * <ul>
     * <li>Currently visible networks</li>
     * <li>Saved networks</li>
     * </ul>
     *
     * @return Iterable of type {@link Network} and/or subtype {@link ConfiguredNetwork}<br>The iterable follows the following order:
     * <ul>
     * <li>Saved+Available networks</li>
     * <li>Saved networks</li>
     * <li>Available networks</li>
     * </ul>
     */
    @Override
    public Iterable<Network> getNetworks() {
        return null;
    }

    @Override
    public void destroy() {
        this.connectivityManager = null;
        this.wifiManager = null;
        this.networkInfo = null;
        unRegisterReceiver(this.context);
    }


    private Iterable<Network> getAvailableNetworks() {
        Iterable<Network> availableNetworks = new ArrayList<Network>();
        List<WifiConfiguration> currentNetworks = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration wc : currentNetworks) {

        }
        return null;
    }

    /**
     * Register the broadcast receiver to listen for wifi scan results
     *
     * @param context {@link Context} passed in {@link AndroidWifiManager#AndroidWifiManager(Context)}
     */
    private void registerReceiver(Context context) {
        this.wifiBroadCastReceiver = new WifiBroadCastReceiver();
        context.registerReceiver(this.wifiBroadCastReceiver, new IntentFilter(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        availableNetworks = wifiManager.getScanResults();
        Log.i(TAG, "Detect wifi networks: " + availableNetworks.size());
        for (int i = 0; i < availableNetworks.size(); i++) {
            Log.i(TAG, i + " --> " + availableNetworks.get(i).SSID);
        }

        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();
        for (int i = 0; i < wifiConfigs.size(); i++) {
            Log.i(TAG, i + " --> " + wifiConfigs.get(i).SSID + wifiConfigs.get(i).hiddenSSID);
        }

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
            availableNetworks = wifiManager.getScanResults();
            Log.i(TAG, "got new wifi networks: " + availableNetworks.size());
        }
    }
}
