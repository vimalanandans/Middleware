package com.bezirk.starter;

import com.bezirk.comms.BezirkCommsPC;
import com.bezirk.util.MockSetUpUtilityForUhuPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.NetworkInterface;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * This testcase verifies the working of UhuPCNetwork Utility
 *
 * @author AJC6KOR
 */
public class UhuPCNetworkUtilTest {

    private static MockSetUpUtilityForUhuPC mockSetUP = new MockSetUpUtilityForUhuPC();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        System.clearProperty("InterfaceName");

    }

    @Test
    public void test() {

        testFetchNetworkInterface();

    }

    /**
     * Positive Testcase :
     * <p/>
     * Network interface is fetched after initializing BezirkCommsPC.
     * Utility should return interface successfully.
     */
    private void testFetchNetworkInterface() {

        com.bezirk.starter.UhuPCNetworkUtil uhuPCNetworkUtil = new com.bezirk.starter.UhuPCNetworkUtil();
        BezirkConfig bezirkConfig = new BezirkConfig();

        BezirkCommsPC.init();
        NetworkInterface intf = null;
        try {
            intf = uhuPCNetworkUtil.fetchNetworkInterface(bezirkConfig);
        } catch (Exception e) {
            fail("Unable to fetch network interface. " + e.getMessage());
        }
        assertNotNull("Unable to fetch network interface.", intf);

    }

}
