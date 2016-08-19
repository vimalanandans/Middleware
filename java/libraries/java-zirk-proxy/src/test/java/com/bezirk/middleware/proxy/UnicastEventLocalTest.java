package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        mockA.setupMockZirk();
    }

    @After
    public void destroyMockservices() {

        Bezirk bezirk = BezirkMiddleware.registerZirk("MOCK_ZIRK");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * The zirk discovers the MockServiceB and communicate unicastly.
     */
    private final class UnicastMockServiceA {
        private final String zirkName = "UnicastMockZirkA";
        private Bezirk bezirk = null;
        private MockZirkBMessageSet messageSet;

        /**
         * Setup the Zirk
         */
        private final void setupMockZirk() {
            bezirk = BezirkMiddleware.registerZirk(zirkName);
            messageSet = new MockZirkBMessageSet();

            messageSet.setEventReceiver(new EventSet.EventReceiver() {
                @Override
                public void receiveEvent(Event event, ZirkEndPoint sender) {
                    MockReplyEvent reply = (MockReplyEvent) event;
                    assertNotNull(reply);
                    logger.info("**** REPLY FROM MOCK SERVICE **** " + reply.answer);
                    assertNotNull(reply.answer);
                    assertEquals("I am Fine! Thank you", reply.answer);
                    isTestPassed = true;
                }
            });

            bezirk.subscribe(messageSet);
        }
    }

    /**
     * Protocol Role that is subscribed by MockServiceB
     */
    private final class MockZirkAMessageSet extends EventSet {
        public MockZirkAMessageSet() {
            super(MockRequestEvent.class);
        }
    }

    /**
     * Protocol Role that is subscribed by MockServiceA
     */
    private final class MockZirkBMessageSet extends EventSet {
        public MockZirkBMessageSet() {
            super(MockReplyEvent.class);
        }
    }

    /**
     * Event that is used for communication
     */
    private final class MockRequestEvent extends Event {
        private final String question = "Who am I?";

        public MockRequestEvent() {

        }
    }

    /**
     * Event that is used for communication
     */
    private final class MockReplyEvent extends Event {
        private String answer = "";

        public MockReplyEvent() {

        }

    }

    /**
     * The zirk discovers the MockServiceA and communicate unicastly.
     */
    private final class UnicastMockServiceB {
        private final String zirkName = "UnicastMockZirkB";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = BezirkMiddleware.registerZirk(zirkName);
            MockZirkAMessageSet events = new MockZirkAMessageSet();

            events.setEventReceiver(new EventSet.EventReceiver() {
                @Override
                public void receiveEvent(Event event, ZirkEndPoint sender) {
                    logger.info(" **** Received Event *****");
                    MockRequestEvent receivedEvent = (MockRequestEvent) event;
                    assertEquals("Who am I?", receivedEvent.question);
                    // send the reply
                    MockReplyEvent replyEvent = new MockReplyEvent();
                    replyEvent.answer = "I am Fine! Thank you";
                    bezirk.sendEvent(sender, replyEvent);
                    logger.info("********* MOCK_SERVICE B responded to the Event **************");
                }
            });

            bezirk.subscribe(events);
        }
    }
}
