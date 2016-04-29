package com.bezirk.control.messages.streaming.rtc;

import com.bezirk.control.messages.streaming.rtc.RTCControlMessage.RTCControlMessageType;
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

/**
 * This testCase verifies the RTCControlMessage by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class RTCControlMessageTest {
    private static final Logger logger = LoggerFactory.getLogger(RTCControlMessageTest.class);

    private static final BezirkZirkId serviceAId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(
            serviceAId);
    private static final BezirkZirkId serviceBId = new BezirkZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(
            serviceBId);
    private static InetAddress inetAddr;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up RTCControlMessageTest TestCase *****");
        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down RTCControlMessageTest TestCase *****");
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
    public void testRTCControlMessage() {

        String serverIp = "TEST_IP";
        String rtcMsg = "TEST_MESSAGE";
        RTCControlMessageType msgType = RTCControlMessageType.RTCCandidate;
        String uniqueKey = "TEST_KEY";
        com.bezirk.control.messages.streaming.rtc.RTCControlMessage rtcControlMessage = new com.bezirk.control.messages.streaming.rtc.RTCControlMessage(sender, recipient, serverIp, uniqueKey, msgType, rtcMsg);
        String serializedMessage = rtcControlMessage.serialize();
        com.bezirk.control.messages.streaming.rtc.RTCControlMessage deserializedRTCControlMessage = com.bezirk.control.messages.streaming.rtc.RTCControlMessage
                .deserialize(serializedMessage, com.bezirk.control.messages.streaming.rtc.RTCControlMessage.class);
        assertEquals("RTCMessageType not equal to the set value.", msgType,
                deserializedRTCControlMessage.getMsgType());
        assertEquals("RTCMessageType not set properly.",
                rtcMsg, deserializedRTCControlMessage.getRtcMsg());

    }

}
