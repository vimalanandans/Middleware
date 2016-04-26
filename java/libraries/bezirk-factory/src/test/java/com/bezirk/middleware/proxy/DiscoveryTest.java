package com.bezirk.middleware.proxy;


import com.bezirk.commons.UhuCompManager;
import com.bezirk.devices.UPADeviceForPC;
import com.bezirk.devices.UPADeviceInterface;
import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.DiscoveredService;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;
import com.bezirk.middleware.addressing.PipePolicy;
import com.bezirk.middleware.addressing.ServiceEndPoint;
import com.bezirk.middleware.addressing.ServiceId;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * This test case is used to test for discovery!
 * Three MockServices A,B,C are registered and Subscribed for Protocol Role.
 * Sub-test - 1 :
 * Mock Service A discover based on the protocol Role specifying the location as null, and it should discover 3 services (MockServiceA, MockServiceB, MockServiceC).
 * Sub-test - 2 :
 * Service C updates it location to new location. Mock Service A discover based on the protocol Role specifying the location to new Location, and it should discover 1 services (MockServiceC).
 * The success of both sub-test validates this testcase.
 * More info can be found on Wiki - <Page>
 *
 * @author vbd4kor
 * @Date - 23/09/2014
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

        Bezirk uhu = com.bezirk.middleware.proxy.Factory.getInstance();
        uhu.unregisterService(mockA.myId);
        uhu.unregisterService(mockB.myId);
        uhu.unregisterService(mockC.myId);
    }

    /**
     * MockServiceA that is simulating as Service that initiates the Discovery
     */
    private final class DiscoveryMockServiceA implements BezirkListener {
        private final String serviceName = "DiscoveryMockServiceA";
        private Bezirk uhu = null;
        private ServiceId myId = null;
        private DiscoveryMockServiceProtocol pRole;

        private final void setupMockService() {
            uhu = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = uhu.registerService(serviceName);
            logger.info("DiscoveryMockServiceA - regId : " + ((UhuServiceId) myId).getUhuServiceId());
            pRole = new DiscoveryMockServiceProtocol();
            uhu.subscribe(myId, pRole, this);
        }

        private final void testDiscoverWithNullLocation() {
            uhu.discover(myId, null, pRole, 10000, 1, this);
        }

        private final void testDiscoverWithSpecificLocation() {
            Address address = new Address(loc);
            uhu.discover(myId, address, pRole, 10000, 1, this);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream inputStream, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, File file, ServiceEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }


        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
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
            logger.debug("*******Size of the Set********* : " + serviceSet.size());

            if (isTestWithNullLocPassed == false && serviceSet.size() == 3) {

                Iterator<DiscoveredService> iterator = serviceSet.iterator();
                while (iterator.hasNext()) {
                    //Himadri: Accepting Rishab's Changes on top of Vijet's

                    UPADeviceForPC upaDeviceForPC = new UPADeviceForPC();
                    Properties props;
                    try {
                        props = UPADeviceForPC.loadProperties();
                        String location = props.getProperty("DeviceLocation");
                        upaDeviceForPC.setDeviceLocation(new Location(location));
                        UhuCompManager.setUpaDevice(upaDeviceForPC);
                    } catch (Exception e) {

                        fail("Exception in setting device location. " + e.getMessage());

                    }
                    UPADeviceInterface upaDevice = UhuCompManager.getUpaDevice();
                    UhuDiscoveredService tempDisService = (UhuDiscoveredService) iterator.next();
                    assertNotNull("Discovered Service is null. ", tempDisService);
                    switch (tempDisService.name) {

                        case "DiscoveryMockServiceA":
                            assertEquals("DiscoveryMockServiceA", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.pRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceA.", tempDisService.service.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceA.", ((UhuServiceId) myId).getUhuServiceId(), tempDisService.service.serviceId.getUhuServiceId());
                            break;
                        case "DiscoveryMockServiceB":
                            assertEquals("DiscoveryMockServiceB", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.pRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceB.", tempDisService.service.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceB.", serviceBId, tempDisService.service.serviceId.getUhuServiceId());
                            break;
                        case "DiscoveryMockServiceC":
                            assertEquals("DiscoveryMockServiceC", tempDisService.name);
                            assertEquals("DiscoveryMockServiceProtocol", tempDisService.pRole);
                            assertNotNull("Device is not set for DiscoveryMockServiceC.", tempDisService.service.device);
                            assertEquals("ServiceID is different for DiscoveryMockServiceC.", serviceCId, tempDisService.service.serviceId.getUhuServiceId());
                            break;
                    }
                }
                logger.info("**** DISCOVERY SUB-TEST WITH NULL LOCATION PASSES SUCCESSFULLY ****");
                isTestWithNullLocPassed = true;
                return;
            }
            if (isTestWithNullLocPassed == true && isTestWithLocPassed == false && serviceSet.size() == 1) {
                logger.info("Discovery subtest with location passed");


                Iterator<DiscoveredService> iterator = serviceSet.iterator();

                UhuDiscoveredService tempDisService = (UhuDiscoveredService) iterator.next();
                assertNotNull(tempDisService);
                assertEquals("DiscoveryMockServiceC", tempDisService.name);
                assertEquals("DiscoveryMockServiceProtocol", tempDisService.pRole);
                assertEquals(loc.toString(), tempDisService.location.toString());
                assertNotNull(tempDisService.service.device);
                assertEquals(serviceCId, tempDisService.service.serviceId.getUhuServiceId());

                isTestWithLocPassed = true;
                logger.info("**** DISCOVERY SUB-TEST WITH SPECIFIC LOCATION PASSES SUCCESSFULLY ****");
                return;
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
        public String getProtocolName() {
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
        private Bezirk uhu = null;
        private ServiceId myId = null;

        public DiscoveryMockServiceB() {
        }

        private final void setupMockService() {
            uhu = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = uhu.registerService(serviceName);
            serviceBId = ((UhuServiceId) myId).getUhuServiceId();
            logger.info("DiscoveryMockServiceB - regId : " + serviceBId);
            uhu.subscribe(myId, new DiscoveryMockServiceProtocol(), this);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId,
                                  InputStream inputStream, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId,
                                  File file, ServiceEndPoint sender) {
        }


        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }


        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
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
        private Bezirk uhu = null;
        private ServiceId myId = null;

        private final void setupMockService() {
            uhu = com.bezirk.middleware.proxy.Factory.getInstance();
            myId = uhu.registerService(serviceName);
            serviceCId = ((UhuServiceId) myId).getUhuServiceId();
            logger.info("DiscoveryMockServiceC - regId : " + serviceCId);

            uhu.subscribe(myId, new DiscoveryMockServiceProtocol(), this);
        }

        private final void changeLocation() {
            uhu.setLocation(myId, loc);
        }

        @Override
        public void receiveEvent(String topic, String event, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, InputStream inputStream, ServiceEndPoint sender) {
        }

        @Override
        public void receiveStream(String topic, String stream, short streamId, File file, ServiceEndPoint sender) {
        }

        @Override
        public void streamStatus(short streamId, StreamStates status) {
        }


        @Override
        public void pipeStatus(Pipe pipe, PipeStates status) {
        }

        @Override
        public void discovered(Set<DiscoveredService> serviceSet) {
        }

        @Override
        public void pipeGranted(Pipe pipe, PipePolicy allowedIn,
                                PipePolicy allowedOut) {

        }


    }
}
