package com.bezirk.aip;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies InspireMe event by setting the properties and retrieving them after deserialization.
 * 
 * @author AJC6KOR
 *
 */
public class InspireMeTest {

	@Test
	public void test() {

		Context context= new Context();
		String user = "BOB";
		context.setUser(user);
		String id="TestInspireID";
		int maxInspirations =4;
		String subTopic ="SubTopicInspiration";
		
		InspireMe inspireMe = new InspireMe();
		inspireMe.setContext(context);
		inspireMe.setId(id);
		inspireMe.setMaxInspirations(maxInspirations);
		inspireMe.setSubTopic(subTopic);
		
		String serializedInspireMe = inspireMe.serialize();
		InspireMe deserializedInspireMe = InspireMe.deserialize(serializedInspireMe,InspireMe.class);
		
		assertEquals("ContextUser is not equal to set value.",user,deserializedInspireMe.getContext().getUser());
		assertEquals("Id is not equal to set value.",id,deserializedInspireMe.getId());
		assertEquals("MaxInspirations is not equal to set value.",maxInspirations,deserializedInspireMe.getMaxInspirations());
		assertEquals("Subtopic is not equal to set value.",subTopic,deserializedInspireMe.getSubTopic());
	}

}
