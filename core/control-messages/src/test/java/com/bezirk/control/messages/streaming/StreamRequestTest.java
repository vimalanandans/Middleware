package com.bezirk.control.messages.streaming;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.network.BezirkNetworkUtilities;

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

/**
 * This testCase verifies the StreamRequest POJO by retrieving the field discriminator.
 *
 * @author AJC6KOR
 */
public class StreamRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamRequestTest.class);

    private static final String sphereName = "Home";
    private static final Location location = new Location("OFFICE1", "BLOCk1", "ROOM1");
    private static final String key = "TESTKEY";
    private static final ZirkId serviceAId = new ZirkId("ServiceA");
    private static final ZirkId serviceBId = new ZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceAId);
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceBId);
    private static final short localStreamId = 10;
    private static InetAddress inetAddr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up StreamRequestTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down StreamRequestTest TestCase *****");
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

            logger.error("Unable to fetch network interface");

        }
        return null;
    }

    @Test
    public void testStreamRequest() {

        com.bezirk.control.messages.streaming.StreamRequest streamRequest = new com.bezirk.control.messages.streaming.StreamRequest(sender, recipient, sphereName, key, location, "TESTSTREAM", "TEST", null, true, true, false, localStreamId);
        assertEquals("StreamRequest is not having discriminator set properly.", Discriminator.StreamRequest, streamRequest.getDiscriminator());


    }
}
