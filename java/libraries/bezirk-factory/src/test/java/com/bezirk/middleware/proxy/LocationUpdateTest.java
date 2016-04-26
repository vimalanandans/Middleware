package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
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
import static org.junit.Assert.fail;

/**
 * @author vbd4kor
 * @Date 24-09-2014
 * This class tests the location Update for the Services.Two Services MockSErviceA, MockServiceB.
 * MockServiceA and MockServiceB subscribes to a protocolRole and a location (L1). MockService A sends a Multicast event and MS-B receives.
 * MS-B updates the location(L2). MS-A sends a multicast event with the same location(L1). MS-B doesnt receive it. MS-A updates the location to L1.
 * MS-B receives the event that validates the testcase.
 */
public class LocationUpdateTest {
    private static final Logger logger = LoggerFactory.getLogger(LocationUpdateTest.class);
    private final Location l1 = new Location("Liz Home", "floor-6", "Garage");  // change in the location
    private final Location l2 = new Location("Bob apartment", "floor-7", "Living-room");  // change in the location
    private final LocationUpdateMockServiceA mockA = new LocationUpdateMockServiceA();
    private final LocationUpdateMockServiceB mockB = new LocationUpdateMockServiceB();
    private boolean isL1passed = false, isL2Passed = false;
    private int countPingServiceB = 0;

    @BeforeClass
    public static void setup() {
        logger.info(" ************** Setting up LocationUpdateTest Testcase ****************************");
    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** Shutting down LocationUpdateTest Testcase ****************************");
    }

    @Before
    public void setUpMockservices() {

        mockB.setupMockService();
        mockA.setupMockService();

    }

    //@Test(timeout = 60000)
    public void testLocationUpdate() {
        mockB.updateLocationToL1();

        mockA.updateLocationToL1();

        mockA.pingServices(l1);

        while (!isL1passed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

                fail("Current thread is interrupted. " + e.getMessage());

            }
        }

        mockB.updateLocationToL2();
        mockA.pingServices(l1);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {

            fail("Current thread is interrupted. " + e.getMessage());
        }

        mockB.updateLocationToL1();
        mockA.pingServices(l1);
        while (!isL2Passed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {

                fail("Current thread is interrupted. " + e.getMessage());

            }
        }

        logger.info("******** TEST CASE FOR LOCATION UPDATE PASSED SUCCESSFULLY **********");
    }

    @After
    public void destroyMockservices() {

        Bezirk uhu = com.bezirk.middleware.proxy.Factory.getInstance();
        uhu.unregisterZirk(mockA.myId);
        uhu.unregisterZirk(mockB.myId);
    }

    /**
     * MockServiceA that is simulating as Zirk that initiates the Multicast Communication
     */
    private final class LocationUpdateMockServiceA implements BezirkListener {
        private final String serviceName = "LocationUpdateMockServiceA";
        private Bezirk uhu = null;
        private ZirkId myId = null;
        private LocationUpdateMockServiceProtocolRole pRole;

        /**
         * Setup the Zirk
         */
        private final void setupMockService() {
            uhu = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = uhu.registerZirk(serviceName);
            logger.info("LocationUpdateMockServiceA - regId : " + ((BezirkZirkId) myId).getBezirkZirkId());
            pRole = new LocationUpdateMockServiceProtocolRole();
            uhu.subscribe(myId, pRole, this);
        }

        /**
         * Send Multi cast request with null location on the wire
         */
        private final void pingServices(Location location) {
            MockRequestEvent req = new MockRequestEvent(Flag.REQUEST, "MockRequestEvent");
            Address address = new Address(location);
            uhu.sendEvent(myId, address, req);
        }

        /**
         * Update the Location to L1
         */
        private final void updateLocationToL1() {
            uhu.setLocation(myId, l1);
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
    private final class LocationUpdateMockServiceProtocolRole extends ProtocolRole {

        private final String[] events = {"MockRequestEvent"};

        @Override
        public String getProtocolName() {
            return LocationUpdateMockServiceProtocolRole.class.getSimpleName();
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
    private final class MockRequestEvent extends Event {

        private final String question = "Ping to Mock Services";

        public MockRequestEvent(Flag flag, String topic) {
            super(flag, topic);
        }
    }

    /**
     * MockServiceB the consumer of the event generated by MockServiecA
     */
    private final class LocationUpdateMockServiceB implements BezirkListener {
        private final String serviceName = "LocationUpdateMockServiceB";
        private Bezirk uhu = null;
        private ZirkId myId = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            uhu = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = uhu.registerZirk(serviceName);
            logger.info("LocationUpdateMockServiceB - regId : " + ((BezirkZirkId) myId).getBezirkZirkId());
            uhu.subscribe(myId, new LocationUpdateMockServiceProtocolRole(), this);
        }

        /**
         * Update the Location to L1
         */
        private final void updateLocationToL1() {
            uhu.setLocation(myId, l1);
        }

        /**
         * Update the location to L2
         */
        private final void updateLocationToL2() {
            uhu.setLocation(myId, l2);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
            logger.info(" **** Received Event *****");

            ++countPingServiceB;

            assertEquals("MockRequestEvent", topic);
            MockRequestEvent receivedEvent = Event.fromJson(event, MockRequestEvent.class);
            assertEquals("Ping to Mock Services", receivedEvent.question);
            if (countPingServiceB == 1) {
                isL1passed = true;
            } else if (countPingServiceB == 2) {
                isL2Passed = true;
            } else {
                fail("ping count is not matching expected value. Current ping count is : " + countPingServiceB);
            }

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
}