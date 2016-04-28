/**
 *
 */
package com.bezirk.control.messages;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the MulticastHeader POJO by retrieving the field values.
 *
 * @author AJC6KOR
 */
public class MulticastHeaderTest {

    private static final Logger log = LoggerFactory
            .getLogger(MulticastControlMessageTest.class);

    private static final String sphereName = "TestSphere";
    private static final BezirkZirkId serviceId = new BezirkZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private static final Location loc = new Location("OFFICE1", "BLOCk1", "ROOM1");
    private static final Address address = new Address(loc);
    private static final String messageId = "TestID";
    private static final String topic = "Message";


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

    @Test
    public void testMulticastHeader() {

        com.bezirk.control.messages.MulticastHeader multicastHeader = prepareMulticastHeader();

        assertEquals("Address not equal to the set value.", address, multicastHeader.getAddress());
        /*--- TO BE UNCOMMENTED ONCE THE BEZIRKSERVICEENDPOINT IS FIXED-----
		 * device null condition should be checked separately before device equals in UhuServiceEndpoint equals api.
		 * 
		assertEquals("DataOnWire not equal to the set value.",sender, multicastHeader.getSenderSEP());
		*/
        assertEquals("SphereName not equal to the set value.", sphereName, multicastHeader.getSphereName());
        assertEquals("Topic not equal to the set value.", topic, multicastHeader.getTopic());
        assertEquals("MessageID not equal to the set value.", messageId, multicastHeader.getUniqueMsgId());


    }

    private com.bezirk.control.messages.MulticastHeader prepareMulticastHeader() {
        com.bezirk.control.messages.MulticastHeader multicastHeader = new com.bezirk.control.messages.MulticastHeader();
        multicastHeader.setAddress(address);
        multicastHeader.setSenderSEP(sender);
        multicastHeader.setSphereName(sphereName);
        multicastHeader.setTopic(topic);
        multicastHeader.setUniqueMsgId(messageId);
        return multicastHeader;
    }

}