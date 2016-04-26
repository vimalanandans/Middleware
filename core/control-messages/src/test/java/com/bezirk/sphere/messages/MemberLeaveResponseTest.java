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

import static org.junit.Assert.*;

/**
 * This testCase verifies the MemberLeaveResponse by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class MemberLeaveResponseTest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(MemberLeaveResponseTest.class);

    private static final String sphereId = "TestSphere";
    private static final String sphereName = "Test";
    private static final UhuZirkId serviceId = new UhuZirkId("ServiceA");
    private static final UhuZirkEndPoint sender = new UhuZirkEndPoint(serviceId);
    private static final UhuZirkId serviceBId = new UhuZirkId("ServiceB");
    private static final UhuZirkEndPoint recipient = new UhuZirkEndPoint(serviceBId);
    private static final String key = "TESTKEY";

    private static InetAddress inetAddr;


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        LOGGER.info("***** Setting up MemberLeaveResponseTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        LOGGER.info("***** Shutting down MemberLeaveResponseTest TestCase *****");
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

            LOGGER.error("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void testMemberLeaveResponse() {

        com.bezirk.sphere.messages.MemberLeaveResponse memberLeaveResponse = new com.bezirk.sphere.messages.MemberLeaveResponse(sphereId, 0, true, true, sender, recipient, serviceId, sphereName, key);
        String serializedMessage = memberLeaveResponse.serialize();
        com.bezirk.sphere.messages.MemberLeaveResponse deserializedMemberLeaveResponse = com.bezirk.sphere.messages.MemberLeaveResponse.deserialize(serializedMessage, com.bezirk.sphere.messages.MemberLeaveResponse.class);
        assertEquals("ZirkId not equal to the set value.", serviceId, deserializedMemberLeaveResponse.getServiceId());
        assertEquals("SphereName not equal to the set value.", sphereName, deserializedMemberLeaveResponse.getSphere_Name());
        assertTrue("IsRemovedSuccessfully not equal to the set value.", deserializedMemberLeaveResponse.isRemovedSuccessfully());
        assertTrue("IsSignatureVerified not equal to the set value.", deserializedMemberLeaveResponse.isSignatureVerified());

    }

}
