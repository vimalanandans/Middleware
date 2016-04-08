package com.bezirk.services.light.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the MakeKing event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class MakeKingTest {


	@Test
	public void test() {

		String location = "FLOOR1/ROOM1/TABLE";
		String king="BOB";
		MakeKing makeKing = new MakeKing(location, king);
		
		String serializedMakeking = makeKing.serialize();
		
		MakeKing deserializedMakeking = MakeKing.deserialize(serializedMakeking, MakeKing.class);
		
		assertEquals("Location is not matching to the set value.",location,deserializedMakeking.getLocation());
		assertEquals("King is not matching to the set value.",king,deserializedMakeking.getKing());
				
		
	}

}
