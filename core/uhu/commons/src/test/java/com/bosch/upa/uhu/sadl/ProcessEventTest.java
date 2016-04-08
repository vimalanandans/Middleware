package com.bosch.upa.uhu.sadl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bosch.upa.uhu.commons.UhuCompManager;
import com.bosch.upa.uhu.control.messages.EventLedger;
import com.bosch.upa.uhu.control.messages.MulticastHeader;
import com.bosch.upa.uhu.control.messages.UnicastHeader;
import com.bosch.upa.uhu.persistence.SphereRegistry;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;
import com.bosch.upa.uhu.sphere.impl.MemberService;
import com.bosch.upa.uhu.sphere.impl.OwnerSphere;
import com.bosch.upa.uhu.sphere.impl.Service;
import com.bosch.upa.uhu.sphere.impl.Sphere;
import com.bosch.upa.uhu.sphere.impl.UhuSphere;
import com.bosch.upa.uhu.test.pipe.MockCallBackService;
import com.bosch.upa.uhu.test.pipe.MockUhuService;

/**
 * This Testcase consists of 3 tests to verifies the processing of events in following scenarios
 * 
 *  a) 	MulticastEvent processing when no service in sphere
 *  b)	MulticastEvent processing with valid service in sphere.
 *  c)	NonLocal multicast event with sphereId null.
 *  d) 	NonLocal multicast event with valid sphereId.
 *  e)	UnicastEvent processing with valid service in sphere.
 *  f) 	NonLocal unicast event processing with sphereId null.
 *  g) 	NonLocal unicast event processign with valid sphereId.
 *   
 * @author AJC6KOR
 *
 */
public class ProcessEventTest {

	private static final Logger log = LoggerFactory
			.getLogger(ProcessEventTest.class);

	

	private static UhuServiceId uhuServiceAId = new UhuServiceId("ServiceA");
	private static UhuServiceId uhuServiceBId = new UhuServiceId("ServiceB");;

	private static Location reception = new Location("OFFICE1", "BLOCK1", "RECEPTION");

	private static UhuSadlManager uhuSadlManager=null;
	private static SadlRegistry sadlRegistry = null;
	private static SphereRegistry sphereRegistry = null;

	private static final MockSetUpUtility mockUtility = new MockSetUpUtility();

	private boolean isEventProcessed= false;



	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("############# Setting up ProcessEventTest TestCase ################");

		
	}

	@Before
	public void setUp() throws  Exception {
		
		mockUtility.setUPTestEnv();
		uhuSadlManager = mockUtility.uhuSadlManager;
		sadlRegistry = mockUtility.sadlPersistence.loadSadlRegistry();
		sphereRegistry = mockUtility.spherePersistence.loadSphereRegistry();

	}
	
	@After
	public void tearDown() throws  Exception {
		mockUtility.destroyTestSetUp();

	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("############ Shutting down ProcessEventTest Testcase ############");

	}
	

	@Test
	public void testProcessMulticastEvent() throws Exception {

		/*MulticastEvent with no services in sphere. SadlManager should return false.*/
		EventLedger eventLedger = new EventLedger();
		eventLedger.setIsMulticast(true);
		isEventProcessed=false;
		MulticastHeader header  = new MulticastHeader();
		String msgTypeOrLabel="SimpleMessage";
		header.setTopic(msgTypeOrLabel);
		eventLedger.setHeader(header);
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertFalse("SadlManager returned true for processEvent with MulticastMessage having service out of the sphere.",isEventProcessed );
		
		/*MulticastEvent with service existing in sphere. SadlManager processEvent should return true. */
		Location loc = new Location(null);
		Address address = new Address(loc );
		HashSet<UhuServiceId>  serviceSet = new HashSet<>();
		serviceSet.add(uhuServiceAId);
		sadlRegistry.sid.add(uhuServiceAId);
		sadlRegistry.registerService(uhuServiceAId);
		sadlRegistry.eventMap.put("SimpleMessage", serviceSet);
		String sphereId ="Home";
		HashSet<String> sphereSet = new HashSet<>();
		sphereSet.add(sphereId);
		String deviceId = mockUtility.upaDevice.getDeviceId();
		Sphere sphere = new OwnerSphere(sphereId, deviceId, UhuSphereType.UHU_SPHERE_TYPE_HOME);
		sphereRegistry.spheres.put(sphereId, sphere );
		Service service = new MemberService("ServiceA", deviceId, sphereSet);
		sphereRegistry.sphereMembership.put(uhuServiceAId.getUhuServiceId(), service );
		sadlRegistry.locationMap.put(uhuServiceAId, loc);
		
		
		UhuSphere uhuSphere = new UhuSphere(mockUtility.cryptoEngine, mockUtility.upaDevice, sphereRegistry);
		UhuCompManager.setSphereForSadl(uhuSphere);
		uhuSphere.initSphere(mockUtility.spherePersistence, mockUtility.uhuComms, null, mockUtility.sphereConfig);
		MockUhuService mockUhuservice = new MockUhuService();
		MockCallBackService uhucallback = new MockCallBackService(mockUhuservice );
		UhuCompManager.setplatformSpecificCallback(uhucallback );
		
		UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(uhuServiceAId );
		header.setSenderSEP(senderSEP);
		header.setSphereName(sphereId);
		header.setAddress(address);
		eventLedger.setHeader(header);
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for processEvent with MulticastMessage having service within the sphere.",isEventProcessed );

		/* Non Local multicast message with sphereId null. SadlManager processEvent should return false. */
		isEventProcessed=false;
		header.setSphereName(null);
		eventLedger.setHeader(header);
		eventLedger.setIsLocal(false);
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertFalse("SadlManager returned true for processEvent with MulticastMessage when sphereId is null.",isEventProcessed);

		/* Non Local multicast message with valid sphereId and message . SadlManager processEvent should return true. */
		header.setSphereName(sphereId);
		mockUtility.cryptoEngine.generateKeys(sphereId);
		eventLedger.setHeader(header);
		String serializedContent ="Test Message";
		byte[] encryptedMessage = mockUtility.cryptoEngine.encryptSphereContent(sphereId , serializedContent );
		eventLedger.setEncryptedMessage(encryptedMessage );
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for processEvent with MulticastMessage when sphereId is valid.",isEventProcessed);

		/*---------- NEED TO FIND WAY TO START LOGGER SERVICE TO TEST THIS----------*/
		
		/* Send Log Message */ 
		/*isEventProcessed =false;
		LoggingStatus.setLoggingEnabled(true);
		ArrayList<String> loggingSphereList = new ArrayList<>();
		loggingSphereList.add(Util.ANY_SPHERE);
		FilterLogMessages.setLoggingSphereList(loggingSphereList);
		ArrayList<String> sphereList = new ArrayList<String>();
		sphereList.add(sphereId);
		isEventProcessed =uhuSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for processEvent with MulticastLogMessage when sphereId is valid and logging is enabled.",isEventProcessed);*/
		
		
		/*---------- NEED TO FIND WAY TO START LOGGER SERVICE TO TEST THIS----------*/
	}

	@Test
	public void testProcessUnicastEvent() {

		/*UnicastEvent with service existing in sphere. SadlManager processEvent should return true. */
		EventLedger eventLedger = new EventLedger();
		isEventProcessed=true;
		eventLedger.setIsMulticast(false);
		eventLedger.setIsLocal(true);
		UhuServiceEndPoint recepient = new UhuServiceEndPoint(uhuServiceBId);
		HashSet<UhuServiceId>  serviceSet = new HashSet<>();
		serviceSet.add(uhuServiceAId);
		serviceSet.add(uhuServiceBId);
		sadlRegistry.sid.add(uhuServiceAId);
		sadlRegistry.registerService(uhuServiceAId);
		sadlRegistry.sid.add(uhuServiceBId);
		sadlRegistry.registerService(uhuServiceBId);
		sadlRegistry.eventMap.put("SimpleMessage", serviceSet);
		sadlRegistry.locationMap.put(uhuServiceAId, reception);
		
		UhuSphere uhuSphere = new UhuSphere(mockUtility.cryptoEngine, mockUtility.upaDevice, sphereRegistry);
		uhuSphere.initSphere(mockUtility.spherePersistence, mockUtility.uhuComms, null, mockUtility.sphereConfig);
		UhuCompManager.setSphereForSadl(uhuSphere);
		MockUhuService mockUhuservice = new MockUhuService();
		MockCallBackService uhucallback = new MockCallBackService(mockUhuservice );
		UhuCompManager.setplatformSpecificCallback(uhucallback );
		
		UhuServiceEndPoint senderSEP = new UhuServiceEndPoint(uhuServiceAId );
		UnicastHeader header  = new UnicastHeader();
		header.setRecipient(recepient);
		header.setSenderSEP(senderSEP);
		String msgTypeOrLabel="SimpleMessage";
		header.setTopic(msgTypeOrLabel);
		eventLedger.setHeader(header);
		
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for sendEvent with local Message",isEventProcessed);

		/* Non Local UnicastEvent message with sphereId null. SadlManager processEvent should return false. */
		isEventProcessed=false;
		eventLedger.setIsLocal(false);
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertFalse("SadlManager returned true for processEvent with UnicastMessage when sphereId is null.",isEventProcessed);

		/* Non Local UnicastEvent message with valid sphereId and message . SadlManager processEvent should return true. */
		String sphereId ="Home";
		header.setSphereName(sphereId);
		mockUtility.cryptoEngine.generateKeys(sphereId);
		eventLedger.setHeader(header);
		String serializedContent ="Test Message";
		byte[] encryptedMessage = mockUtility.cryptoEngine.encryptSphereContent(sphereId , serializedContent );
		eventLedger.setEncryptedMessage(encryptedMessage );
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertTrue("SadlManager returned false for processEvent with UnicastMessage when sphereId is valid.",isEventProcessed);

		/* Non Local UnicastEvent message with invalid recipient serviceID. SadlManager processEvent should return false. */
		header.setRecipient(new UhuServiceEndPoint(new UhuServiceId(null)));
		eventLedger.setHeader(header);
		isEventProcessed = uhuSadlManager.processEvent(eventLedger);
		assertFalse("SadlManager returned true for processEvent with UnicastMessage when recipient service id is invalid.",isEventProcessed);
		
	}


}
