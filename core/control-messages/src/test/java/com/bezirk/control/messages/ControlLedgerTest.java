package com.bezirk.control.messages;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

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
public class ControlLedgerTest {

    private static final Logger log = LoggerFactory
            .getLogger(ControlLedgerTest.class);


    private static final byte[] checksum = "DummyCheck".getBytes();
    private static final byte[] dataOnWire = "DummyData".getBytes();
    private static final byte[] encryptedMessage = "EncryptedTestMessage".getBytes();
    private static final Boolean isMessageFromHost = true;
    private static final long lastSent = 10;
    private static final Integer numOfSends = 1;
    private static final Boolean retransmit = true;
    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
    private static final String sphereId = "TestSphere";
    private static final UhuZirkId serviceId = new UhuZirkId("ServiceA");
    private static final UhuZirkEndPoint sender = new UhuZirkEndPoint(serviceId);
    private static final com.bezirk.control.messages.ControlMessage message = new ControlMessage(sender, sphereId, discriminator, retransmit);
    private static final byte[] sendData = "DataToBeSent".getBytes();
    private static final String serializedMessage = message.serialize();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up ControlLedgerTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down ControlLedgerTest TestCase *****");
    }


    @Test
    public void testControlLedger() {

        com.bezirk.control.messages.ControlLedger ctrlLedger = prepareControlLedger();

        assertArrayEquals("CheckSum not equal to the set value.", checksum, ctrlLedger.getChecksum());
        assertArrayEquals("DataOnWire not equal to the set value.", dataOnWire, ctrlLedger.getDataOnWire());
        assertArrayEquals("EncryptedMessage not equal to the set value.", encryptedMessage, ctrlLedger.getEncryptedMessage());
        assertEquals("IsMessageFromHost not equal to the set value.", isMessageFromHost, ctrlLedger.getIsMessageFromHost());
        assertEquals("LastSent not equal to the set value.", lastSent, ctrlLedger.getLastSent());
        assertEquals("NumOfSends not equal to the set value.", numOfSends, ctrlLedger.getNumOfSends());
        assertEquals("Message not equal to the set value.", message, ctrlLedger.getMessage());
        assertArrayEquals("SendData not equal to the set value.", sendData, ctrlLedger.getSendData());
        assertEquals("SerializedMessage not equal to the set value.", serializedMessage, ctrlLedger.getSerializedMessage());
        assertEquals("SphereID not equal to the set value.", sphereId, ctrlLedger.getSphereId());


    }

    private com.bezirk.control.messages.ControlLedger prepareControlLedger() {
        com.bezirk.control.messages.ControlLedger ctrlLedger = new com.bezirk.control.messages.ControlLedger();
        ctrlLedger.setChecksum(checksum);
        ctrlLedger.setDataOnWire(dataOnWire);
        ctrlLedger.setEncryptedMessage(encryptedMessage);
        ctrlLedger.setIsMessageFromHost(isMessageFromHost);
        ctrlLedger.setLastSent(lastSent);
        ctrlLedger.setNumOfSends(numOfSends);
        ctrlLedger.setMessage(message);
        ctrlLedger.setSendData(sendData);
        ctrlLedger.setSerializedMessage(serializedMessage);
        ctrlLedger.setSphereId(sphereId);
        return ctrlLedger;
    }
}
