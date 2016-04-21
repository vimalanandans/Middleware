package com.bezirk.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author vbd4kor
 * @Date 24-09-2014
 * This test case is used to test the Unicast Event communication locally!
 * 2 Services - MockServiceA register for ProtocolB , MockServiceB register for ProtocolA.
 * MockService-A - discovers the service based on ProtocolB. MockServiceA sends the Unicast event to the discovered service.
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

        Bezirk uhu = Factory.getInstance();
        uhu.unregisterService(mockA.myId);
        uhu.unregisterService(mockB.myId);
    }

    /**
     * The service discovers the MockServiceB and communicate unicastly.
     */
    private final class UnicastMockServiceA implements BezirkListener {
        private final String serviceName = "UnicastMockServiceA";
        private Bezirk uhu = null;
        private ServiceId myId = null;
        private MockServiceBProtocolRole pRole;

        /**
         * Setup the Service
         */
        private final void setupMockService() {
            uhu = Factory.getInstance();
            myId = uhu.registerService(serviceName);
            logger.info("MOCK_SERVICE_A - regId : " + ((UhuServiceId) myId).getUhuServiceId());
            pRole = new MockServiceBProtocolRole();
            uhu.subscribe(myId, pRole, this);
        }

        /**
         * Discover the service
         */
        private final void discoverMockService() {
            MockServiceAProtocolRole pRole = new MockServiceAProtocolRole();
            uhu.discover(myId, null, pRole, 10000, 1, this);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
            assertEquals("MockReplyEvent", topic);
            MockReplyEvent reply = Event.deserialize(event, MockReplyEvent.class);
            assertNotNull(reply);
            logger.info("**** REPLY FROM MOCK SERVICE **** " + reply.answer);
            assertNotNull(reply.answer);
            assertEquals("I am Fine! Thank you", reply.answer);
            isTestPassed = true;
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamConditions status) {
        }

        @Override
        public void pipeStatus(Pipe p, PipeConditions status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
            logger.info("Received Discovery Response");
            if (serviceSet == null) {
                fail("Service Set of Discovered Services in Null");
                return;
            }
            if (serviceSet.isEmpty()) {
                fail("Service Set is Empty");
                return;
            }

            assertEquals(1, serviceSet.size());
            UhuDiscoveredService dService = null;

            Iterator<DiscoveredService> iterator = serviceSet.iterator();
            dService = (UhuDiscoveredService) iterator.next();
            logger.info("DiscoveredServiceName : " + dService.name + "\n" +
                    "Discovered Role : " + dService.pRole + "\n" +
                    "Discovered SEP" + dService.service + "\n");

            MockRequestEvent request = new MockRequestEvent(Message.Flag.REQUEST, "MockRequestEvent");
            uhu.sendEvent(myId, dService.service, request);
        }


        @Override
        public void pipeGranted(Pipe p, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }
    }

    /**
     * Protocol Role that is subscribed by MockServiceB
     */
    private final class MockServiceAProtocolRole extends ProtocolRole {

        private final String[] events = {"MockRequestEvent"};

        @Override
        public String getProtocolName() {
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
        public String getProtocolName() {
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
     * The service discovers the MockServiceA and communicate unicastly.
     */
    private final class UnicastMockServiceB implements BezirkListener {
        private final String serviceName = "UnicastMockServiceB";
        private Bezirk uhu = null;
        private ServiceId myId = null;

        /**
         * Setup the service
         */
        private final void setupMockService() {
            uhu = Factory.getInstance();
            myId = uhu.registerService(serviceName);
            logger.info("UnicastMockServiceB - regId : " + ((UhuServiceId) myId).getUhuServiceId());
            uhu.subscribe(myId, new MockServiceAProtocolRole(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
            logger.info(" **** Received Event *****");
            assertEquals("MockRequestEvent", topic);
            MockRequestEvent receivedEvent = Event.deserialize(event, MockRequestEvent.class);
            assertEquals("Who am I?", receivedEvent.question);
            // send the reply
            MockReplyEvent replyEvent = new MockReplyEvent(Message.Flag.REPLY, "MockReplyEvent");
            replyEvent.answer = "I am Fine! Thank you";
            uhu.sendEvent(myId, sender, replyEvent);
            logger.info("********* MOCK_SERVICE B responded to the Event **************");
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream f, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, String filePath, ServiceEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamConditions status) {
        }


        @Override
        public void pipeStatus(Pipe p, PipeConditions status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
        }

        @Override
        public void pipeGranted(Pipe p, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }

    }
}
