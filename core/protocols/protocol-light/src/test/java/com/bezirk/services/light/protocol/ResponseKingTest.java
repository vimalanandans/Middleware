package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the ResponseKing event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class ResponseKingTest {


	@Test
	public void test() {

		String location ="FLOOR1/ROOM1/TABLE";
		String king ="BOB";
		ResponseKing responseKing = new ResponseKing(location, king);
		
		String serializedResponseKing = responseKing.serialize();
		
		ResponseKing deserializedResponseKing = ResponseKing.deserialize(serializedResponseKing, ResponseKing.class);
		
		assertEquals("King is not equal to the set value.",king,deserializedResponseKing.getKing());
		assertEquals("Location is not equal to the set value.", location,deserializedResponseKing.getLocation());
		
	
	}

}
