package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

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
    private static final Logger logger = LoggerFactory.getLogger(UnicastHeaderTest.class);

    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint senderSEP = new BezirkZirkEndPoint(
            serviceId);
    private static final String sphereName = "TestSphere";
    private static final ZirkId serviceBId = new ZirkId("ServiceB");
    private static final BezirkZirkEndPoint recipient = new BezirkZirkEndPoint(serviceBId);
    private static final String messageId = GenerateMsgId.generateEvtId(senderSEP);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up UnicastHeaderTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down UnicastHeaderTest TestCase *****");
    }

    @Test
    public void testUnicastHeader() {

        com.bezirk.control.messages.UnicastHeader unicastHeader = prepareUnicastHeader();

        assertEquals("Recipient not equal to the set value.", recipient,
                unicastHeader.getRecipient());
        assertEquals("Sender not equal to the set value.", senderSEP,
                unicastHeader.getSender());
        assertEquals("SphereName not equal to the set value.",
                sphereName, unicastHeader.getSphereId());
        assertEquals("MessageID not equal to the set value.", messageId,
                unicastHeader.getUniqueMsgId());

    }

    private com.bezirk.control.messages.UnicastHeader prepareUnicastHeader() {
        com.bezirk.control.messages.UnicastHeader unicastHeader = new com.bezirk.control.messages.UnicastHeader();
        unicastHeader.setRecipient(recipient);
        unicastHeader.setSender(senderSEP);
        unicastHeader.setSphereId(sphereName);
        unicastHeader.setUniqueMsgId(messageId);
        return unicastHeader;
    }
}
