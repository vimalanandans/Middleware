package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the RequestKing event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class RequestKingTest {


	@Test
	public void test() {

		String location ="FLOOR1/ROOM1/TABLE";
		RequestKing requestKing = new RequestKing(location );
		
		String serializedRequestKing = requestKing.serialize();
		
		RequestKing deserializedRequestKing = RequestKing.deserialize(serializedRequestKing, RequestKing.class);
		
		assertEquals("Location is not equal to the set value.",location,deserializedRequestKing.getLocation());
	
	}

}
