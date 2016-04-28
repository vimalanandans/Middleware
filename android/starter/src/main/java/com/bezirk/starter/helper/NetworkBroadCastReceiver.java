package com.bezirk.starter.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.bezirk.starter.IUhuStackHandler;
import com.bezirk.starter.MainService;
import com.bezirk.starter.UhuWifiManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Broadcast receiver which keeps listening to the wifi events, for any changes when associating to the network
 */
public class NetworkBroadCastReceiver extends BroadcastReceiver {
    private static final Logger logger = LoggerFactory.getLogger(NetworkBroadCastReceiver.class);

    private final MainService mainService;
    private final IUhuStackHandler stackHandler;
    private UhuWifiManager uhuWifiManager;

    public NetworkBroadCastReceiver(MainService mainService, IUhuStackHandler stackHandler) {
        this.mainService = mainService;
        this.stackHandler = stackHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        uhuWifiManager = UhuWifiManager.getInstance();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String action = intent.getAction();
        if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
            SupplicantState supl_state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            switch (supl_state) {
                case ASSOCIATING:
                    Log.i("SupplicantState", "Trying to Associate with a New Network..");
                    break;
                case COMPLETED:
                    Log.i("SupplicantState", "Connected");
                    handleConnectedState(context, wifiInfo);
                    break;
                case DISCONNECTED:
                    logger.debug("Wifi is in DISCONNECTED state!!!");
                    break;
                default:

            }
        } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            processWifiStateChange(intent);

        } else {
            Log.i("SupplicantState", "Unknown event");
        }
    }

    private void processWifiStateChange(Intent intent) {
        //Trying to know on off status
        int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                WifiManager.WIFI_STATE_UNKNOWN);
        switch (extraWifiState) {
            case WifiManager.WIFI_STATE_DISABLED:
                Log.i("SupplicantState", "Wifi Disabled");
                uhuWifiManager.setConnectedWifiSSID(null);
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.i("SupplicantState", "Wifi Connected");
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                Log.i("SupplicantState", "Unknown");
                break;
            default:
        }
    }

    private void handleConnectedState(Context context, WifiInfo wifiInfo) {
        if (UhuStackHandler.getUhuComms() == null && mainService != null) {
                /*it is observed in few devices that, after receiving completed supplicant state the wifi manager will not retrieve the connection correctly.
                Hence making this separated thread sleep for 3 sec and then starting the stack.*/
            try {
                Thread.sleep(3000);
                //call startStack, this could have happened when you have started stack without wifi turned ON.
                stackHandler.startStack(mainService);
            } catch (InterruptedException e) {
                logger.debug("Exception in waiting thread for network change.", e);
            }

            return;
        } else if (uhuWifiManager.getConnectedWifiSSID() == null || !uhuWifiManager.getConnectedWifiSSID().equals(wifiInfo.getSSID())) {
            //restart comms
            String message = "Bezirk has been reconfigured to Wifi Access Point! " + wifiInfo.getSSID();
            new RestartCommsAsyncTask(context, message, stackHandler).execute();
        }
        uhuWifiManager.setConnectedWifiSSID(wifiInfo.getSSID());
    }

}
