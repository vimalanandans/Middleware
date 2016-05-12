package com.bezirk.middleware.android.libraries.wifimanager;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class tests {@link AndroidWifiManager}
 * {@link AndroidWifiManager} uses android libraries thus functionality is tested using {@link InstrumentationTestCase}
 *
 * @author Rishabh Gulati
 */
public class AndroidWifiManagerTest extends InstrumentationTestCase implements WifiManager.ConnectCallback {
    private static final String TAG = AndroidWifiManagerTest.class.getCanonicalName();
    private final static String NETWORK_NAME = "Rishabh";
    private final static String NETWORK_PASSWORD = "T53QZWXMG8Q9442Y";
    private final static WifiManager.SecurityType SECURITY_TYPE = WifiManager.SecurityType.WPA2;

    private WifiManager wifiManager;

    @SmallTest
    public void testConnect() {
        wifiManager.connect(NETWORK_NAME, NETWORK_PASSWORD, SECURITY_TYPE, true, this);
    }

    @SmallTest
    public void testConnectAndSave() {
        wifiManager.connect(NETWORK_NAME, NETWORK_PASSWORD, SECURITY_TYPE, true, this);
        try {
            Thread.sleep(3000); //to wait for network connection and save operation to complete
            SavedNetwork savedNetwork = wifiManager.getSavedNetwork(NETWORK_NAME);
            assertNotNull(savedNetwork);
            assertEquals(savedNetwork.getName(), NETWORK_NAME);
            assertEquals(savedNetwork.getPassword(), NETWORK_PASSWORD);
            assertEquals(savedNetwork.getSecurityType(), SECURITY_TYPE);
            Log.i(TAG, savedNetwork.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //TODO use setupBeforeClass instead
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        wifiManager = new AndroidWifiManager(getInstrumentation().getContext());
        ((AndroidWifiManager)wifiManager).getDataManager().clean(); //clean saved information
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    public void onComplete(Status status, String networkName) {
    }
}
