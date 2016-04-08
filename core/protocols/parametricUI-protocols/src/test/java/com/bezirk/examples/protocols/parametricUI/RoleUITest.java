package com.bezirk.examples.protocols.parametricUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;

import org.junit.Test;

/**
 *This testcase verifies the RoleConusmeUI and RoleParametricUI protocols by retrieving the properties after deserialization.
 *
 * 
 * @author AJC6KOR
 *
 */
public class RoleUITest {

	@Test
	public void test() {
		
		testConsumeUIRole();
		
		testParametricUIRole();
	
	}


	private void testConsumeUIRole() {

		RoleConsumeUI consumerRole = new RoleConsumeUI();
				
		String[] expectedEvents = new String[]{ReplyUIchoices.TOPIC, ReplyUIvalues.TOPIC};
		assertTrue("ConsumeRoleUI is missing expected event topics in eventTopicList.",Arrays.equals(expectedEvents, consumerRole.getEventTopics()));
		assertEquals("ProtocolName is not equal to RoleConsumeUI.", RoleConsumeUI.class.getSimpleName(),consumerRole.getProtocolName());
		assertNotNull("Description is null for RoleConsumeUI",consumerRole.getDescription());
		assertNull("StreamTopics is not null for RoleConsumeUI",consumerRole.getStreamTopics());
	}
	
	private void testParametricUIRole() {
		
		RoleParametricUI parametricRole = new RoleParametricUI();
		
		String[] expectedEvents = new String[]{NoticeUIshowPic.TOPIC,
				 NoticeUIshowText.TOPIC,
				 NoticeUIshowVideo.TOPIC,
				 RequestUIinputValues.TOPIC,
				 RequestUImultipleChoice.TOPIC,
				 RequestUIpickOne.TOPIC	};
		assertTrue("ParametricRole is missing expected event topics in eventTopicList.",Arrays.equals(expectedEvents, parametricRole.getEventTopics()));
		assertEquals("ProtocolName is not equal to RoleParametricUI.", RoleParametricUI.class.getSimpleName(),parametricRole.getProtocolName());
		assertNotNull("Description is null for RoleConsumeUI",parametricRole.getDescription());
		assertNull("StreamTopics is not null for RoleConsumeUI",parametricRole.getStreamTopics());
		
	}

}
