package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.logging.LoggingServiceMessage;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezrik.network.UhuNetworkUtilities;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author ajc6kor
 */
public class BezirkValidatorUtilityTest {

    private static final Logger log = LoggerFactory.getLogger(BezirkValidatorUtilityTest.class);

    static BezirkZirkEndPoint sender = new BezirkZirkEndPoint(new BezirkZirkId("MockServiceA"));
    static String serviceId = "MockServiceB";
    static BezirkZirkId uhuServiceId = new BezirkZirkId(serviceId);
    static BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(uhuServiceId);
    private static InetAddress inetAddr;
    String sphereId = "testSphere";
    boolean isValid = false;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        inetAddr = getInetAddress();
        recipient.device = inetAddr.getHostAddress();
        sender.device = inetAddr.getHostAddress();
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
    public void test() {

        testCheckDiscoveryRequest();

        testCheckHeader();

        testCheckLoggingServiceMessage();

        testCheckProtocolRole();

        testCheckRTCStreamRequest();

        testCheckStreamRequest();

        testCheckUhuServiceId();

        testChekUhuServiceEndPoint();


    }

    private void testChekUhuServiceEndPoint() {

		/*-------------- Positive cases --------------*/
        isValid = BezirkValidatorUtility.checkBezirkZirkEndPoint(recipient);
        assertTrue("Non null serviceEndpoint is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        BezirkZirkId uhuSid = null;
        BezirkZirkEndPoint uhuServiceEndPoint = new BezirkZirkEndPoint(uhuSid);
        isValid = BezirkValidatorUtility.checkBezirkZirkEndPoint(uhuServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        uhuSid = new BezirkZirkId(null);
        uhuServiceEndPoint = new BezirkZirkEndPoint(uhuSid);
        isValid = BezirkValidatorUtility.checkBezirkZirkEndPoint(uhuServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        uhuServiceEndPoint = new BezirkZirkEndPoint(uhuServiceId);
        isValid = BezirkValidatorUtility.checkBezirkZirkEndPoint(uhuServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

    }

    private void testCheckUhuServiceId() {

		/*-------------- Positive cases --------------*/
        isValid = BezirkValidatorUtility.checkUhuServiceId(uhuServiceId);
        assertTrue("Non null serviceID is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        isValid = BezirkValidatorUtility.checkUhuServiceId(null);
        assertFalse("Null serviceID is considered valid by validator", isValid);

        isValid = BezirkValidatorUtility.checkUhuServiceId(new BezirkZirkId(null));
        assertFalse("Null serviceID is considered valid by validator", isValid);
    }

    private void testCheckStreamRequest() {

		/*-------------- Positive cases --------------*/
        StreamRequest request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertTrue("Valid streamRequest is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = BezirkValidatorUtility.checkStreamRequest(null);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, null, "testLabel", "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", "testLabel", null, true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", null, "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, null,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(null, recipient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        BezirkZirkEndPoint recepient = new BezirkZirkEndPoint(new BezirkZirkId("test"));
        recepient.device = "";
        request = new StreamRequest(sender, recepient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = BezirkValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

    }

    private void testCheckRTCStreamRequest() {

		/*-------------- Positive cases --------------*/
        isValid = BezirkValidatorUtility.checkRTCStreamRequest(uhuServiceId, recipient);
        assertTrue("Valid RTCStreamRequest is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = BezirkValidatorUtility.checkRTCStreamRequest(null, recipient);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

        isValid = BezirkValidatorUtility.checkRTCStreamRequest(uhuServiceId, null);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

    }

    private void testCheckProtocolRole() {

		/*-------------- Positive cases --------------*/
        MockProtocolRole pRole = new MockProtocolRole();
        SubscribedRole role = new SubscribedRole(pRole);
        isValid = BezirkValidatorUtility.checkProtocolRole(role);
        assertTrue("Valid protocolrole is considered invalie by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = BezirkValidatorUtility.checkProtocolRole(null);
        assertFalse("Null protocolrole is considered valid by validator.", isValid);

        pRole.setProtocolName(null);
        role = new SubscribedRole(pRole);
        isValid = BezirkValidatorUtility.checkProtocolRole(role);
        assertFalse("Null protocolrole is considered valid by validator.", isValid);

    }

    private void testCheckLoggingServiceMessage() {

		/*-------------- Positive cases --------------*/
        String serverIp = "123.12.10.2";
        String[] sphereList = new String[]{"Sphere1", "Sphere2"};
        LoggingServiceMessage logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, sphereList, true);
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
        assertTrue("Valid logServiceMessage is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(null);
        assertFalse("Invalid logServiceMessage is considered valid by validator.", isValid);

        logServiceMsg = new LoggingServiceMessage(sender, sphereId, null, 2020, sphereList, true);
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
        assertFalse("Invalid logServiceMessage is considered valid by validator.", isValid);

        logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, null, true);
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
        assertFalse("Invalid logServiceMessage is considered valid by validator.", isValid);

        sphereList = new String[]{};
        logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, 2020, sphereList, true);
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
        assertFalse("Invalid logServiceMessage is considered valid by validator.", isValid);

        logServiceMsg = new LoggingServiceMessage(sender, sphereId, serverIp, -1, sphereList, true);
        isValid = BezirkValidatorUtility.checkLoggingServiceMessage(logServiceMsg);
        assertFalse("Invalid logServiceMessage is considered valid by validator.", isValid);
    }

    private void testCheckHeader() {

		/*-------------- Positive cases --------------*/
        Header mHeader = new Header(sphereId, sender, "12", "test");
        isValid = BezirkValidatorUtility.checkHeader(mHeader);
        assertTrue("Valid header is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        mHeader = new Header(null, sender, "12", "test");
        isValid = BezirkValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);

        mHeader = new Header(sphereId, sender, "12", null);
        isValid = BezirkValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);

        mHeader = new Header(sphereId, null, "12", "test");
        isValid = BezirkValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);


    }

    private void testCheckDiscoveryRequest() {

		/*-------------- Positive cases --------------*/
        DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, null, null, 2, 90000, 3);
        isValid = BezirkValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertTrue("Valid discoveryRequest is considered invalid by validator.", isValid);


		/*-------------- Negative cases --------------*/
        discoveryRequest = new DiscoveryRequest(null, sender, null, null, 2, 60000, 3);
        isValid = BezirkValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertFalse("Invalid discoveryRequest is considered valid by validator.", isValid);

        BezirkZirkEndPoint sep = new BezirkZirkEndPoint(uhuServiceId);
        discoveryRequest = new DiscoveryRequest(sphereId, sep, null, null, 2, 60000, 3);
        isValid = BezirkValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertFalse("Invalid discoveryRequest is considered valid by validator.", isValid);

    }

    class MockProtocolRole extends ProtocolRole {

        String protocolName = this.getClass().getSimpleName();

        @Override
        public String getProtocolName() {
            return protocolName;
        }

        public void setProtocolName(String protocolName) {
            this.protocolName = protocolName;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return null;
        }

        @Override
        public String[] getStreamTopics() {
            return null;
        }


    }
}