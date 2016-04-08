package com.bezirk.protocols.penguin.v01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *  This testcase verifies the GetUserProfile event by setting the properties and retrieving them.
 *  
 * @author RHR8KOR
 *
 */
public class EventsTest {

	@SuppressWarnings("static-access")
	@Test
	public void test() {
		
		
		/**
		 *  JUnit Test for the GetUserProfile.java which extends Event
		 */
		
		
		GetUserProfile getUsrProfile = new GetUserProfile();
		
		getUsrProfile.setUser("User_1");	
		getUsrProfile.setService("User_1_Service");		
		
		ContextValue contextValue1 = new ContextValue("Service_1", "FM");
		ContextValue contextValue2 = new ContextValue("Service_2", "AM");
		ContextValue contextValueNew = new ContextValue("Service_3", "FM 101");

		List<ContextValue> context = new ArrayList<ContextValue>();
		
		context.add(contextValue1);
		context.add(contextValue2);
		
		getUsrProfile.setContext(context);
		assertEquals(2, getUsrProfile.getContext().size());		
		assertFalse("Context Object contains the contextValues", getUsrProfile.getContext().contains(contextValueNew));
		assertTrue("Context Object Does'nt contains the contextValue1", getUsrProfile.getContext().contains(contextValue1));

		/*----- TO BE UNCOMMENTED ONCE GetUserProfile IS FIXED ------
		 *  FIX : Second constructor should initialize the context*/
 	/*	
		GetUserProfile getUserProfileString = new GetUserProfile("TTS Service");
		getUserProfileString.addContext(contextValue1);
		getUserProfileString.addContext(contextValue2);		
		assertEquals(2, getUserProfileString.getContext().size());*/
		
		
		String jsonSer = getUsrProfile.serialize();
		GetUserProfile getUserProfSer = new GetUserProfile();
		getUserProfSer = GetUserProfile.deserialize(jsonSer);
		assertEquals("User_1", getUserProfSer.getUser());
		assertEquals("User_1_Service", getUserProfSer.getService());
		assertEquals(2, getUserProfSer.getContext().size());

	 
		
 	}

}
