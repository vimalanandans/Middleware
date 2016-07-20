package com.bezirk.control.messages.streaming;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.streaming.control.Objects.StreamRecord.StreamingStatus;
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

/**
 * This testCase verifies the StreamResponse POJO by retrieving the field discriminator.
 *
 * @author AJC6KOR
 */
public class StreamDescriptorResponseTest {
    private static final Logger logger = LoggerFactory.getLogger(StreamDescriptorResponseTest.class);

    private static final String sphereName = "Home";
    private static final ZirkId serviceAId = new ZirkId("ServiceA");
    private static final ZirkId serviceBId = new ZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceAId);
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceBId);
    private static InetAddress inetAddr;
    private String strmKey = "STREAM_KEY";
    private String streamIp = BezirkNetworkUtilities.getDeviceIp();
    private int streamPort = 7999;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up StreamDescriptorResponseTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down StreamDescriptorResponseTest TestCase *****");
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
    public void testStreamResponse() {

        com.bezirk.control.messages.streaming.StreamResponse streamResponse = new com.bezirk.control.messages.streaming.StreamResponse(sender, recipient, sphereName, strmKey, StreamingStatus.READY, streamIp, streamPort);
        assertEquals("StreamResponse is not having discriminator set properly.", Discriminator.StreamResponse, streamResponse.getDiscriminator());


    }
}