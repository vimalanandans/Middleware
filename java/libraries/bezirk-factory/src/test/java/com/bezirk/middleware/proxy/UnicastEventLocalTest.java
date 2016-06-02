package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author vbd4kor
 * This test case is used to test the Unicast Event communication locally!
 * 2 Services - MockServiceA register for ProtocolB , MockServiceB register for ProtocolA.
 * MockService-A - discovers the zirk based on ProtocolB. MockServiceA sends the Unicast event to the discovered zirk.
 * The MockServiceB receives the event and responds with a unicast reply. The MockServiceA receives the reply and validates!
 */
public class UnicastEventLocalTest {
    private static final Logger logger = LoggerFactory.getLogger(UnicastEventLocalTest.class);
    private boolean isTestPassed = false;

    private UnicastMockServiceA mockA = new UnicastMockServiceA();
    private UnicastMockServiceB mockB = new UnicastMockServiceB();

    @BeforeClass
    public static void setup() {
        logger.info(" ************** Setting up UnicastEventLocallyTest Testcase ****************************");
    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** Shutting down UnicastEventLocallyTest Testcase ****************************");
    }

    @Before
    public void setUpMockservices() {
        mockB.setupMockService();
        mockA.setupMockService();
    }

    //@Test(timeout=60000)
    public void testLocalEventCommunication() {

        mockA.discoverMockService();

        while (!isTestPassed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info(" ************** Testcase Successful ****************************");
    }

    @After
    public void destroyMockservices() {

        Bezirk bezirk = com.bezirk.middleware.proxy.Factory.registerZirk("MOCK_ZIRK");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * The zirk discovers the MockServiceB and communicate unicastly.
     */
    private final class UnicastMockServiceA implements BezirkListener {
        private final String zirkName = "UnicastMockZirkA";
        private Bezirk bezirk = null;
        private MockServiceBProtocolRole pRole;

        /**
         * Setup the Zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            pRole = new MockServiceBProtocolRole();
            bezirk.subscribe(pRole, this);
        }

        /**
         * Discover the zirk
         */
        private final void discoverMockService() {
            MockServiceAProtocolRole pRole = new MockServiceAProtocolRole();
            bezirk.discover(null, pRole, 10000, 1, this);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
            assertEquals("MockReplyEvent", topic);
            MockReplyEvent reply = Event.fromJson(event, MockReplyEvent.class);
            assertNotNull(reply);
            logger.info("**** REPLY FROM MOCK SERVICE **** " + reply.answer);
            assertNotNull(reply.answer);
            assertEquals("I am Fine! Thank you", reply.answer);
            isTestPassed = true;
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, File file, ZirkEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }

        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
        }

        @Override
        public void discovered(Set<DiscoveredZirk> zirkSet) {
            logger.info("Received Discovery Response");
            if (zirkSet == null) {
                fail("Zirk Set of Discovered Services in Null");
                return;
            }
            if (zirkSet.isEmpty()) {
                fail("Zirk Set is Empty");
                return;
            }

            assertEquals(1, zirkSet.size());
            BezirkDiscoveredZirk dService = null;

            Iterator<DiscoveredZirk> iterator = zirkSet.iterator();
            dService = (BezirkDiscoveredZirk) iterator.next();
            logger.info("DiscoveredServiceName : " + dService.name + "\n" +
                    "Discovered Role : " + dService.protocolRole + "\n" +
                    "Discovered SEP" + dService.zirk + "\n");

            MockRequestEvent request = new MockRequestEvent(Message.Flag.REQUEST, "MockRequestEvent");
            bezirk.sendEvent(dService.zirk, request);
        }


        @Override
        public void pipeGranted(Pipe pipe, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }
    }

    /**
     * Protocol Role that is subscribed by MockServiceB
     */
    private final class MockServiceAProtocolRole extends ProtocolRole {

        private final String[] events = {"MockRequestEvent"};

        @Override
        public String getRoleName() {
            return MockServiceAProtocolRole.class.getSimpleName();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return events;
        }

        @Override
        public String[] getStreamTopics() {
            return null;
        }

    }

    /**
     * Protocol Role that is subscribed by MockServiceA
     */
    private final class MockServiceBProtocolRole extends ProtocolRole {

        private final String[] events = {"MockReplyEvent"};

        @Override
        public String getRoleName() {
            return MockServiceBProtocolRole.class.getSimpleName();
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public String[] getEventTopics() {
            return events;
        }

        @Override
        public String[] getStreamTopics() {
            return null;
        }

    }

    /**
     * Event that is used for communication
     */
    private final class MockRequestEvent extends Event {
        private final String question = "Who am I?";

        public MockRequestEvent(Flag flag, String topic) {
            super(flag, topic);
        }
    }

    /**
     * Event that is used for communication
     */
    private final class MockReplyEvent extends Event {
        private String answer = "";

        public MockReplyEvent(Flag flag, String topic) {
            super(flag, topic);
        }

    }

    /**
     * The zirk discovers the MockServiceA and communicate unicastly.
     */
    private final class UnicastMockServiceB implements BezirkListener {
        private final String zirkName = "UnicastMockZirkB";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            bezirk.subscribe(new MockServiceAProtocolRole(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
            logger.info(" **** Received Event *****");
            assertEquals("MockRequestEvent", topic);
            MockRequestEvent receivedEvent = Event.fromJson(event, MockRequestEvent.class);
            assertEquals("Who am I?", receivedEvent.question);
            // send the reply
            MockReplyEvent replyEvent = new MockReplyEvent(Message.Flag.REPLY, "MockReplyEvent");
            replyEvent.answer = "I am Fine! Thank you";
            bezirk.sendEvent(sender, replyEvent);
            logger.info("********* MOCK_SERVICE B responded to the Event **************");
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, File file, ZirkEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }


        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
        }

        @Override
        public void discovered(Set<DiscoveredZirk> zirkSet) {
        }

        @Override
        public void pipeGranted(Pipe pipe, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }

    }
}
