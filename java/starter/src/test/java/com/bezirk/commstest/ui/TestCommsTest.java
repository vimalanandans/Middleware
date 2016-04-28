package com.bezirk.commstest.ui;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.comms.BezirkComms;
import com.bezirk.devices.UPADeviceForPC;
import com.bezirk.devices.UPADeviceInterface;
import com.bezrik.network.BezirkNetworkUtilities;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestCommsTest {
    int pingCount = 5;

    com.bezirk.commstest.ui.CommsTest commsTest;
    UPADeviceInterface upaDeviceInterface;
    UPADeviceForPC upaDevice = Mockito.mock(UPADeviceForPC.class);
    private boolean recievedPing = false;

    @Before
    public void init() {
        upaDevice.setDeviceLocation(null);
        upaDevice.setDeviceName("Test-PC");
        BezirkCompManager.setUpaDevice(upaDevice);
        getInetAddress();
        IUpdateResponseMock responseUT = new IUpdateResponseMock();
        commsTest = new CommsTest(responseUT);
    }

    @Test
    public void testSendPing() {
        commsTest.sendPing(pingCount);
        assertTrue("Ping not received.", recievedPing);
    }

    @Test
    public void testGetSelectedServices() {
        assertNull("Pong is not null when testPingID is not null",
                commsTest.getSelectedServices("testPingId"));

    }

    private void getInetAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {

                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && inetAddress.isSiteLocalAddress()) {
                        BezirkNetworkUtilities.getIpForInterface(intf);
                        BezirkComms.setINTERFACE_NAME(intf.toString());

                    }

                }
            }
        } catch (SocketException e) {
            fail("Unable to fetch network interface");
        }

    }

    class IUpdateResponseMock implements IUpdateResponse {

        @Override
        public void updatePingResposne(String response) {
            //Nothing to be done

        }

        @Override
        public void updateUIPingSent(com.bezirk.commstest.ui.PingMessage msg) {
            if (pingCount == msg.pingId) {

                recievedPing = true;
            }

        }

        @Override
        public void updateUIPingReceived(com.bezirk.commstest.ui.PingMessage msg) {
            //Nothing to be done

        }

        @Override
        public void updateUIPongSent(PingMessage msg) {
            //Nothing to be done
        }

        @Override
        public void updateUIPongReceived(com.bezirk.commstest.ui.PongMessage msg, int size) {
            //Nothing to be done
        }

    }

}
