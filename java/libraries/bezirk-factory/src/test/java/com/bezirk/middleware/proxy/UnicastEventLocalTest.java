package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.middleware.messages.StreamDescriptor;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

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

    @Before
    public void setUpMockservices() {
        mockB.setupMockService();
        mockA.setupMockService();
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

        @Override
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
            assertEquals("MockReplyEvent", topic);
            MockReplyEvent reply = (MockReplyEvent) event;
            assertNotNull(reply);
            logger.info("**** REPLY FROM MOCK SERVICE **** " + reply.answer);
            assertNotNull(reply.answer);
            assertEquals("I am Fine! Thank you", reply.answer);
            isTestPassed = true;
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
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
        public void receiveEvent(String topic, Event event, ZirkEndPoint sender) {
            logger.info(" **** Received Event *****");
            assertEquals("MockRequestEvent", topic);
            MockRequestEvent receivedEvent = (MockRequestEvent) event;
            assertEquals("Who am I?", receivedEvent.question);
            // send the reply
            MockReplyEvent replyEvent = new MockReplyEvent(Message.Flag.REPLY, "MockReplyEvent");
            replyEvent.answer = "I am Fine! Thank you";
            bezirk.sendEvent(sender, replyEvent);
            logger.info("********* MOCK_SERVICE B responded to the Event **************");
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, StreamDescriptor streamDescriptor, short streamId, File file, ZirkEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }

    }
}
