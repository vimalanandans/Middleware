package com.bezirk.protocols.dragonfly.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *	 This testcase verifies the ObservationTest event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class ObservationTestTest {

	@Test
	public void test() {
		
		Integer observationsNumberInThisBatch=5;
		
		ObservationTest observationTest = new ObservationTest();
		observationTest.setObservationsNumberInThisBatch(observationsNumberInThisBatch);;
		
		
		String serializedObservationTest = observationTest.serialize();
		
		ObservationTest deserializedObservationTest = ObservationTest.deserialize(serializedObservationTest);
		
		assertEquals("ObservationsNumberInThisBatch is not equal to the set value.",observationsNumberInThisBatch,deserializedObservationTest.getObservationsNumberInThisBatch());

	}

}
