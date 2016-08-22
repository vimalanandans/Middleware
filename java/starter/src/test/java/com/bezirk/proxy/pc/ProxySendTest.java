package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.StreamDescriptor;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.pubsubbroker.PubSubBroker;
import com.bezirk.util.MockSetUpUtilityForBezirkPC;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
     * StreamDescriptor Descriptor
     */
    class MockRequestStreamDescriptor extends StreamDescriptor {
        public String sampleValue = "TestValue";

        public MockRequestStreamDescriptor(boolean incremental, boolean encrypted) {
            //Need to change the File argument as required!!, just made it as null as a quick // FIXME: 8/1/2016
            super(incremental, encrypted, null, "Test");
        }
    }
}
