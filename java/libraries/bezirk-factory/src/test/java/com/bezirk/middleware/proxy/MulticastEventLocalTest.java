package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.addressing.ZirkId;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.Message.Flag;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author vbd4kor
 * This class tests the local MulticastEvent communication. Three MockServices register and subscribe with common ProtocolRole.
 * Sub-test- 1 :MockServiceA sends the multicastEvent on the wire. MockServiceB and MockServiceC should receive the Events.
 * Sub-test- 2 : MockServiceC changes to New Location. MockServiceA pings a multicast event by setting the address to new location
 * Only MockServiceC should receive the event.
 */
public class MulticastEventLocalTest {
    private static final Logger logger = LoggerFactory.getLogger(MulticastEventLocalTest.class);
    private static boolean didMockBreceive = false, didMockCreceive = false, didMockCReceiveSpecifically = false;
    private final Location loc = new Location("Liz Home", "floor-6", "Garage");  // change in the location
    private MulticastMockServiceA mockA = new MulticastMockServiceA();
    private MulticastMockServiceB mockB = new MulticastMockServiceB();
    private MulticastMockServiceC mockC = new MulticastMockServiceC();

    @BeforeClass
    public static void setup() {
        logger.info(" ************** Setting up MulticastEventLocalTest Testcase ****************************");
    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** Shutting down MulticastEventLocalTest Testcase ****************************");
    }

    @Before
    public void setUpMockservices() {

        mockB.setupMockService();
        mockC.setupMockService();
        mockA.setupMockService();

    }

    // FIXME: This test sporadically fails, presumably after a timeout
    //@Test(timeout=60000)
    public void testForDiscovery() {
        mockA.pingServices();

        while ((didMockBreceive != false) && (didMockCreceive != false)) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info(" **************  MulticastEventLocalTesting for NULL Location is Successful ****************************");
        // Change the location of Zirk C.
        mockC.changeLocation();
        mockA.pingServiceC();

        while (!didMockCReceiveSpecifically) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info(" **************  MulticastEventLocalTesting Successful ****************************");
    }

    @After
    public void destroyMockservices() {

        Bezirk bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
        bezirk.unregisterZirk(mockA.myId);
        bezirk.unregisterZirk(mockB.myId);
        bezirk.unregisterZirk(mockC.myId);
    }

    /**
     * MockServiceA that is simulating as Zirk that initiates the Multicast Communication
     */
    private final class MulticastMockServiceA implements BezirkListener {
        private final String serviceName = "MulticastMockServiceA";
        private Bezirk bezirk = null;
        private ZirkId myId = null;
        private MulticastMockServiceProtocolRole pRole;

        /**
         * Setup the Zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = bezirk.registerZirk(serviceName);
            logger.info("MulticastMockServiceA - regId : " + ((BezirkZirkId) myId).getBezirkZirkId());
            pRole = new MulticastMockServiceProtocolRole();
            bezirk.subscribe(myId, pRole, this);
        }

        /**
         * Send Multi cast request with null location on the wire
         */
        private final void pingServiceC() {
            MulticastMockRequestEvent req = new MulticastMockRequestEvent(Flag.REQUEST, "MockRequestEvent");
            RecipientSelector recipientSelector = new RecipientSelector(loc);
            bezirk.sendEvent(myId, recipientSelector, req);
        }

        /**
         * Send Multi cast request with specific location on the wire
         */
        private final void pingServices() {
            MulticastMockRequestEvent req = new MulticastMockRequestEvent(Flag.REQUEST, "MockRequestEvent");
            RecipientSelector recipientSelector = null;
            bezirk.sendEvent(myId, recipientSelector, req);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
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

    /**
     * ProtocolRole used by the mock Services
     */
    private final class MulticastMockServiceProtocolRole extends ProtocolRole {

        private final String[] events = {"MockRequestEvent"};

        @Override
        public String getRoleName() {
            return MulticastMockServiceProtocolRole.class.getSimpleName();
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
     * Sample Event used by the services subscribing for a protocolRole
     */
    private final class MulticastMockRequestEvent extends Event {

        private final String question = "Ping to Mock Services";

        private MulticastMockRequestEvent(Flag flag, String topic) {
            super(flag, topic);
        }
    }

    /**
     * MockServiceB the consumer of the event generated by MockServiecA
     */
    private final class MulticastMockServiceB implements BezirkListener {
        private final String serviceName = "MulticastMockServiceB";
        private Bezirk bezirk = null;
        private ZirkId myId = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = bezirk.registerZirk(serviceName);
            logger.info("MulticastMockServiceB - regId : " + ((BezirkZirkId) myId).getBezirkZirkId());
            bezirk.subscribe(myId, new MulticastEventLocalTest.MulticastMockServiceProtocolRole(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
            logger.info(" **** Received Event *****");

            assertEquals("MockRequestEvent", topic);
            MulticastMockRequestEvent receivedEvent = Event.fromJson(event, MulticastMockRequestEvent.class);
            assertEquals("Ping to Mock Services", receivedEvent.question);
            didMockBreceive = true;
            logger.info("********* MOCK_SERVICE B received the Event successfully **************");
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

    /**
     * MockServiceC the consumer of the event generated by MockServiecA
     */
    private final class MulticastMockServiceC implements BezirkListener {
        private final String serviceName = "MulticastMockServiceC";
        private Bezirk bezirk = null;
        private ZirkId myId = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = bezirk.registerZirk(serviceName);
            logger.info("MulticastMockServiceC - regId : " + ((BezirkZirkId) myId).getBezirkZirkId());
            bezirk.subscribe(myId, new MulticastMockServiceProtocolRole(), this);
        }

        /**
         * Update the location of the zirk
         */
        private final void changeLocation() {
            bezirk.setLocation(myId, loc);
        }

        @Override
        public void receiveEvent(String topic, String event,
                                 ZirkEndPoint sender) {
            logger.info(" **** Received Event *****");

            assertEquals("MockRequestEvent", topic);
            MulticastMockRequestEvent receivedEvent = Event.fromJson(event, MulticastMockRequestEvent.class);
            assertEquals("Ping to Mock Services", receivedEvent.question);
            if (!didMockCreceive) {
                didMockCreceive = true;
            } else {
                didMockCReceiveSpecifically = true;
            }

            logger.info("********* MOCK_SERVICE C received the Event successfully **************");

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
