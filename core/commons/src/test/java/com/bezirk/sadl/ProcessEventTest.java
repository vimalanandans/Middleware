package com.bezirk.sadl;

import com.bezirk.commons.BezirkCompManager;
import com.bezirk.control.messages.EventLedger;
import com.bezirk.control.messages.MulticastHeader;
import com.bezirk.control.messages.UnicastHeader;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.BezirkSphereType;
import com.bezirk.sphere.impl.MemberZirk;
import com.bezirk.sphere.impl.OwnerSphere;
import com.bezirk.sphere.impl.Zirk;
import com.bezirk.sphere.impl.Sphere;
import com.bezirk.sphere.impl.BezirkSphere;
import com.bezirk.pipe.MockCallBackZirk;
import com.bezirk.pipe.MockBezirkZirk;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This Testcase consists of 3 tests to verifies the processing of events in following scenarios
 * a) 	MulticastEvent processing when no zirk in sphere
 * b)	MulticastEvent processing with valid zirk in sphere.
 * c)	NonLocal multicast event with sphereId null.
 * d) 	NonLocal multicast event with valid sphereId.
 * e)	UnicastEvent processing with valid zirk in sphere.
 * f) 	NonLocal unicast event processing with sphereId null.
 * g) 	NonLocal unicast event processing with valid sphereId.
 *
 * @author AJC6KOR
 */
public class ProcessEventTest {
    private static final Logger logger = LoggerFactory.getLogger(ProcessEventTest.class);

    private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
    private static ZirkId bezirkZirkAId = new ZirkId("ServiceA");
    private static ZirkId bezirkZirkBId = new ZirkId("ServiceB");
    private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");
    private static BezirkSadlManager bezirkSadlManager = null;
    private static SadlRegistry sadlRegistry = null;
    private static SphereRegistry sphereRegistry = null;
    private boolean isEventProcessed = false;


    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("############# Setting up ProcessEventTest TestCase ################");


    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("############ Shutting down ProcessEventTest Testcase ############");

    }

    @Before
    public void setUp() throws Exception {

        mockUtility.setUPTestEnv();
        bezirkSadlManager = mockUtility.bezirkSadlManager;
        sadlRegistry = mockUtility.sadlPersistence.loadSadlRegistry();
        sphereRegistry = mockUtility.spherePersistence.loadSphereRegistry();

    }

    @After
    public void tearDown() throws Exception {
        mockUtility.destroyTestSetUp();

    }

    @Test
    public void testProcessMulticastEvent() throws Exception {

		/*MulticastEvent with no services in sphere. SadlManager should return false.*/
        EventLedger eventLedger = new EventLedger();
        eventLedger.setIsMulticast(true);
        isEventProcessed = false;
        MulticastHeader header = new MulticastHeader();
        String msgTypeOrLabel = "SimpleMessage";
        header.setTopic(msgTypeOrLabel);
        eventLedger.setHeader(header);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertFalse("SadlManager returned true for processEvent with MulticastMessage having zirk out of the sphere.", isEventProcessed);

		/*MulticastEvent with zirk existing in sphere. SadlManager processEvent should return true. */
        Location loc = new Location(null);
        RecipientSelector recipientSelector = new RecipientSelector(loc);
        HashSet<ZirkId> serviceSet = new HashSet<>();
        serviceSet.add(bezirkZirkAId);
        sadlRegistry.sid.add(bezirkZirkAId);
        sadlRegistry.registerService(bezirkZirkAId);
        sadlRegistry.eventMap.put("SimpleMessage", serviceSet);
        String sphereId = "Home";
        HashSet<String> sphereSet = new HashSet<>();
        sphereSet.add(sphereId);
        String deviceId = mockUtility.upaDevice.getDeviceId();
        Sphere sphere = new OwnerSphere(sphereId, deviceId, BezirkSphereType.BEZIRK_SPHERE_TYPE_HOME);
        sphereRegistry.spheres.put(sphereId, sphere);
        Zirk zirk = new MemberZirk("ServiceA", deviceId, sphereSet);
        sphereRegistry.sphereMembership.put(bezirkZirkAId.getZirkId(), zirk);
        sadlRegistry.locationMap.put(bezirkZirkAId, loc);


        BezirkSphere bezirkSphere = new BezirkSphere(mockUtility.cryptoEngine, mockUtility.upaDevice, sphereRegistry);
        BezirkCompManager.setSphereForSadl(bezirkSphere);
        bezirkSphere.initSphere(mockUtility.spherePersistence, mockUtility.bezirkComms, null, mockUtility.sphereConfig);
        MockBezirkZirk mockBezirkZirk = new MockBezirkZirk();
        MockCallBackZirk bezirkCallback = new MockCallBackZirk(mockBezirkZirk);
        BezirkCompManager.setplatformSpecificCallback(bezirkCallback);

        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(bezirkZirkAId);
        header.setSenderSEP(senderSEP);
        header.setSphereName(sphereId);
        header.setRecipientSelector(recipientSelector);
        eventLedger.setHeader(header);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertTrue("SadlManager returned false for processEvent with MulticastMessage having zirk within the sphere.", isEventProcessed);

		/* Non Local multicast message with sphereId null. SadlManager processEvent should return false. */
        isEventProcessed = false;
        header.setSphereName(null);
        eventLedger.setHeader(header);
        eventLedger.setIsLocal(false);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertFalse("SadlManager returned true for processEvent with MulticastMessage when sphereId is null.", isEventProcessed);

		/* Non Local multicast message with valid sphereId and message . SadlManager processEvent should return true. */
        header.setSphereName(sphereId);
        mockUtility.cryptoEngine.generateKeys(sphereId);
        eventLedger.setHeader(header);
        String serializedContent = "Test Message";
        byte[] encryptedMessage = mockUtility.cryptoEngine.encryptSphereContent(sphereId, serializedContent);
        eventLedger.setEncryptedMessage(encryptedMessage);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertTrue("SadlManager returned false for processEvent with MulticastMessage when sphereId is valid.", isEventProcessed);

		/*---------- NEED TO FIND WAY TO START LOGGER SERVICE TO TEST THIS----------*/
		
		/* Send Log Message */ 
		/*isEventProcessed =false;
		LoggingStatus.setLoggingEnabled(true);
		ArrayList<String> loggingSphereList = new ArrayList<>();
		loggingSphereList.add(Util.ANY_SPHERE);
		FilterLogMessages.setLoggingSphereList(loggingSphereList);
		ArrayList<String> sphereList = new ArrayList<String>();
		sphereList.add(sphereId);
		isEventProcessed =bezirkSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for processEvent with MulticastLogMessage when sphereId is valid and logging is enabled.",isEventProcessed);*/
		
		
		/*---------- NEED TO FIND WAY TO START LOGGER SERVICE TO TEST THIS----------*/
    }

    @Test
    public void testProcessUnicastEvent() {

		/*UnicastEvent with zirk existing in sphere. SadlManager processEvent should return true. */
        EventLedger eventLedger = new EventLedger();
        isEventProcessed = true;
        eventLedger.setIsMulticast(false);
        eventLedger.setIsLocal(true);
        BezirkZirkEndPoint recepient = new BezirkZirkEndPoint(bezirkZirkBId);
        HashSet<ZirkId> serviceSet = new HashSet<>();
        serviceSet.add(bezirkZirkAId);
        serviceSet.add(bezirkZirkBId);
        sadlRegistry.sid.add(bezirkZirkAId);
        sadlRegistry.registerService(bezirkZirkAId);
        sadlRegistry.sid.add(bezirkZirkBId);
        sadlRegistry.registerService(bezirkZirkBId);
        sadlRegistry.eventMap.put("SimpleMessage", serviceSet);
        sadlRegistry.locationMap.put(bezirkZirkAId, reception);

        BezirkSphere bezirkSphere = new BezirkSphere(mockUtility.cryptoEngine, mockUtility.upaDevice, sphereRegistry);
        bezirkSphere.initSphere(mockUtility.spherePersistence, mockUtility.bezirkComms, null, mockUtility.sphereConfig);
        BezirkCompManager.setSphereForSadl(bezirkSphere);
        MockBezirkZirk mockBezirkZirk = new MockBezirkZirk();
        MockCallBackZirk bezirkCallback = new MockCallBackZirk(mockBezirkZirk);
        BezirkCompManager.setplatformSpecificCallback(bezirkCallback);

        BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(bezirkZirkAId);
        UnicastHeader header = new UnicastHeader();
        header.setRecipient(recepient);
        header.setSenderSEP(senderSEP);
        String msgTypeOrLabel = "SimpleMessage";
        header.setTopic(msgTypeOrLabel);
        eventLedger.setHeader(header);

        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertTrue("SadlManager returned false for sendEvent with local Message", isEventProcessed);

		/* Non Local UnicastEvent message with sphereId null. SadlManager processEvent should return false. */
        isEventProcessed = false;
        eventLedger.setIsLocal(false);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertFalse("SadlManager returned true for processEvent with UnicastMessage when sphereId is null.", isEventProcessed);

		/* Non Local UnicastEvent message with valid sphereId and message . SadlManager processEvent should return true. */
        String sphereId = "Home";
        header.setSphereName(sphereId);
        mockUtility.cryptoEngine.generateKeys(sphereId);
        eventLedger.setHeader(header);
        String serializedContent = "Test Message";
        byte[] encryptedMessage = mockUtility.cryptoEngine.encryptSphereContent(sphereId, serializedContent);
        eventLedger.setEncryptedMessage(encryptedMessage);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertTrue("SadlManager returned false for processEvent with UnicastMessage when sphereId is valid.", isEventProcessed);

		/* Non Local UnicastEvent message with invalid recipient serviceID. SadlManager processEvent should return false. */
        header.setRecipient(new BezirkZirkEndPoint(new ZirkId(null)));
        eventLedger.setHeader(header);
        isEventProcessed = bezirkSadlManager.processEvent(eventLedger);
        assertFalse("SadlManager returned true for processEvent with UnicastMessage when recipient zirk id is invalid.", isEventProcessed);

    }


}
