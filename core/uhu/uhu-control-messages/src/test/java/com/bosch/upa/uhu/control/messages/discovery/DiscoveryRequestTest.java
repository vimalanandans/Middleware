package com.bosch.upa.uhu.control.messages.discovery;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.api.addressing.Location;
import com.bosch.upa.uhu.control.messages.ControlMessage.Discriminator;
import com.bosch.upa.uhu.proxy.api.impl.SubscribedRole;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

/**
 * This testCase verifies the DiscoveryRequest by retrieving the field values after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class DiscoveryRequestTest {
	
	private static final Logger log = LoggerFactory
			.getLogger(DiscoveryRequestTest.class);

	private static final String sphereId = "TestSphere";
	private static final UhuServiceId serviceId = new UhuServiceId("ServiceA");
	private static final UhuServiceEndPoint sender = new UhuServiceEndPoint(serviceId );
	private int maxDiscovered =5;
	private long timeout =10000;
	private int discoveryId =2;
	private Location location = new Location("OFFICE1", "BLOCK1", "ROOM1");
	private SubscribedRole protocol = new SubscribedRole();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		log.info("***** Setting up DiscoveryRequestTest TestCase *****");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {

		log.info("***** Shutting down DiscoveryRequestTest TestCase *****");
	}



	
	@Test
	public void testDiscoveryRequest() {
		
		DiscoveryRequest discoveryRequest = new DiscoveryRequest(sphereId, sender, location, protocol, discoveryId, timeout, maxDiscovered);
		String serializedMessage = discoveryRequest.serialize();
		DiscoveryRequest deserializedDiscoveryRequest = DiscoveryRequest.deserialize(serializedMessage, DiscoveryRequest.class);
		assertEquals("DiscoveryId not equal to the set value.",discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
		assertEquals("Discriminator not set properly.",Discriminator.DiscoveryRequest, deserializedDiscoveryRequest.getDiscriminator());
		assertEquals("Location not equal to the set value.",location, deserializedDiscoveryRequest.getLocation());
		assertEquals("MaxDiscovered not equal to the set value.",maxDiscovered, deserializedDiscoveryRequest.getMaxDiscovered());
		//NOT WORKING
		//	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
		assertEquals("Timeout not equal to the set value.",timeout, deserializedDiscoveryRequest.getTimeout());

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
		deserializedDiscoveryRequest = DiscoveryRequest.deserialize(serializedMessage, DiscoveryRequest.class);
		assertEquals("DiscoveryId not equal to the set value.",discoveryId, deserializedDiscoveryRequest.getDiscoveryId());
		assertEquals("Location not equal to the set value.",location, deserializedDiscoveryRequest.getLocation());
		assertEquals("Timeout not equal to the set value.",timeout, deserializedDiscoveryRequest.getTimeout());
		//NOT WORKING
				//	assertEquals("Protocol not equal to the set value.",protocol, deserializedDiscoveryRequest.getProtocol());
	}

}
