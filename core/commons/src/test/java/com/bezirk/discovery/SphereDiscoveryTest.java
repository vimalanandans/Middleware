package com.bezirk.discovery;

import com.bezirk.control.messages.discovery.SphereDiscoveryResponse;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.objects.BezirkSphereInfo;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezirk.sphere.security.CryptoEngine;
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
 * @author ajc6kor
 */
public class SphereDiscoveryTest {


    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId zirkId = new BezirkZirkId("ZirkB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(zirkId);
    private static final BezirkZirkEndPoint zirkBEndPoint = new BezirkZirkEndPoint(new BezirkZirkId("Zirk123B"));

    private static final String requestKey = "REQUEST_KEY";
    private static InetAddress inetAddr;


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
        SphereDiscoveryRecord disc = new SphereDiscoveryRecord(sphereId, timeout, max);

        BezirkDeviceInterface upaDevice = null;
        SphereRegistry sphereRegistry = new SphereRegistry();
        CryptoEngine cryptoEngine = new CryptoEngine(sphereRegistry);
        BezirkSphere bezirkSphere = new BezirkSphere(cryptoEngine, upaDevice, sphereRegistry);

        SphereDiscovery sphereDiscovery = new SphereDiscovery(bezirkSphere);

        sphereDiscovery.addRequest(dlabel, disc);

        DiscoveryLabel dlabelTemp = new DiscoveryLabel(zirkBEndPoint, 14);
        sphereDiscovery.addRequest(dlabelTemp, disc);

        assertEquals("DiscoveredMap size is not equal to the number of requests added", 2, getDiscoveredMapsize(sphereDiscovery));


        SphereDiscoveryResponse response = new SphereDiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
        BezirkSphereInfo bezirkSphereInfo = new BezirkSphereInfo(sphereId, "Test", BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME, null, null);
        response.setBezirkSphereInfo(bezirkSphereInfo);

        assertTrue("Unable to add response to SphereDiscovery.", sphereDiscovery.addResponse(response));

        sphereDiscovery.remove(dlabelTemp);
        assertEquals("DiscoveredMap size is not equal to 1 after removing entry.", 1, getDiscoveredMapsize(sphereDiscovery));

        BezirkZirkEndPoint invalidRecepient = new BezirkZirkEndPoint(null);
        invalidRecepient.device = getInetAddress().getHostAddress();
        response = new SphereDiscoveryResponse(invalidRecepient, sphereId, requestKey, discoveryId);
        response.setBezirkSphereInfo(bezirkSphereInfo);
        assertFalse("sphere Discovery response is added even when recepient is having null serviceID", sphereDiscovery.addResponse(response));


        response = new SphereDiscoveryResponse(recipient, sphereId, requestKey, 24);
        response.setBezirkSphereInfo(bezirkSphereInfo);
        assertFalse("sphere Discovery response is added even when discovery id is invalid.", sphereDiscovery.addResponse(response));

    }

    private int getDiscoveredMapsize(SphereDiscovery sphereDiscovery) {

        int discoveredMapSize = 0;

        try {
            if (sphereDiscovery.getDiscoveredMap() != null)

                discoveredMapSize = sphereDiscovery.getDiscoveredMap().size();

        } catch (InterruptedException e) {

            fail("Unable to retrieve discovered map.");

        }
        return discoveredMapSize;
    }


}
