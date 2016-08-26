package com.bezirk.middleware.core.util;

import com.bezirk.middleware.core.control.messages.Header;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        try {
            inetAddr = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        recipient.device = inetAddr.getHostAddress();
        sender.device = inetAddr.getHostAddress();
    }

    @Test
    public void testCheckBezirkServiceEndPoint() {

		/*-------------- Positive cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkEndPoint(recipient);
        assertTrue("Non null serviceEndpoint is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        ZirkId bezirkSid = null;
        BezirkZirkEndPoint bezirkServiceEndPoint = new BezirkZirkEndPoint(bezirkSid);
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        bezirkSid = new ZirkId(null);
        bezirkServiceEndPoint = new BezirkZirkEndPoint(bezirkSid);
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

        bezirkServiceEndPoint = new BezirkZirkEndPoint(zirkId);
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkEndPoint(bezirkServiceEndPoint);
        assertFalse("Null serviceEndpoint is considered valid by validator", isValid);

    }

    @Test
    public void testCheckBezirkServiceId() {

		/*-------------- Positive cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkId(zirkId);
        assertTrue("Non null serviceID is considered invalid by validator", isValid);

		/*-------------- Negative cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkId(null);
        assertFalse("Null serviceID is considered valid by validator", isValid);

        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkBezirkZirkId(new ZirkId(null));
        assertFalse("Null serviceID is considered valid by validator", isValid);
    }

    @Test
    public void testCheckStreamRequest() {

		/*-------------- Positive cases --------------*/
		/*-------------- Negative cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkStreamRequest(null);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);


        // FIXME: 8/1/2016  Punith: UnComment and fix the test case for changes to StreamRequest constructor.
        /*StreamRequest request = new StreamRequest(sender, recipient, sphereId,
                null, null, null, "testFile", true);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, sphereId,
                null, null, "testString", null, true);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(sender, recipient, null,
                null, null, "testString", "testFile", true);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        request = new StreamRequest(null, recipient, sphereId,
                null, null, "testString", "testFile", true);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);

        BezirkZirkEndPoint recepient = new BezirkZirkEndPoint(new ZirkId("test"));
        recepient.device = "";
        request = new StreamRequest(sender, recepient, sphereId,
                null, null, "testString", "testFile", true);
        isValid = ValidatorUtility.checkStreamRequest(request);
        assertFalse("Invalid streamRequest is considered valid by validator.", isValid);*/

    }

    @Test
    public void testCheckRTCStreamRequest() {

		/*-------------- Positive cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkRTCStreamRequest(zirkId, recipient);
        assertTrue("Valid RTCStreamRequest is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkRTCStreamRequest(null, recipient);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkRTCStreamRequest(zirkId, null);
        assertFalse("Invalid RTCStreamRequest is considered valid by validator.", isValid);

    }

    @Test
    public void testCheckHeader() {

		/*-------------- Positive cases --------------*/
        Header mHeader = new Header(sphereId, sender, "12","TestEventName");
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkHeader(mHeader);
        assertTrue("Valid header is considered invalid by validator.", isValid);

		/*-------------- Negative cases --------------*/
        mHeader = new Header(null, sender, "12","TestEventName");
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);

        mHeader = new Header(sphereId, null, "12","TestEventName");
        isValid = com.bezirk.middleware.core.util.ValidatorUtility.checkHeader(mHeader);
        assertFalse("Invalid header is considered valid by validator.", isValid);


    }
}
