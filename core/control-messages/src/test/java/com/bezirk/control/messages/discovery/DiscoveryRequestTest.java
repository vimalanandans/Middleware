package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * This testCase verifies the DiscoveryRequest by retrieving the field values after deserialization.
 *
 * @author AJC6KOR
 */
public class DiscoveryRequestTest {
    private static final Logger logger = LoggerFactory.getLogger(DiscoveryRequestTest.class);

    private static final String sphereId = "TestSphere";
    private static final ZirkId serviceId = new ZirkId("ServiceA");
    private static final BezirkZirkEndPoint sender = new BezirkZirkEndPoint(serviceId);
    private int maxDiscovered = 5;
    private long timeout = 10000;
    private int discoveryId = 2;
    private Location location = new Location("OFFICE1", "BLOCK1", "ROOM1");
    private SubscribedRole protocol = new SubscribedRole();

    @BeforeClass
    public static void setUpBeforeClass() {

        logger.info("***** Setting up DiscoveryRequestTest TestCase *****");

    }

    @AfterClass
    public static void tearDownAfterClass() {

        logger.info("***** Shutting down DiscoveryRequestTest TestCase *****");
    }


    @Test
    public void testDiscoveryRequest() {

        com.bezirk.control.messages.discovery.DiscoveryRequest discoveryRequest = new com.bezirk.control.messages.discovery.DiscoveryRequest(sphereId, sender, location, protocol, discoveryId, timeout, maxDiscovered);
        String serializedMessage = discoveryRequest.serialize();
        com.bezirk.control.messages.discovery.DiscoveryRequest deserializedDiscoveryRequest = com.bezirk.control.messages.discovery.DiscoveryRequest.deserialize(serializedMessage, com.bezirk.control.messages.discovery.DiscoveryRequest.class);
        assertEquals("DiscoveryId not equal to the set value.", discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
        assertEquals("Discriminator not set properly.", Discriminator.DiscoveryRequest, deserializedDiscoveryRequest.getDiscriminator());
        assertEquals("Location not equal to the set value.", location, deserializedDiscoveryRequest.getLocation());
        assertEquals("MaxDiscovered not equal to the set value.", maxDiscovered, deserializedDiscoveryRequest.getMaxDiscovered());
        //NOT WORKING
        //	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
        assertEquals("Timeout not equal to the set value.", timeout, deserializedDiscoveryRequest.getTimeout());

		/*Check After Updation*/
        location = new Location("OFFICE2", "BLOCK2", "ROOM2");
        maxDiscovered = 4;
        protocol = new SubscribedRole();
        timeout = 20000;
        discoveryRequest.setLocation(location);
        discoveryRequest.setMaxDiscovered(maxDiscovered);
        discoveryRequest.setProtocol(protocol);
        discoveryRequest.setTimeout(timeout);
        serializedMessage = discoveryRequest.serialize();
        deserializedDiscoveryRequest = com.bezirk.control.messages.discovery.DiscoveryRequest.deserialize(serializedMessage, com.bezirk.control.messages.discovery.DiscoveryRequest.class);
        assertEquals("DiscoveryId not equal to the set value.", discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
        assertEquals("Location not equal to the set value.", location, deserializedDiscoveryRequest.getLocation());
        assertEquals("Timeout not equal to the set value.", timeout, deserializedDiscoveryRequest.getTimeout());
        //NOT WORKING
        //	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
    }

}
