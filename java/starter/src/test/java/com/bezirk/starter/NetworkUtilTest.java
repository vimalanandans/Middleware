package com.bezirk.starter;

import com.bezirk.comms.BezirkCommsPC;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.NetworkInterface;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * This testcase verifies the working of BezirkPCNetwork Utility
 *
 * @author AJC6KOR
 */
public class NetworkUtilTest {
    private static MockSetUpUtilityForBezirkPC mockSetUP = new MockSetUpUtilityForBezirkPC();

    @BeforeClass
    public static void setUpBeforeClass() {
        System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
    }

    @AfterClass
    public static void tearDownAfterClass() {
        System.clearProperty("InterfaceName");
    }

    @Test
    public void test() {
        testFetchNetworkInterface();
    }

    /**
     * Positive Testcase :
     * <p>
     * Network interface is fetched after initializing BezirkCommsPC.
     * Utility should return interface successfully.
     * </p>
     */
    private void testFetchNetworkInterface() {

        NetworkUtil networkUtil = new NetworkUtil();
        BezirkConfig bezirkConfig = new BezirkConfig();

       // BezirkCommsPC.init();
        NetworkInterface intf = null;
        try {
            intf = networkUtil.fetchNetworkInterface(bezirkConfig);
        } catch (Exception e) {
            fail("Unable to fetch network interface. " + e.getMessage());
        }
        assertNotNull("Unable to fetch network interface.", intf);

    }

}
