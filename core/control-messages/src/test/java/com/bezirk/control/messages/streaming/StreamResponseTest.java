package com.bezirk.control.messages.streaming;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.streaming.control.Objects.StreamRecord.StreamingStatus;
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

/**
 * This testCase verifies the StreamResponse POJO by retrieving the field discriminator.
 *
 * @author AJC6KOR
 */
public class StreamResponseTest {

    private static final Logger log = LoggerFactory
            .getLogger(StreamResponseTest.class);

    private static final String sphereName = "Home";
    private static final UhuServiceId serviceAId = new UhuServiceId("ServiceA");
    private static final UhuServiceId serviceBId = new UhuServiceId("ServiceB");
    private static final UhuServiceEndPoint recipient = new UhuServiceEndPoint(serviceAId);
    private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceBId);
    private static InetAddress inetAddr;
    private String strmKey = "STREAM_KEY";
    private String streamIp = UhuNetworkUtilities.getDeviceIp();
    private int streamPort = 7999;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up StreamResponseTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down StreamResponseTest TestCase *****");
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
    public void testStreamResponse() {

        com.bezirk.control.messages.streaming.StreamResponse streamResponse = new com.bezirk.control.messages.streaming.StreamResponse(sender, recipient, sphereName, strmKey, StreamingStatus.READY, streamIp, streamPort);
        assertEquals("StreamResponse is not having discriminator set properly.", Discriminator.StreamResponse, streamResponse.getDiscriminator());


    }
}
