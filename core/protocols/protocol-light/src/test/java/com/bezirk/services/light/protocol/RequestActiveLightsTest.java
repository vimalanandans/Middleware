package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the RequestActiveLights events by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class RequestActiveLightsTest {


	@Test
	public void test() {

		String requestedLocation= "FLOOR1/ROOM1/TABLE";
		
		RequestActiveLights requestActiveLights = new RequestActiveLights();
		requestActiveLights.setRequestedLocation(requestedLocation);
	
		String serializedRequestActiveLights = requestActiveLights.serialize();
		
		RequestActiveLights deserializedRequestActiveLight = RequestActiveLights.deserialize(serializedRequestActiveLights, RequestActiveLights.class);
		
		assertEquals("RequestedLocation not equal to the set value.",requestedLocation, deserializedRequestActiveLight.getRequestedLocation());

		
	}

}
