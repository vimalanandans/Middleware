package com.bezirk.middleware.proxy;

import com.bezirk.middleware.Bezirk;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.ZirkEndPoint;
import com.bezirk.middleware.messages.Event;
import com.bezirk.middleware.messages.EventSet;
import com.bezirk.middleware.messages.Message.Flag;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author vbd4kor
 * This class tests the local MulticastEvent communication. Three MockServices register and subscribe with common MessageRole.
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

        Bezirk bezirk = com.bezirk.middleware.proxy.Factory.registerZirk("XXX");
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
        bezirk.unregisterZirk();
    }

    /**
     * MockServiceA that is simulating as Zirk that initiates the Multicast Communication
     */
    private final class MulticastMockServiceA {
        private final String zirkName = "MulticastMockZirkA";
        private Bezirk bezirk = null;
        private MulticastMockMessageSet eventSet;

        /**
         * Setup the Zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            eventSet = new MulticastMockMessageSet();
            bezirk.subscribe(eventSet);
        }

        /**
         * Send Multi cast request with null location on the wire
         */
        private final void pingServiceC() {
            MulticastMockRequestEvent req = new MulticastMockRequestEvent(Flag.REQUEST, "MockRequestEvent");
            RecipientSelector recipientSelector = new RecipientSelector(loc);
            bezirk.sendEvent(recipientSelector, req);
        }

        /**
         * Send Multi cast request with specific location on the wire
         */
        private final void pingServices() {
            MulticastMockRequestEvent req = new MulticastMockRequestEvent(Flag.REQUEST, "MockRequestEvent");
            RecipientSelector recipientSelector = null;
            bezirk.sendEvent(recipientSelector, req);
        }
    }

    private final class MulticastMockMessageSet extends EventSet {
        public MulticastMockMessageSet() {
            super(MulticastMockRequestEvent.class);
        }
    }

    /**
     * Sample Event used by the services subscribing for a messageSet
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
    private final class MulticastMockServiceB {
        private final String zirkName = "MulticastMockServiceB";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = com.bezirk.middleware.proxy.Factory.registerZirk(zirkName);
            MulticastMockMessageSet events = new MulticastMockMessageSet();

            events.setEventReceiver(new EventSet.EventReceiver() {
                @Override
                public void receiveEvent(Event event, ZirkEndPoint sender) {
                    logger.info(" **** Received Event *****");

                    assertEquals("MockRequestEvent", event.topic);
                    MulticastMockRequestEvent receivedEvent = (MulticastMockRequestEvent) event;
                    assertEquals("Ping to Mock Services", receivedEvent.question);
                    didMockBreceive = true;
                    logger.info("********* MOCK_SERVICE B received the Event successfully **************");
                }
            });

            bezirk.subscribe(events);
        }
    }

    /**
     * MockServiceC the consumer of the event generated by MockServiecA
     */
    private final class MulticastMockServiceC {
        private final String zirkName = "MulticastMockServiceC";
        private Bezirk bezirk = null;

        /**
         * Setup the zirk
         */
        private final void setupMockService() {
            bezirk = Factory.registerZirk(zirkName);

            MulticastMockMessageSet events = new MulticastMockMessageSet();

            events.setEventReceiver(new EventSet.EventReceiver() {
                @Override
                public void receiveEvent(Event event, ZirkEndPoint sender) {
                    logger.info(" **** Received Event *****");

                    assertEquals("MockRequestEvent", event.topic);
                    MulticastMockRequestEvent receivedEvent = (MulticastMockRequestEvent) event;
                    assertEquals("Ping to Mock Services", receivedEvent.question);
                    if (!didMockCreceive) {
                        didMockCreceive = true;
                    } else {
                        didMockCReceiveSpecifically = true;
                    }

                    logger.info("********* MOCK_SERVICE C received the Event successfully **************");
                }
            });

            bezirk.subscribe(events);
        }

        /**
         * Update the location of the zirk
         */
        private final void changeLocation() {
            bezirk.setLocation(loc);
        }
    }
}
