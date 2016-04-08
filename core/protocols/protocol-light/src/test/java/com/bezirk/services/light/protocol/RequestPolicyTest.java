package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the RequestPolicy event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class RequestPolicyTest {

	@Test
	public void test() {

		String location ="ROOM1";
		RequestPolicy requestPolicy = new RequestPolicy(location );
	
		String serializedRequestPolicy = requestPolicy.serialize();
		RequestPolicy deserializedRequestPolicy = RequestPolicy.deserialize(serializedRequestPolicy, RequestPolicy.class);
		
		assertEquals("Location not equal to the set value.",location, deserializedRequestPolicy.getLocation());
				
		
	}

}
