package com.bezirk.control.messages;

import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This testCase verifies the GenerateMessageId by verifying generated id in the following scenarios.
 * <p/>
 * a) EventId generated for different devices should be different.
 * b) CtrlId generated each time should be different from the previous one.
 *
 * @author AJC6KOR
 */
public class GenerateMsgIdTest {

    private static final Logger log = LoggerFactory
            .getLogger(GenerateMsgIdTest.class);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up GenerateMsgIdTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down GenerateMsgIdTest TestCase *****");
    }


    @Test
    public void testGenerateEvtId() {

        UhuServiceId serviceId = new UhuServiceId("ServiceA");
        UhuServiceEndPoint sep = new UhuServiceEndPoint("DeviceA", serviceId);
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
