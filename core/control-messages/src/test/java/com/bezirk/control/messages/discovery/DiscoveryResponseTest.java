package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezrik.network.UhuNetworkUtilities;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This testCase verifies the DiscoveryResponse by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class DiscoveryResponseTest {

    private static final Logger log = LoggerFactory
            .getLogger(DiscoveryResponseTest.class);

    private static final String sphereId = "TestSphere";
    private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
    private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceId);
    private static final String requestKey = "REQUEST_KEY";
    private static InetAddress inetAddr;
    private int discoveryId = 2;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up DiscoveryResponseTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down DiscoveryResponseTest TestCase *****");
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

                        inetAddr = UhuNetworkUtilities.getIpForInterface(intf);
                        return inetAddr;
                    }

                }
            }
        } catch (SocketException e) {

            log.error("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void testDiscoveryResponse() {

        com.bezirk.control.messages.discovery.DiscoveryResponse discoveryResponse = new com.bezirk.control.messages.discovery.DiscoveryResponse(recipient, sphereId, requestKey, discoveryId);
        String serializedMessage = discoveryResponse.serialize();
        com.bezirk.control.messages.discovery.DiscoveryResponse deserializedDiscoveryResponse = com.bezirk.control.messages.discovery.DiscoveryResponse.deserialize(serializedMessage, com.bezirk.control.messages.discovery.DiscoveryResponse.class);
        assertEquals("DiscoveryId not equal to the set value.", Integer.valueOf(discoveryId), deserializedDiscoveryResponse.getReqDiscoveryId());
        assertEquals("Discriminator not set properly.", Discriminator.DiscoveryResponse, deserializedDiscoveryResponse.getDiscriminator());
        assertNotNull("Discovered Service list is null", deserializedDiscoveryResponse.getServiceList());


    }

}