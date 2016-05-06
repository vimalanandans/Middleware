package com.bezirk.middleware.android.libraries.wifimanager;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class tests the implementation of {@link AndroidWifiManager}
 * {@link AndroidWifiManager} uses android libraries thus functionality is tested using {@link InstrumentationTestCase}
 *
 * @author Rishabh Gulati
 */
public class Test extends InstrumentationTestCase implements WifiManager.ConnectCallback {
    private static final String TAG = Test.class.getCanonicalName();

    @SmallTest
    public void test() {
        WifiManager wifiManager = new AndroidWifiManager(getInstrumentation().getContext());
        assertEquals(true, wifiManager.isEnabled());
        //Log.d(TAG, wifiManager.getNetworkName());
        //Log.d(TAG, wifiManager.getSecurityType().toString());
        //Log.d(TAG, Boolean.toString(wifiManager.isConnected()));
        wifiManager.connect("bezirk5g", "bezirk@nighthawk@5g", WifiManager.SecurityType.WPA2, this);
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.i(TAG, "Wait complete");
//            }
//        }, 40000);
    }

    @Override
    public void onComplete(Status status, String networkName) {
        Log.d(TAG, "network name " + networkName + " Status " + status.toString());
    }
}
