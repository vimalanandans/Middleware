package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.middleware.messages.UnicastStream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.sadl.BezirkSadlManager;
import com.bezirk.util.MockComms;
import com.bezirk.util.MockProtocolsForBezirkPC;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This testcase verifies the methods in send apis in ProxyForServices class.
 *
 * @author AJC6KOR
 */
public class ProxySendTest {

    private static final MockSetUpUtilityForBezirkPC mockSetUP = new MockSetUpUtilityForBezirkPC();
    private static final Logger logger = LoggerFactory.getLogger(ProxySendTest.class);
    private static BezirkSadlManager sadlManager;
    private final String serviceName = "MockServiceA";
    private final String serviceAId = "MockServiceAId";
    private final BezirkZirkId senderId = new BezirkZirkId(serviceAId);
    private final String serviceBId = "MockServiceBId";
    private final BezirkZirkId receiverId = new BezirkZirkId(serviceBId);
    private final BezirkZirkEndPoint receiver = new BezirkZirkEndPoint(receiverId);
    private final File sendFile =
            new File(ProxyForServices.class.getClassLoader().getResource("streamingTest.txt").getPath());

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("********** Setting up ProxySendTest Testcase **********");

        System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
        mockSetUP.setUPTestEnv();

        try {
            sadlManager = mockSetUP.getBezirkSadlManager();
        } catch (UnknownHostException e) {
            fail("Unable to set up test environment.");
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        logger.info("********** Shutting down ProxySendTest Testcase **********");

        System.clearProperty("InterfaceName");

        mockSetUP.destroyTestSetUp();
    }

    @Test
    public void testSendStream() {
        ProxyForServices proxyForServices = new ProxyForServices();
        proxyForServices.setSadlRegistry(sadlManager);
        MockComms mockComms = (MockComms) mockSetUP.getBezirkComms();
        proxyForServices.setCommsManager(mockComms);

        proxyForServices.registerService(senderId, serviceName);
        String serializedStream = new MockRequestStream(Message.Flag.REQUEST, "MockStream", receiver).toJson();
        receiver.device = "DeviceB";
        short streamId = proxyForServices.sendStream(senderId, receiver, serializedStream, sendFile, (short) 5);
        // checking the stream id is not enough
        assertEquals("Proxy is unable to send stream. ", 1, streamId);

        assertEquals("Proxy is unable to add stream request to the comms queue.", 1, mockComms.getStreamList().size());
        mockComms.clearQueues();
    }

    @Test
    public void testSendUnicastEvent() {
        try {
            ProxyForServices proxyForServices = new ProxyForServices();
            proxyForServices.setSadlRegistry(sadlManager);
            MockComms mockComms = (MockComms) mockSetUP.getBezirkComms();
            proxyForServices.setCommsManager(mockComms);
            proxyForServices.registerService(senderId, serviceName);

            String serializedEventMsg = new MockProtocolsForBezirkPC().new MockEvent1(Flag.REQUEST, "MockEvent").toJson();
            receiver.device = "DeviceB";
            proxyForServices.sendUnicastEvent(senderId, receiver, serializedEventMsg);

            assertEquals("Proxy is unable to add unicast event message to the comms queue.", 1, mockComms.getEventList().size());

            mockComms.clearQueues();
        } catch (Exception e) {
            fail("Proxy is unable to send unicast events.");
        }
    }

    @Test
    public void testSendMulticastEvent() {
        try {
            ProxyForServices proxyForServices = new ProxyForServices();
            proxyForServices.setSadlRegistry(sadlManager);
            MockComms mockComms = (MockComms) mockSetUP.getBezirkComms();
            proxyForServices.setCommsManager(mockComms);
            proxyForServices.registerService(senderId, serviceName);

            String serializedEventMsg = new MockProtocolsForBezirkPC().new MockEvent1(Flag.REQUEST, "MockEvent").toJson();
            Address address = new Address(new Location("FLOOR1/BLOCk1/ROOM1"));
            proxyForServices.sendMulticastEvent(senderId, address, serializedEventMsg);
            assertEquals("Proxy is unable to add multicast event message to the comms queue.", 1, mockComms.getEventList().size());

            proxyForServices.sendMulticastEvent(senderId, null, serializedEventMsg);
            assertEquals("Proxy is unable to add multicast event message to the comms queue.", 2, mockComms.getEventList().size());
            mockComms.clearQueues();
        } catch (Exception e) {
            fail("Proxy is unable to send unicast events.");
        }
    }

    /**
     * Stream Descriptor
     */
    class MockRequestStream extends UnicastStream {
        public String sampleValue = "TestValue";

        public MockRequestStream(Flag flag, String topic,
                                 ZirkEndPoint recipient) {
            super(flag, topic, recipient);
        }
    }
}
