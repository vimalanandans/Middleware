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
 * This testCase verifies the ControlMessage POJO by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class ControlMessageTest {


    private static final Logger log = LoggerFactory
            .getLogger(ControlMessageTest.class);

    private static final String key = "TESTKEY";
    private static final Boolean retransmit = true;
    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;
    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up ControlMessageTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down ControlMessageTest TestCase *****");
    }

    @Test
    public void testControlMessage() {

        com.bezirk.control.messages.ControlMessage ctrlMessage = new com.bezirk.control.messages.ControlMessage(sender, sphereId, discriminator, retransmit, key);
        String serializedMessage = ctrlMessage.serialize();
        com.bezirk.control.messages.ControlMessage deserializedCtrlMessage = com.bezirk.control.messages.ControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.ControlMessage.class);

        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
        assertEquals("Retransmit not equal to the set value.", retransmit, deserializedCtrlMessage.getRetransmit());

	/*--- TO BE UNCOMMENTED ONCE THE UHUSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 
	 */
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
        assertEquals("Key not equal to the set value.", key, deserializedCtrlMessage.getUniqueKey());
        assertEquals("Deserialized messageId not matching with the generated id.", ctrlMessage.getMessageId(), deserializedCtrlMessage.getMessageId());

    }


}
