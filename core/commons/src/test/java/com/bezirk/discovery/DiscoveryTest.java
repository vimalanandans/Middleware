package com.bezirk.discovery;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.control.messages.discovery.DiscoveryResponse;
import com.bezirk.messagehandler.ZirkMessageHandler;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.pipe.MockCallBackZirk;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.pipe.MockBezirkZirk;
import com.bezrik.network.BezirkNetworkUtilities;

import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This testcase validates the processing done by Discovery.
 *
 * @author ajc6kor
 */
public class DiscoveryTest {

    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId zirkId = new BezirkZirkId("ZirkB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(zirkId);
    private static final BezirkZirkEndPoint zirkBEndPoint = new BezirkZirkEndPoint(new BezirkZirkId("Zirk123B"));

    private static final String requestKey = "REQUEST_KEY";
    private static InetAddress inetAddr;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
        zirkBEndPoint.device = inetAddr.getHostAddress();
    }

    private static InetAddress getInetAddress() {
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

                        inetAddr = BezirkNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {

            fail("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void test() {

        int discoveryId = 12;
        DiscoveryLabel dlabel = new DiscoveryLabel(recipient, discoveryId);
        long timeout = 10000;
        int max = 1;
        DiscoveryRecord disc = new DiscoveryRecord(timeout, max);

        Discovery discovery = new Discovery();
        discovery.addRequest(dlabel, disc);

        DiscoveryLabel dlabelTemp = new DiscoveryLabel(zirkBEndPoint, 14);
        discovery.addRequest(dlabelTemp, disc);

        assertEquals("DiscoveredMap size is not equal to the number of requests added", 2, getDiscoveredMapsize(discovery));

        ZirkMessageHandler bezirkCallback = new MockCallBackZirk(new MockBezirkZirk());
        BezirkCompManager.setplatformSpecificCallback(bezirkCallback);

		/*Testing addResponse api in discovery*/
        DiscoveryResponse response = new DiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
        BezirkDiscoveredZirk zirk = getZirk();
        response.getZirkList().add(zirk);
        discovery.addResponse(response);
        assertEquals("DiscoveredMap size is not equal to 1 after adding response.", 1, getDiscoveredMapsize(discovery));

		/*Testing remove api in discovery*/
        dlabelTemp = new DiscoveryLabel(recipient, 25);
        discovery.addRequest(dlabelTemp, disc);
        discovery.remove(dlabelTemp);
        assertEquals("DiscoveredMap size is not equal to 1 after removing entry.", 1, getDiscoveredMapsize(discovery));

		/*Testing addResponse api in discovery for invalid recepient*/
        BezirkZirkEndPoint invalidRecepient = new BezirkZirkEndPoint(null);
        invalidRecepient.device = getInetAddress().getHostAddress();
        response = new DiscoveryResponse(invalidRecepient, sphereId, requestKey, discoveryId);
        assertFalse("Discovery response is added even for invalid recepient.", discovery.addResponse(response));

		/*Testing addResponse for servicelist less than max mentioned in discovery record*/
        disc = new DiscoveryRecord(200000, 3);
        discovery.addRequest(dlabel, disc);
        response = new DiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
        assertTrue("Discovery response is not added even when discovered servicelist is less than max and timeout happened.", discovery.addResponse(response));

		/*Testing addResponse for invalid discoveryId*/
        disc = new DiscoveryRecord(200000, 3);
        discovery.addRequest(dlabel, disc);
        response = new DiscoveryResponse(recipient, sphereId, requestKey, 24);
        assertFalse("Discovery response is not added even when discoveryId is invalid.", discovery.addResponse(response));

    }

    private BezirkDiscoveredZirk getZirk() {
        String zirkName = "ZirkB";
        BezirkZirkEndPoint sep = new BezirkZirkEndPoint(new BezirkZirkId("ZirkB123"));
        return new BezirkDiscoveredZirk(sep, zirkName, null, new Location(null));
    }

    private int getDiscoveredMapsize(Discovery discovery) {

        int discoveredMapSize = 0;

        try {
            if (discovery.getDiscoveredMap() != null)

                discoveredMapSize = discovery.getDiscoveredMap().size();

        } catch (InterruptedException e) {

            fail("Unable to retrieve discovered map.");

        }
        return discoveredMapSize;
    }


}
