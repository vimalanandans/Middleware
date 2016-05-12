package com.bezirk.middleware.android.libraries.wifimanager;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * This class tests the {@link DataManager}
 *
 * @author Rishabh Gulati
 */
public class DataManagerTest extends InstrumentationTestCase {
    private DataManager dataManager;
    private final static String NETWORK_NAME = "testNetwork";
    private final static String NETWORK_PASSWORD = "testPassword";
    private final static WifiManager.SecurityType SECURITY_TYPE = WifiManager.SecurityType.WPA2;

    @SmallTest
    public void testSaveNetwork() {
        assertTrue(saveNetwork());
    }

    @SmallTest
    public void testGetNetwork() {
        saveNetwork();
        SavedNetwork savedNetwork = dataManager.getSavedNetwork(NETWORK_NAME);
        assertNotNull(savedNetwork);
        assertEquals(savedNetwork.getName(), NETWORK_NAME);
        assertEquals(savedNetwork.getPassword(), NETWORK_PASSWORD);
        assertEquals(savedNetwork.getSecurityType(), SECURITY_TYPE);
    }

    @SmallTest
    public void testSavedNetworks() {
        saveNetwork();
        assertEquals(dataManager.getSavedNetworks().size(), 1);
    }

    private boolean saveNetwork() {
        return dataManager.saveNetwork(NETWORK_NAME, NETWORK_PASSWORD, SECURITY_TYPE);
    }

    //TODO use setupBeforeClass instead
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dataManager = new DataManager(getInstrumentation().getContext());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        dataManager.clean();
    }
}
