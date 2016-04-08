// Commented by Vimal. because these SADL send functionalites are moved to comms
// hence it doesn't makes sense to test as below.

//package com.bosch.upa.uhu.sadl;
//
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.bosch.upa.uhu.control.messages.ControlLedger;
//import com.bosch.upa.uhu.control.messages.ControlMessage;
//import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
//import com.bosch.upa.uhu.control.messages.EventLedger;
//import com.bosch.upa.uhu.control.messages.MulticastControlMessage;
//import com.bosch.upa.uhu.control.messages.UnicastControlMessage;
//
///**
//* This Testcase verifies the behavior of sadlManager send service in the
//* following scenarios.
//*
//*  a) 	Send local UnicastControl Message
//* 	b)  Send remote UnicastControl Message
//*  c)  Send Multicast control Message
//*  d) 	Send local Unicast Event Message
//*  e)	Send remote Unicast Event Message
//*  f)	Send Multicast Event Message
//*
//* @author AJC6KOR
//*
//*/
//public class MessageSenderTest {
//
//	private final static Logger log = LoggerFactory
//			.getLogger(MessageSenderTest.class);
//
//	private static UhuSadlManager uhuSadlManager = null;
//
//	private static final MockSetUpUtility mockUtility = new MockSetUpUtility();
//
//	private static boolean isMessageSend =false;
//
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//
//		log.info("***** Setting up MessageSenderTest TestCase *****");
//		mockUtility.setUPTestEnv();
//		uhuSadlManager = mockUtility.uhuSadlManager;
//	}
//
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//
//		log.info("***** Shutting down MessageSenderTest Testcase *****");
//		mockUtility.destroyTestSetUp();
//	}
//
//
//	@Test
//	public void testMessageSendingService() {
//
//		log.info("***** Testing EventSending *****");
//		testSendEvent();
//		log.info("***** EventSending tested successfully *****");
//
//
//		log.info("***** testing sendControlMessage *****");
//		testSendControlMessage();
//		log.info("***** ControlMessageSending tested successfully *****");
//
//
//	}
//
//
//	/**
//	 *  SadlManager's sendControlMessage api is tested for multicast and unicast control messages of different types.
//	 */
//	private void testSendControlMessage() {
//
//		ControlLedger ctrlLedger = new ControlLedger();
//		testSendMulticastControlMessage(ctrlLedger);
//
//		testSendUnicastControlMessage(ctrlLedger);
//
//
//	}
//
//
//	private void testSendUnicastControlMessage(ControlLedger ctrlLedger) {
//
//		/*Local UnicastMessage with streamRequest discriminator.
//		 * SadlManager should return false as it is a local Message.*/
//		isMessageSend =true;
//		UnicastControlMessage unicastMessage = mock(UnicastControlMessage.class);
//		when(unicastMessage.getIsLocal()).thenReturn(true);
//		when(unicastMessage.getDiscriminator()).thenReturn(Discriminator.StreamRequest);
//		ctrlLedger.setMessage(unicastMessage);
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//
//		assertFalse("SadlManager returned true for local stream send request",isMessageSend);
//
//		/*Local UnicastMessage with DiscoveryRequest discriminator.
//		 * SadlManager should return false as it is a local Message.*/
//		isMessageSend =true;
//		when(unicastMessage.getDiscriminator()).thenReturn(Discriminator.DiscoveryRequest);
//		ctrlLedger.setMessage(unicastMessage);
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//		assertFalse("SadlManager returned true for local discovery send request.",isMessageSend);
//
//
//		/*Remote UnicastMessage with streamRequest discriminator.
//		 * SadlManager should return true as is it a remote Message.*/
//		isMessageSend =false;
//		when(unicastMessage.getIsLocal()).thenReturn(false);
//		when(unicastMessage.getDiscriminator()).thenReturn(Discriminator.StreamRequest);
//		ctrlLedger.setMessage(unicastMessage);
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//		assertTrue("SadlManager returned false for multicast stream send request.",isMessageSend);
//
//		/*Remote UnicastMessage with discoveryRequest discriminator.
//		 * SadlManager should return false as it is a remote Message.*/
//		isMessageSend =false;
//		when(unicastMessage.getDiscriminator()).thenReturn(Discriminator.DiscoveryRequest);
//		ctrlLedger.setMessage(unicastMessage);
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//		assertTrue("SadlManager returned false for multicast discovery send request.",isMessageSend);
//	}
//
//
//	private void testSendMulticastControlMessage(ControlLedger ctrlLedger) {
//
//		/*MulticastControlMessage with StreamRequest discriminator.
//		 * SadlManager should return true as it is a multicast Message.*/
//		ControlMessage multicastMessage = mock(MulticastControlMessage.class);
//		when(multicastMessage.getDiscriminator()).thenReturn(Discriminator.StreamRequest);
//		ctrlLedger.setMessage(multicastMessage);
//		isMessageSend = false;
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//		assertTrue("SadlManager returned false for multicast stream send request.",isMessageSend);
//
//		/*
//		 * Multicastcontrol message with discoveryRequest discriminator.
//		 * SadlManager should return true as it is a multicast message*/
//		isMessageSend =false;
//		when(multicastMessage.getDiscriminator()).thenReturn(Discriminator.DiscoveryRequest);
//		isMessageSend = uhuSadlManager.sendControlMessage(ctrlLedger);
//		assertTrue("SadlManager returned false for multicast discovery send request.",isMessageSend);
//	}
//
//	/**
//	 *  SadlManager's sendEvent api is tested for multicast and unicast events.
//	 */
//	private void testSendEvent() {
//
//		EventLedger eventLedger = new EventLedger();
//
//		testSendMulticastEvent(eventLedger);
//
//		testSendUnicastEvent(eventLedger);
//
//
//
//	}
//
//
//	private void testSendMulticastEvent(EventLedger eventLedger) {
//
//		/*MulticastEvent with NumOfSends =0. SadlManager sendEvent should return true. */
//		eventLedger.setIsMulticast(true);
//		eventLedger.setNumOfSends(0);
//		isMessageSend=false;
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertTrue("SadlManager returned true for sendEvent with MulticastMessage having NumOfSends =0",isMessageSend );
//
//		/* MulticastEvent with NumOfSends =1. SadlManager sendEvent should return true. */
//		isMessageSend=false;
//		eventLedger.setIsLocal(false);
//		eventLedger.setNumOfSends(1);
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertTrue("SadlManager returned fasle for sendEvent with MulticastMessage having NumOfSends =1",isMessageSend);
//	}
//
//
//	private void testSendUnicastEvent(EventLedger eventLedger) {
//
//		/* Local UnicastEvent with NumOfSends =0. SadlManager sendEvent should return false as it is a local Message. */
//		isMessageSend=true;
//		eventLedger.setIsMulticast(false);
//		eventLedger.setIsLocal(true);
//		eventLedger.setNumOfSends(0);
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertFalse("SadlManager returned true for sendEvent with local Message having NumOfSends =0",isMessageSend);
//
//		/*Local UnicastEvent with NumOfSends =1. SadlManager sendEvent should return false as it is a local Message. */
//		isMessageSend=true;
//		eventLedger.setIsMulticast(false);
//		eventLedger.setIsLocal(true);
//		eventLedger.setNumOfSends(1);
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertFalse("SadlManager returned true for sendEvent with local Message having NumOfSends =1",isMessageSend);
//
//		/* Remote UnicastEvent with NumOfSends =0. SadlManager sendEvent should return false as it is a local Message. */
//		isMessageSend=true;
//		eventLedger.setIsMulticast(false);
//		eventLedger.setIsLocal(false);
//		eventLedger.setNumOfSends(0);
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertTrue("SadlManager returned false for sendEvent with remote Message having NumOfSends =0",isMessageSend);
//
//		/*Remote UnicastEvent with NumOfSends =1. SadlManager sendEvent should return false as it is a local Message. */
//		isMessageSend=true;
//		eventLedger.setIsMulticast(false);
//		eventLedger.setIsLocal(false);
//		eventLedger.setNumOfSends(1);
//		isMessageSend = uhuSadlManager.sendEvent(eventLedger);
//		assertTrue("SadlManager returned false for sendEvent with remote Message having NumOfSends =1",isMessageSend);
//	}
//
//
//}
