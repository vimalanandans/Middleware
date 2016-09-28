package com.bezirk.middleware.core.control.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This testCase verifies the ControlMessage POJO by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class ControlMessageTest {
    private static final Logger logger = LoggerFactory.getLogger(ControlMessageTest.class);

    private static final String key = "TESTKEY";
    private static final Boolean retransmit = true;
    private static final Discriminator discriminator = Discriminator.DISCOVERY_REQUEST;
    private static final String sphereId = "TestSphere";
    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);

    @BeforeClass
    public static void setUpBeforeClass() {

        logger.info("***** Setting up ControlMessageTest TestCase *****");

    }

    @AfterClass
    public static void tearDownAfterClass() {

        logger.info("***** Shutting down ControlMessageTest TestCase *****");
    }

    @Test
    public void testControlMessage() {

        com.bezirk.middleware.core.control.messages.ControlMessage ctrlMessage = new com.bezirk.middleware.core.control.messages.ControlMessage(sender, sphereId, discriminator, retransmit, key);
        String serializedMessage = ctrlMessage.serialize();
        com.bezirk.middleware.core.control.messages.ControlMessage deserializedCtrlMessage = com.bezirk.middleware.core.control.messages.ControlMessage.deserialize(serializedMessage, com.bezirk.middleware.core.control.messages.ControlMessage.class);

        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
     //   assertEquals("Retransmit not equal to the set value.", retransmit, deserializedCtrlMessage.getRetransmit());

	/*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in BezirkServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 
	 */
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
        assertEquals("Key not equal to the set value.", key, deserializedCtrlMessage.getUniqueKey());
        assertEquals("Deserialized messageId not matching with the generated id.", ctrlMessage.getMessageId(), deserializedCtrlMessage.getMessageId());

    }


}
