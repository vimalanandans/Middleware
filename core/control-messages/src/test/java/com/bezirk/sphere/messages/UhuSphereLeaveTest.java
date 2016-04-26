package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;
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
 * This testCase verifies the UhuSphereLeave by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class UhuSphereLeaveTest {

    private static final Logger log = LoggerFactory
            .getLogger(UhuSphereLeaveTest.class);


    private static final String sphereId = "TestSphere";
    private static final UhuZirkId serviceId = new UhuZirkId("ServiceA");
    private static final UhuZirkEndPoint sender = new UhuZirkEndPoint(serviceId);
    private static final UhuZirkId serviceBId = new UhuZirkId("ServiceB");
    private static final UhuZirkEndPoint recipient = new UhuZirkEndPoint(serviceBId);
    private static InetAddress inetAddr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up UhuSphereLeaveTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down UhuSphereLeaveTest TestCase *****");
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
    public void testUhuSphereLeave() {
        com.bezirk.sphere.messages.UhuSphereLeave uhuSphereLeave = new com.bezirk.sphere.messages.UhuSphereLeave(sphereId, serviceId, sender, recipient);
        String serializedMessage = uhuSphereLeave.serialize();
        com.bezirk.sphere.messages.UhuSphereLeave deserializedUhuSphereLeave = com.bezirk.sphere.messages.UhuSphereLeave.deserialize(serializedMessage, com.bezirk.sphere.messages.UhuSphereLeave.class);
        assertEquals("Deserialized UhuSphereLeave serviceId are not equal to original serviceId", serviceId, deserializedUhuSphereLeave.getServiceId());
        assertNotNull("Deserialized UhuSphereLeave dont have the time set in it.", deserializedUhuSphereLeave.getTime());

    }
}
