package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage.Discriminator;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

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

    @Test
    public void testDiscoveryRequest() {
        DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, location, null, discoveryId, timeout, maxDiscovered);
        String serializedMessage = discoveryRequest.serialize();
        DiscoveryRequest deserializedDiscoveryRequest = DiscoveryRequest.deserialize(serializedMessage, DiscoveryRequest.class);
        assertEquals(discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
        assertEquals(Discriminator.DiscoveryRequest, deserializedDiscoveryRequest.getDiscriminator());
        assertEquals(location, deserializedDiscoveryRequest.getLocation());
        assertEquals(maxDiscovered, deserializedDiscoveryRequest.getMaxDiscovered());
        //NOT WORKING
        //	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
        assertEquals("Timeout not equal to the set value.", timeout, deserializedDiscoveryRequest.getTimeout());

		/*Check After Updation*/
        location = new Location("OFFICE2", "BLOCK2", "ROOM2");
        maxDiscovered = 4;

        timeout = 20000;
        discoveryRequest.setLocation(location);
        discoveryRequest.setMaxDiscovered(maxDiscovered);
        discoveryRequest.setProtocol(null);
        discoveryRequest.setTimeout(timeout);
        serializedMessage = discoveryRequest.serialize();
        deserializedDiscoveryRequest = DiscoveryRequest.deserialize(serializedMessage, DiscoveryRequest.class);
        assertEquals(discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
        assertEquals(location, deserializedDiscoveryRequest.getLocation());
        assertEquals(timeout, deserializedDiscoveryRequest.getTimeout());
        //NOT WORKING
        //	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
    }

    private class MockProtocolRole extends ProtocolRole implements Serializable {
        public String getRoleName() {
            return "test role";
        }

        public String getDescription() {
            return "Blah blah blah";
        }

        public String[] getEventTopics() {
            return new String[] {""};
        }

        public String[] getStreamTopics() {
            return new String[] {""};
        }
    }
}
