package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the UnicastHeader POJO by retrieving the field values.
 *
 * @author AJC6KOR
 */
public class UnicastHeaderTest {

    private static final Logger log = LoggerFactory
            .getLogger(UnicastHeaderTest.class);

    private static final UhuZirkId serviceId = new UhuZirkId("ServiceA");
    private static final UhuZirkEndPoint senderSEP = new UhuZirkEndPoint(
            serviceId);
    private static final String sphereName = "TestSphere";
    private static final UhuZirkId serviceBId = new UhuZirkId("ServiceB");
    private static final UhuZirkEndPoint recipient = new UhuZirkEndPoint(serviceBId);
    private static final String messageId = GenerateMsgId.generateEvtId(senderSEP);
    private static final String topic = "Message";

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up UnicastHeaderTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down UnicastHeaderTest TestCase *****");
    }

    @Test
    public void testUnicastHeader() {

        com.bezirk.control.messages.UnicastHeader unicastHeader = prepareUnicastHeader();

        assertEquals("Recipient not equal to the set value.", recipient,
                unicastHeader.getRecipient());
        assertEquals("Sender not equal to the set value.", senderSEP,
                unicastHeader.getSenderSEP());
        assertEquals("SphereName not equal to the set value.",
                sphereName, unicastHeader.getSphereName());
        assertEquals("Topic not equal to the set value.",
                topic, unicastHeader.getTopic());
        assertEquals("MessageID not equal to the set value.", messageId,
                unicastHeader.getUniqueMsgId());

    }

    private com.bezirk.control.messages.UnicastHeader prepareUnicastHeader() {
        com.bezirk.control.messages.UnicastHeader unicastHeader = new com.bezirk.control.messages.UnicastHeader();
        unicastHeader.setRecipient(recipient);
        unicastHeader.setSenderSEP(senderSEP);
        unicastHeader.setSphereName(sphereName);
        unicastHeader.setTopic(topic);
        unicastHeader.setUniqueMsgId(messageId);
        return unicastHeader;
    }
}
