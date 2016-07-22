package com.bezirk.util;

import com.bezirk.control.messages.Header;
import com.bezirk.control.messages.discovery.DiscoveryRequest;
import com.bezirk.control.messages.streaming.StreamRequest;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezrik.network.NetworkUtilities;

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
public class ValidatorUtilityTest {
    private static final Logger logger = LoggerFactory.getLogger(ValidatorUtilityTest.class);

    static BezirkZirkEndPoint sender = new BezirkZirkEndPoint(new ZirkId("MockServiceA"));
    static String serviceId = "MockServiceB";
    static ZirkId zirkId = new ZirkId(serviceId);
    static BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(zirkId);
    private static InetAddress inetAddr;
    String sphereId = "testSphere";
    boolean isValid = false;


    @BeforeClass
    public static void setUpBeforeClass() {

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

                        inetAddr = NetworkUtilities.getIpForInterface(intf);
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
    public void test() {

        testCheckDiscoveryRequest();

        testCheckHeader();

        testCheckProtocolRole();

        testCheckRTCStreamRequest();

        testCheckStreamRequest();

        testCheckBezirkServiceId();

        testCheckBezirkServiceEndPoint();


    }

    private void testCheckBezirkServiceEndPoint() {

		/*-------------- Positive cases --------------*/
        isValid = ValidatorUtility.checkBezirkZirkEndPoint(recipient);
        assertTrue("Non null serviceEndpoint is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        ZirkId bezirkSid = null;
        BezirkZirkEndPoint bezirkServiceEndPoint = new BezirkZirkEndPoint(bezirkSid);
        isValid = ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        bezirkSid = new ZirkId(null);
        bezirkServiceEndPoint = new BezirkZirkEndPoint(bezirkSid);
        isValid = ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        bezirkServiceEndPoint = new BezirkZirkEndPoint(zirkId);
        isValid = ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

    }

    private void testCheckBezirkServiceId() {

		/*-------------- Positive cases --------------*/
        isValid = ValidatorUtility.checkBezirkZirkId(zirkId);
        assertTrue("Non null serviceID is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        isValid = ValidatorUtility.checkBezirkZirkId(null);
        assertFalse("Null serviceID is considered valid by validator", isValid);

        isValid = ValidatorUtility.checkBezirkZirkId(new ZirkId(null));
        assertFalse("Null serviceID is considered valid by validator", isValid);
    }

    private void testCheckStreamRequest() {

		/*-------------- Positive cases --------------*/
        StreamRequest request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertTrue("Valid streamRequest is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = ValidatorUtility.checkStreamRequest(null);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, null, "testLabel", "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", "testLabel", null, true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", null, "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, null,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(null, recipient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        BezirkZirkEndPoint recepient = new BezirkZirkEndPoint(new ZirkId("test"));
        recepient.device = "";
        request = new StreamRequest(sender, recepient, sphereId,
                null, null, "testString", "testLabel", "testFile", true, true, true, (short) 3);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

    }

    private void testCheckRTCStreamRequest() {

		/*-------------- Positive cases --------------*/
        isValid = ValidatorUtility.checkRTCStreamRequest(zirkId, recipient);
        assertTrue("Valid RTCStreamRequest is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = ValidatorUtility.checkRTCStreamRequest(null, recipient);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

        isValid = ValidatorUtility.checkRTCStreamRequest(zirkId, null);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

    }

    private void testCheckProtocolRole() {

		/*-------------- Positive cases --------------*/
        MockProtocolRole pRole = new MockProtocolRole();
        SubscribedRole role = new SubscribedRole(pRole);
        isValid = ValidatorUtility.checkProtocolRole(role);
        assertTrue("Valid protocolrole is considered invalie by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = ValidatorUtility.checkProtocolRole(null);
        assertFalse("Null protocolrole is considered valid by validator.", isValid);

        pRole.setProtocolName(null);
        role = new SubscribedRole(pRole);
        isValid = ValidatorUtility.checkProtocolRole(role);
        assertFalse("Null protocolrole is considered valid by validator.", isValid);

    }

    private void testCheckHeader() {

		/*-------------- Positive cases --------------*/
        Header mHeader = new Header(sphereId, sender, "12", "test");
        isValid = ValidatorUtility.checkHeader(mHeader);
        assertTrue("Valid header is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        mHeader = new Header(null, sender, "12", "test");
        isValid = ValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);

        mHeader = new Header(sphereId, sender, "12", null);
        isValid = ValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);

        mHeader = new Header(sphereId, null, "12", "test");
        isValid = ValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);


    }

    private void testCheckDiscoveryRequest() {

		/*-------------- Positive cases --------------*/
        DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, null, null, 2, 90000, 3);
        isValid = ValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertTrue("Valid discoveryRequest is considered invalid by validator.", isValid);


		/*-------------- Negative cases --------------*/
        discoveryRequest = new DiscoveryRequest(null, sender, null, null, 2, 60000, 3);
        isValid = ValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertFalse("Invalid discoveryRequest is considered valid by validator.", isValid);

        BezirkZirkEndPoint sep = new BezirkZirkEndPoint(zirkId);
        discoveryRequest = new DiscoveryRequest(sphereId, sep, null, null, 2, 60000, 3);
        isValid = ValidatorUtility.checkDiscoveryRequest(discoveryRequest);
        assertFalse("Invalid discoveryRequest is considered valid by validator.", isValid);

    }

    class MockProtocolRole extends ProtocolRole {

        String protocolName = this.getClass().getSimpleName();

        @Override
        public String getRoleName() {
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
