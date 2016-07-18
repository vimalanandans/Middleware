package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.middleware.messages.UnicastStream;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.util.MockComms;
import com.bezirk.util.MockProtocolsForBezirkPC;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;
import com.bezirk.proxy.ProxyServer;

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
 * This testcase verifies the methods in send apis in ProxyServiceLegacy class.
 *
 * @author AJC6KOR
 */
public class ProxySendTest {

    private static final MockSetUpUtilityForBezirkPC mockSetUP = new MockSetUpUtilityForBezirkPC();
    private static final Logger logger = LoggerFactory.getLogger(ProxySendTest.class);
    private static PubSubBroker sadlManager;
    private final String serviceName = "MockServiceA";
    private final String serviceAId = "MockServiceAId";
    private final ZirkId senderId = new ZirkId(serviceAId);
    private final String serviceBId = "MockServiceBId";
    private final ZirkId receiverId = new ZirkId(serviceBId);
    private final BezirkZirkEndPoint receiver = new BezirkZirkEndPoint(receiverId);
    private File sendFile;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("********** Setting up ProxySendTest Testcase **********");

        System.setProperty("InterfaceName", mockSetUP.getInterface().getName());
        mockSetUP.setUPTestEnv();

        try {
            sadlManager = mockSetUP.getPubSubBroker();
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
