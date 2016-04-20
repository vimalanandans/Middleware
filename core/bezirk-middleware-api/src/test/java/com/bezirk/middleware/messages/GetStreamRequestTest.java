/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.middleware.messages;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the GetStreamRequest by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class GetStreamRequestTest {

	@Test
	public void test() {

		String subTopic ="TestSubTopic";
		com.bezirk.middleware.messages.GetStreamRequest getStreamRequest = new GetStreamRequest(subTopic );
		String serializedgetStreamRequest = getStreamRequest.serialize();
		GetStreamRequest deserializedRequest = GetStreamRequest.deserialize(serializedgetStreamRequest);
		
		assertEquals("Subtopic is not equal to the set value.",subTopic, deserializedRequest.getSubTopic());
	
	}

}
