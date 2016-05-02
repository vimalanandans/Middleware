package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This testCase verifies the GenerateMessageId by verifying generated id in the following scenarios.
 * a) EventId generated for different devices should be different.
 * b) CtrlId generated each time should be different from the previous one.
 */
public class GenerateMsgIdTest {
    private static final Logger logger = LoggerFactory.getLogger(GenerateMsgIdTest.class);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        logger.info("***** Setting up GenerateMsgIdTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        logger.info("***** Shutting down GenerateMsgIdTest TestCase *****");
    }


    @Test
    public void testGenerateEvtId() {

        BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
        BezirkZirkEndPoint sep = new BezirkZirkEndPoint("DeviceA", serviceId);
        String eventId = com.bezirk.control.messages.GenerateMsgId.generateEvtId(sep);
        sep.device = "DeviceB";
        assertNotEquals("Same messageId generated for different devices", eventId, com.bezirk.control.messages.GenerateMsgId.generateEvtId(sep));

    }

    @Test
    public void testGenerateCtrlId() {

        int ctrlId = com.bezirk.control.messages.GenerateMsgId.generateCtrlId();

        assertNotEquals("Same controlId generated multiple times.", ctrlId, com.bezirk.control.messages.GenerateMsgId.generateCtrlId());


    }


}
