/**
 *
 */
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
 * This testCase verifies the MulticastControlMessage by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */

public class MulticastControlMessageTest {

    private static final Logger log = LoggerFactory
            .getLogger(MulticastControlMessageTest.class);

    private static final String sphereId = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private static final String key = "TESTKEY";
    private static final Discriminator discriminator = Discriminator.DiscoveryRequest;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        log.info("***** Setting up MulticastControlMessageTest TestCase *****");

    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {

        log.info("***** Shutting down MulticastControlMessageTest TestCase *****");
    }

    /**
     * Test method for {@link com.bezirk.control.messages.MulticastControlMessage#MulticastControlMessage()}.
     */
    @Test
    public void testMulticastControlMessage() {

        com.bezirk.control.messages.MulticastControlMessage multicastCtrolMessage = new com.bezirk.control.messages.MulticastControlMessage(sender, sphereId, discriminator, key);
        String serializedMessage = multicastCtrolMessage.serialize();
        com.bezirk.control.messages.MulticastControlMessage deserializedCtrlMessage = com.bezirk.control.messages.MulticastControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.MulticastControlMessage.class);
        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
        assertEquals("Key not equal to the set value.", key, deserializedCtrlMessage.getUniqueKey());
    /*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 
	 */

        multicastCtrolMessage = new com.bezirk.control.messages.MulticastControlMessage(sender, sphereId, discriminator);
        serializedMessage = multicastCtrolMessage.serialize();
        deserializedCtrlMessage = com.bezirk.control.messages.MulticastControlMessage.deserialize(serializedMessage, com.bezirk.control.messages.MulticastControlMessage.class);
        assertEquals("Discriminator not equal to the set value.", discriminator, deserializedCtrlMessage.getDiscriminator());
        assertEquals("SphereId not equal to the set value.", sphereId, deserializedCtrlMessage.getSphereId());
	/*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
	 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
	 * 
	 assertEquals("Sender not equal to the set value.",sender, deserializedCtrlMessage.getSender());
	 
	 */

    }


}