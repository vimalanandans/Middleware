package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SubscriptionUnsubscriptionTest {
    private final static Logger logger = LoggerFactory.getLogger(SubscriptionUnsubscriptionTest.class);

    private static final BezirkZirkId dummyServiceId = new BezirkZirkId("InvalidServiceForTest");
    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static final MockProtocols mockProtocol = new MockProtocols();
    private static final ProtocolRole streamlessPRole = mockProtocol.new StreamlessProtocol();
    private static final ProtocolRole eventlessPRole = mockProtocol.new EventlessProtocol();
    private static final ProtocolRole dummyPRole = mockProtocol.new DummyProtocol();
    private static final SubscribedRole subscribedStreamlessPRole = new SubscribedRole(
            streamlessPRole);
    private static final SubscribedRole subscribedEventlessPRole = new SubscribedRole(eventlessPRole);
    private static final SubscribedRole subscribedDummyPRole = new SubscribedRole(dummyPRole);
    private static BezirkSadlManager bezirkSadlManager = null;
    private static BezirkZirkId bezirkZirkAId = new BezirkZirkId("ServiceA");
    private static BezirkZirkId bezirkZirkBId = new BezirkZirkId("ServiceB");
    private static BezirkZirkId bezirkZirkCId = new BezirkZirkId("ServiceC");
    private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up SubscriptionUnsubscriptionTest TestCase *****");
        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;
        setUpMockServices();

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down SubscriptionUnsubscriptionTest Testcase *****");
        mockUtility.destroyTestSetUp();
    }

    private static void setUpMockServices() {
        bezirkSadlManager.registerService(bezirkZirkAId);
        bezirkSadlManager.setLocation(bezirkZirkAId, reception);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedStreamlessPRole);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedEventlessPRole);
        bezirkSadlManager.subscribeService(bezirkZirkAId, subscribedDummyPRole);
        bezirkSadlManager.registerService(bezirkZirkBId);
        bezirkSadlManager.subscribeService(bezirkZirkBId, subscribedStreamlessPRole);
        bezirkSadlManager.registerService(bezirkZirkCId);
        bezirkSadlManager.setLocation(bezirkZirkCId, reception);
        bezirkSadlManager.subscribeService(bezirkZirkCId, subscribedEventlessPRole);
    }

    @Test
    public void testSubscriptionUnsubscription() {

        logger.info("***** Testing SubscibeService *****");
        testSubscribeService();
        logger.info("***** Tested SubscibeService successfully*****");

        logger.info("***** Testing UnSubscibeService *****");
        testUnsubscribe();
        logger.info("***** Tested Unsubscribeservice successfully *****");

    }

    private void testSubscribeService() {

        boolean isSubscribed = true;
        /*
		 * SadlManager should return false when asked to subscribe zirk with null serviceID.
		 * */
        isSubscribed = bezirkSadlManager.subscribeService(null, subscribedEventlessPRole);
        assertFalse("SadlManager allowed subscribe for null serviceID", isSubscribed);

        isSubscribed = true;
		/*
		 * SadlManager should return false when asked to subscribe to null protcolrole.
		 * */
        isSubscribed = bezirkSadlManager.subscribeService(bezirkZirkAId, null);
        assertFalse("SadlManager allowed subscribe for null protocolRole", isSubscribed);

        isSubscribed = true;
		/*
		 * SadlManager should return false when subscription is invoked with unregistered serviceID.
		 * */
        isSubscribed = bezirkSadlManager.subscribeService(dummyServiceId, subscribedEventlessPRole);
        assertFalse(isSubscribed);

		/*
		 * SadlManager should return serviceA id when queried for services subscribed to streamlessProtocol.
		 * ServiceCId should not be present in this list.
		 * */
        Set<BezirkZirkId> serviceIdSet = bezirkSadlManager.sadlRegistry.protocolMap
                .get(streamlessPRole.getRoleName());

        assertNotNull("ServiceIdSet is null", serviceIdSet);
        assertTrue("ServiceA  is not present in protocolMap", serviceIdSet.contains(bezirkZirkAId));
        assertFalse("ServiceC Id present", serviceIdSet.contains(bezirkZirkCId));

		/*
		 * SadlManager protocolDescMap should have the description for StreamlessProtocol as serviceA
		 * had subscribed to it.
		 * */
        String protocolDesc = bezirkSadlManager.sadlRegistry.protocolDescMap.get(streamlessPRole.getRoleName());
        assertEquals("StreamlessProtocol description not present in protocolDescMap", streamlessPRole.getDescription(), protocolDesc);

		/*
		 * ServiceA id should be present in the eventMap for streamlessProtocol. ServiceC should not be present in this list.
		 * */
        for (String topic : streamlessPRole.getEventTopics()) {

            serviceIdSet = bezirkSadlManager.sadlRegistry.eventMap.get(topic);
            assertTrue("ServiceA is not found in eventMap", serviceIdSet.contains(bezirkZirkAId));
            assertFalse("ServiceC Id is present ine eventMap for StreamlessProtocol events", serviceIdSet.contains(bezirkZirkCId));

        }

		/*
		 * ServiceC id should be present in the eventMap for eventlessProtocol.
		 * ServiceB should not be present in this list.
		 * */
        for (String topic : eventlessPRole.getStreamTopics()) {

            serviceIdSet = bezirkSadlManager.sadlRegistry.streamMap.get(topic);
            assertTrue("ServiceC is not found in eventMap", serviceIdSet.contains(bezirkZirkCId));
            assertNotEquals("ServiceB Id is present ine eventMap for MockProtcolRole events", true, serviceIdSet.contains(bezirkZirkBId));

        }
		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/


		/*SadlManager should return false when not able to persist unsubscribe data to registry.*/
		/*
		mockUtility.clearSadlPersistence();
		isSubscribed= bezirkSadlManager.subscribeService(bezirkZirkBId, subscribedEventlessPRole);
		assertFalse(isSubscribed);
		mockUtility.restoreSadlPersistence();
		bezirkSadlManager= mockUtility.bezirkSadlManager;
*/

		/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/

    }

    private void testUnsubscribe() {
        boolean isUnscubscribed = true;
		/*
		 * SadlManager should return false when asked to unsubscribe null serviceID.
		 * */
        isUnscubscribed = bezirkSadlManager.unsubscribe(null, subscribedEventlessPRole);
        assertFalse(isUnscubscribed);

        isUnscubscribed = true;
		/*
		 * SadlManager should return false when protocolrole is null in unsubscription request
		 * */
        isUnscubscribed = bezirkSadlManager.unsubscribe(bezirkZirkAId, null);
        assertFalse(isUnscubscribed);
		

		/*
		 * SadlManager should return true when asked to unsubscribe ServiceA from streamless ProtocolRole
		 * */
        isUnscubscribed = bezirkSadlManager.unsubscribe(bezirkZirkAId, subscribedStreamlessPRole);
        assertTrue(isUnscubscribed);
		
		/*
		 * ServiceB should be present in the eventlist for streamless protocol. 
		 * ServiceA and ServiceC should not be present in the eventlist for streamless protocol.
		 * */
        Set<BezirkZirkId> serviceIdSet;
        for (String topic : streamlessPRole.getEventTopics()) {

            serviceIdSet = bezirkSadlManager.sadlRegistry.eventMap.get(topic);
            assertTrue("ServiceB not found in the eventMap for streamlessProtocol event(" + topic + ").", serviceIdSet.contains(bezirkZirkBId));
            assertFalse("ServiceA is found in eventMap for StreamlessProtocol event(" + topic + ") even after unsubscription.", serviceIdSet.contains(bezirkZirkAId));
            assertFalse("ServiceC is present ine eventMap for StreamlessProtocol event(" + topic + ").", serviceIdSet.contains(bezirkZirkCId));

        }
		
		/*
		 * SadlManager should remove only those events/streams related to the unsubscribed protocolRole.
		 * ServiceB should not be found in id list for dummyPRole events/streams.
		 * ServiceA and ServiceC should be present in id list for dummyPRole events/streams.
		 * */
        for (String topic : dummyPRole.getEventTopics()) {

            serviceIdSet = bezirkSadlManager.sadlRegistry.eventMap.get(topic);
            //assertFalse("ServiceB  found in the eventMap for dummyPRole event("+topic+").",serviceIdSet.contains(bezirkZirkBId));
            //assertTrue("ServiceA is not found in eventMap for dummyPRole event("+topic+") even after unsubscription.",serviceIdSet.contains(bezirkZirkAId));
            assertFalse("ServiceC is present ine eventMap for dummyPRole event(" + topic + ").", serviceIdSet.contains(bezirkZirkCId));

        }

        for (String topic : eventlessPRole.getStreamTopics()) {

            serviceIdSet = bezirkSadlManager.sadlRegistry.streamMap.get(topic);
            assertTrue("ServiceA is not found in streamMap for EventlessProtocol stream(" + topic + ").", serviceIdSet.contains(bezirkZirkAId));
            assertTrue("ServiceC is not found in streamMap for EventlessProtocol stream(" + topic + ").", serviceIdSet.contains(bezirkZirkCId));
            assertFalse("ServiceB is present in streamMap for EventlessProtocol stream(" + topic + ").", serviceIdSet.contains(bezirkZirkBId));

        }
		
		/*
		 * SadlManager should remove the protocol entry when no services are subscribed to it.
		 * */
        bezirkSadlManager.unsubscribe(bezirkZirkBId, subscribedStreamlessPRole);
        assertNull("Services still present in protocolmap for streamless protocol", bezirkSadlManager.sadlRegistry.protocolMap.get(streamlessPRole.getRoleName()));
		
		
		/*
		 * SadlManager should return false when duplicate unsubscribe request received.
		 * */
        boolean falseUnsubscribe = bezirkSadlManager.unsubscribe(bezirkZirkAId, subscribedStreamlessPRole);
        assertFalse("SadlManager returned true for duplicate unsubscribe request.", falseUnsubscribe);
		
		/*
		 * SadlManager should return false when invalid serviceID requested to unsubscribe
		 * */
        BezirkZirkId invalidId = new BezirkZirkId("Invalid");
        assertFalse("SadlManager allowed unregistered serviceID to unsubscribe.", bezirkSadlManager.unsubscribe(invalidId, subscribedEventlessPRole));
        assertFalse("SadlManager returned true for invalid unsubscribe request.", bezirkSadlManager.unsubscribe(bezirkZirkBId, subscribedEventlessPRole));

        boolean isUnsubscribed = bezirkSadlManager.unsubscribe(bezirkZirkAId, subscribedEventlessPRole);
        assertTrue("SadlManager couldn't unsubscribe valid serviceA.", isUnsubscribed);

/* ------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/		
		
	/*	SadlManager should return false when not able to persist unsubscribe data to registry.*/
			
/*		mockUtility.clearSadlPersistence();
		isUnsubscribed= bezirkSadlManager.unregisterZirk(bezirkZirkBId);
		assertFalse(isUnsubscribed);
		mockUtility.restoreSadlPersistence();
		bezirkSadlManager= mockUtility.bezirkSadlManager;
	*/	
		
/*------ TO BE UNCOMMENTED ONCE FIX IS DONE : COMMENTED TO AVOID BUILD FAILURE -------------*/
    }

}
