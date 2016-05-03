package com.bezirk.control.messages;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This testCase verifies the ControlLedger POJO by retrieving the field values using getters.
 *
 * @author AJC6KOR
 */
public class EventLedgerTest {
    private static final Logger logger = LoggerFactory.getLogger(EventLedgerTest.class);

    private static final byte[] checksum = "DummyCheck".getBytes();
    private static final byte[] dataOnWire = "DummyData".getBytes();
    private static final byte[] encryptedMessage = "EncryptedTestMessage".getBytes();
    private static final long lastSent = 10;
    private static final Integer numOfSends = 1;
    private static final Boolean retransmit = true;
    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private static final com.bezirk.control.messages.ControlMessage message = new com.bezirk.control.messages.ControlMessage(sender, sphereId, discriminator, retransmit);
    private static final String serializedMessage = message.serialize();
    private static final com.bezirk.control.messages.Header header = new Header(sphereId, sender, "TESTID", "Message");
    private static final String serializedHeader = header.serialize();
    private static final Boolean isLocal = true;
    private static final Boolean isMulticast = true;
    private static final byte[] encryptedHeader = header.toString().getBytes();

    @BeforeClass
    public static void setUpBeforeClass() {
        logger.info("***** Setting up EventLedgerTest TestCase *****");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        logger.info("***** Shutting down EventLedgerTest TestCase *****");
    }

    /**
     * Test method for {@link com.bezirk.control.messages.EventLedger#EventLedger()}.
     */
    @Test
    public void testEventLedger() {

        com.bezirk.control.messages.EventLedger eventLedger = prepareEventLedger();

        assertArrayEquals("CheckSum not equal to the set value.", checksum, eventLedger.getChecksum());
        assertArrayEquals("DataOnWire not equal to the set value.", dataOnWire, eventLedger.getDataOnWire());
        assertArrayEquals("EncryptedHeader not equal to the set value.", encryptedHeader, eventLedger.getEncryptedHeader());
        assertArrayEquals("EncryptedMessage not equal to the set value.", encryptedMessage, eventLedger.getEncryptedMessage());
        assertEquals("Header not equal to the set value.", header, eventLedger.getHeader());
        assertEquals("IsLocal not equal to the set value.", isLocal, eventLedger.getIsLocal());
        assertEquals("IsMulticast not equal to the set value.", isMulticast, eventLedger.getIsMulticast());
        assertEquals("LastSent not equal to the set value.", lastSent, eventLedger.getLastSent());
        assertEquals("NumOfSends not equal to the set value.", numOfSends, eventLedger.getNumOfSends());
        assertEquals("SerializedHeader not equal to the set value.", serializedHeader, eventLedger.getSerializedHeader());
        assertEquals("SerializedMessage not equal to the set value.", serializedMessage, eventLedger.getSerializedMessage());


    }

    private com.bezirk.control.messages.EventLedger prepareEventLedger() {
        com.bezirk.control.messages.EventLedger eventLedger = new EventLedger();
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
