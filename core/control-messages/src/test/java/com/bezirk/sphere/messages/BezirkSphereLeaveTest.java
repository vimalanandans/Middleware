package com.bezirk.sphere.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezrik.network.BezirkNetworkUtilities;

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
 * This testCase verifies the BezirkSphereLeave by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class BezirkSphereLeaveTest {

    private static final Logger log = LoggerFactory
            .getLogger(BezirkSphereLeaveTest.class);


    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private static final BezirkZirkId serviceBId = new BezirkZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceBId);
    private static InetAddress inetAddr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up BezirkSphereLeaveTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down BezirkSphereLeaveTest TestCase *****");
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

            log.error("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void testBezirkSphereLeave() {
        BezirkSphereLeave bezirkSphereLeave = new BezirkSphereLeave(sphereId, serviceId, sender, recipient);
        String serializedMessage = bezirkSphereLeave.serialize();
        BezirkSphereLeave deserializedBezirkSphereLeave = BezirkSphereLeave.deserialize(serializedMessage, BezirkSphereLeave.class);
        assertEquals("Deserialized BezirkSphereLeave zirkId are not equal to original zirkId", serviceId, deserializedBezirkSphereLeave.getServiceId());
        assertNotNull("Deserialized BezirkSphereLeave dont have the time set in it.", deserializedBezirkSphereLeave.getTime());

    }
}