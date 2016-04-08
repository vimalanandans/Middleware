package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.bezirk.api.addressing.Location;

/**
 *	 This testcase verifies the ResponseLightLocation event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class ResponseLightLocationTest {


	@Test
	public void test() {

		Integer lightId =25;
		Location location = new Location("FLOOR1/ROOM1/TABLE");
		ResponseLightLocation responseLightLocation = new ResponseLightLocation(lightId, location);
	
		String serializedResponseLightLocation = responseLightLocation.serialize();
		
		ResponseLightLocation deserializedResponseLightLocation = ResponseLightLocation.deserialize(serializedResponseLightLocation, ResponseLightLocation.class);
		assertEquals("LightId is not matching with the set value.", lightId,deserializedResponseLightLocation.getLightId());
		assertEquals("Location is not matching with the set value.", location,deserializedResponseLightLocation.getLocation());
				
	
	}

}
