/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.android.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Broadcast receiver which keeps listening to the wifi events, for any changes when associating to the network
 */
public class NetworkBroadCastReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NetworkBroadCastReceiver.class);
    private final WifiManager wifiManager;

    public NetworkBroadCastReceiver(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String action = intent.getAction();
        switch (action) {
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                switch (supplicantState) {
                    case ASSOCIATING:
                        logger.trace("Trying to Associate with a New Network..");
                        break;
                    case COMPLETED:
                        logger.trace("Connected");
                        handleConnectedState(context, wifiInfo);
                        break;
                    case DISCONNECTED:
                        logger.debug("Wifi is in DISCONNECTED state!!!");
                        break;
                    default:

                }
                break;
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                processWifiStateChange(intent);

                break;
            default:
                logger.trace("Unknown event");
                break;
        }
    }

    private void processWifiStateChange(Intent intent) {
        int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);
        switch (extraWifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                logger.trace("Wifi Disabled");
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                logger.trace("Wifi Connected");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                logger.trace("Unknown");
                break;
            default:
        }
    }

    private void handleConnectedState(Context context, WifiInfo wifiInfo) {
        logger.debug("Connected to wifi " + wifiInfo.getSSID());
        logger.warn("Comms start/restart currently not initiated based on wifi change");
    }
}
