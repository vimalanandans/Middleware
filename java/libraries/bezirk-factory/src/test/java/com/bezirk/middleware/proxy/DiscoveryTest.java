package com.bezirk.middleware.proxy;


import com.bezirk.commons.BezirkCompManager;
import com.bezirk.devices.BezirkDeviceForPC;
import com.bezirk.devices.BezirkDeviceInterface;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.DiscoveredZirk;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.ZirkId;

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
 * This test case is used to test for discovery!
 * Three MockServices A,B,C are registered and Subscribed for Protocol Role.
 * Sub-test - 1 :
 * Mock Zirk A discover based on the protocol Role specifying the location as null, and it should discover 3 services (MockServiceA, MockServiceB, MockServiceC).
 * Sub-test - 2 :
 * Zirk C updates it location to new location. Mock Zirk A discover based on the protocol Role specifying the location to new Location, and it should discover 1 services (MockServiceC).
 * The success of both sub-test validates this testcase.
 *
 * @author vbd4kor
 */
public class DiscoveryTest {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryTest.class);
    private static boolean isTestWithNullLocPassed = false;
    private static boolean isTestWithLocPassed = false;
    private final Location loc = new Location("Liz Home", "floor-6", "Garage");  // change in the location
    private String serviceBId = null, serviceCId = null;
    private DiscoveryMockServiceA mockA = new DiscoveryMockServiceA();
    private DiscoveryMockServiceB mockB = new DiscoveryMockServiceB();
    private DiscoveryMockServiceC mockC = new DiscoveryMockServiceC();

    @BeforeClass
    public static void setup() {
        logger.info(" ****************** Setting up DiscoveryTest Testcase *******************");

    }

    @AfterClass
    public static void tearDown() {
        logger.info(" ************** Shutting down DiscoveryTest Testcase ****************************");
    }

    @Before
    public void setUpMockservices() {

        mockB.setupMockService();
        mockC.setupMockService();
        mockA.setupMockService();

    }

    //@Test
    public void testForDiscovery() {

        mockA.testDiscoverWithNullLocation();
        while (!isTestWithNullLocPassed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mockC.changeLocation();
        mockA.testDiscoverWithSpecificLocation();

        while (!isTestWithLocPassed) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        logger.info(" ************** TEST SUCCESSFUL ************************");
    }

    @After
    public void destroyMockservices() {

        Bezirk bezirk = com.bezirk.middleware.proxy.Factory.registerZirk("XXX");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * MockServiceA that is simulating as Zirk that initiates the Discovery
     */
    private final class DiscoveryMockServiceA implements BezirkListener {
        private final String zirkName = "DiscoveryMockServiceA";
        private Bezirk bezirk = null;
        private ZirkId myId = null;
        private DiscoveryMockServiceProtocol pRole;

        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            pRole = new DiscoveryMockServiceProtocol();
            bezirk.subscribe(pRole, this);
        }

        private final void testDiscoverWithNullLocation() {
            bezirk.discover(null, pRole, 10000, 1, this);
        }

        private final void testDiscoverWithSpecificLocation() {
            RecipientSelector recipientSelector = new RecipientSelector(loc);
            bezirk.discover(recipientSelector, pRole, 10000, 1, this);
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
            logger.info("Received Discovery Response");
            if (zirkSet == null) {
                fail("Zirk Set of Discovered Services in Null");
                return;
            }
            if (zirkSet.isEmpty()) {
                fail("Zirk Set is Empty");
                return;
            }
            logger.debug("*******Size of the Set********* : " + zirkSet.size());

            if (isTestWithNullLocPassed == false && zirkSet.size() == 3) {

                for (DiscoveredZirk aZirkSet : zirkSet) {
                    BezirkDeviceForPC bezirkDeviceForPC = new BezirkDeviceForPC();
//                    Properties props;
//
//                    try {
//                        props = BezirkDeviceForPC.loadProperties();
//                        String location = props.getProperty("DeviceLocation");
//                        bezirkDeviceForPC.setDeviceLocation(new Location(location));
//                        BezirkCompManager.setUpaDevice(bezirkDeviceForPC);
//                    } catch (Exception e) {
//
//                        fail("Exception in setting device location. " + e.getMessage());
//
//                    }

                    String location = "Floor1/null/null";
                    bezirkDeviceForPC.setDeviceLocation(new Location(location));
                    BezirkCompManager.setUpaDevice(bezirkDeviceForPC);

                    BezirkDeviceInterface bezirkDevice = BezirkCompManager.getUpaDevice();
                    BezirkDiscoveredZirk tempDisService = (BezirkDiscoveredZirk) aZirkSet;
                    assertNotNull("Discovered Zirk is null. ", tempDisService);
                    switch (tempDisService.name) {

                        case "DiscoveryMockServiceA":
                            assertEquals("DiscoveryMockServiceA", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.protocolRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceA.", tempDisService.zirk.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceA.", myId.getZirkId(), tempDisService.zirk.zirkId.getZirkId());
                            break;
                        case "DiscoveryMockServiceB":
                            assertEquals("DiscoveryMockServiceB", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.protocolRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceB.", tempDisService.zirk.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceB.", serviceBId, tempDisService.zirk.zirkId.getZirkId());
                            break;
                        case "DiscoveryMockServiceC":
                            assertEquals("DiscoveryMockServiceC", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.protocolRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceC.", tempDisService.zirk.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceC.", serviceCId, tempDisService.zirk.zirkId.getZirkId());
                            break;
                    }
                }
                logger.info("**** DISCOVERY SUB-TEST WITH NULL LOCATION PASSES SUCCESSFULLY ****");
                isTestWithNullLocPassed = true;
                return;
            }
            if (isTestWithNullLocPassed == true && isTestWithLocPassed == false && zirkSet.size() == 1) {
                logger.info("Discovery subtest with location passed");


                Iterator<DiscoveredZirk> iterator = zirkSet.iterator();

                BezirkDiscoveredZirk tempDisService = (BezirkDiscoveredZirk) iterator.next();
                assertNotNull(tempDisService);
                assertEquals("DiscoveryMockServiceC", tempDisService.name);
                assertEquals("DiscoveryMockServiceProtocol", tempDisService.protocolRole);
                assertEquals(loc.toString(), tempDisService.location.toString());
                assertNotNull(tempDisService.zirk.device);
                assertEquals(serviceCId, tempDisService.zirk.zirkId.getZirkId());

                isTestWithLocPassed = true;
                logger.info("**** DISCOVERY SUB-TEST WITH SPECIFIC LOCATION PASSES SUCCESSFULLY ****");
            }
        }

        @Override
        public void pipeGranted(Pipe pipe, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }


    }

    /**
     * ProtocolRole used by the mock Services
     */
    private final class DiscoveryMockServiceProtocol extends ProtocolRole {

        private final String[] events = {"MockRequestEvent"};
        private final String[] streams = {"MockRequestStream"};

        @Override
        public String getRoleName() {
            return DiscoveryMockServiceProtocol.class.getSimpleName();
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
            return streams;
        }

    }

    /**
     * MockServiceB stimulating the responder of the discovery request initiated by MockServiceA
     */
    private final class DiscoveryMockServiceB implements BezirkListener {
        private final String serviceName = "DiscoveryMockServiceB";
        private Bezirk bezirk = null;

        public DiscoveryMockServiceB() {
        }

        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(serviceName);
            bezirk.subscribe(new DiscoveryMockServiceProtocol(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId,
                                  InputStream inputStream, ZirkEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId,
                                  File file, ZirkEndPoint sender) {
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
     * MockServiceC stimulating the responder of the discovery request initiated by MockServiceA
     */
    private final class DiscoveryMockServiceC implements BezirkListener {
        private final String serviceName = "DiscoveryMockServiceC";
        private Bezirk bezirk = null;

        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(serviceName);

            bezirk.subscribe(new DiscoveryMockServiceProtocol(), this);
        }

        private final void changeLocation() {
            bezirk.setLocation(loc);
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
}
