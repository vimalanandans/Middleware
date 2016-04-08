package com.bosch.upa.uhu.control.messages;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the ControlLedger POJO by retrieving the field values using getters.
 * 
 * @author AJC6KOR
 *
 */
public class EventLedgerTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(EventLedgerTest.class);
	

	private static final byte[] checksum =  "DummyCheck".getBytes();
	private static final byte[] dataOnWire = "DummyData".getBytes();
	private static final byte[] encryptedMessage = "EncryptedTestMessage".getBytes();
	private static final long lastSent = 10 ;
	private static final Integer numOfSends = 1;
	private static final Boolean retransmit =true;
	private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	private static final ControlMessage message = new ControlMessage(sender , sphereId, discriminator, retransmit);
	private static final String serializedMessage =message.serialize();
	private static final  Header header = new Header(sphereId, sender, "TESTID", "Message");
	private static final  String serializedHeader = header.serialize();
	private static final  Boolean isLocal =true;
	private static final  Boolean isMulticast =true;
	private static final  byte[] encryptedHeader=header.toString().getBytes();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up EventLedgerTest TestCase *****");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down EventLedgerTest TestCase *****");
	}

	/**
	 * Test method for {@link com.bosch.upa.uhu.control.messages.EventLedger#EventLedger()}.
	 */
	@Test
	public void testEventLedger() {

		EventLedger eventLedger = prepareEventLedger();
		
		assertArrayEquals("CheckSum not equal to the set value.",checksum, eventLedger.getChecksum());
		assertArrayEquals("DataOnWire not equal to the set value.",dataOnWire, eventLedger.getDataOnWire());
		assertArrayEquals("EncryptedHeader not equal to the set value.",encryptedHeader, eventLedger.getEncryptedHeader());
		assertArrayEquals("EncryptedMessage not equal to the set value.",encryptedMessage, eventLedger.getEncryptedMessage());
		assertEquals("Header not equal to the set value.",header, eventLedger.getHeader());
		assertEquals("IsLocal not equal to the set value.",isLocal, eventLedger.getIsLocal());
		assertEquals("IsMulticast not equal to the set value.",isMulticast, eventLedger.getIsMulticast());
		assertEquals("LastSent not equal to the set value.",lastSent, eventLedger.getLastSent());
		assertEquals("NumOfSends not equal to the set value.",numOfSends, eventLedger.getNumOfSends());
		assertEquals("SerializedHeader not equal to the set value.",serializedHeader, eventLedger.getSerializedHeader());
		assertEquals("SerializedMessage not equal to the set value.",serializedMessage, eventLedger.getSerializedMessage());
		
	
	
	}

	private EventLedger prepareEventLedger() {
		EventLedger eventLedger = new EventLedger();
		eventLedger.setChecksum(checksum);
		eventLedger.setDataOnWire(dataOnWire);
		eventLedger.setEncryptedHeader(encryptedHeader);
		eventLedger.setEncryptedMessage(encryptedMessage);
		eventLedger.setLastSent(lastSent);
		eventLedger.setNumOfSends(numOfSends);
		eventLedger.setHeader(header);
		eventLedger.setIsLocal(isLocal);
		eventLedger.setSerializedMessage(serializedMessage);
		eventLedger.setSerializedHeader(serializedHeader);
		eventLedger.setIsMulticast(isMulticast);
		return eventLedger;
	
		
	
	
	}

	}
