package com.bezirk.protocols.context;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.bezirk.protocols.context.exception.UserPreferenceException;
import com.bezirk.middleware.addressing.Location;

/**
 *	 This testcase verifies the Context by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class ContextTest {

	@Test
	public void test() {

		Location location = new Location("FLOOR1/ROOM1/LAB");
		String dateTime ="2007-04-05 12.30-02:001+0001";
		String partOfDay = "First Session"; 
		Context context = null;
		try {
			
			context = new Context(location, dateTime);
		} catch (UserPreferenceException e) {
			
			fail("Unable to create new context."+e.getMessage());
		
		}
		
		assertEquals("DateTime is not equal to the set value.",dateTime,context.getDateTime());
		assertEquals("Location is not equal to the set value.",location,context.getLocation());

		context = new Context();
		try {
			
			context.setDateTime(dateTime);
			
		} catch (UserPreferenceException e) {
			
			fail("Unable to set datetime for new context."+e.getMessage());
		}
		context.setLocation(location);
		context.setPartOfDay(partOfDay);
		
		assertEquals("DateTime is not equal to the set value.",dateTime,context.getDateTime());
		assertEquals("Location is not equal to the set value.",location,context.getLocation());
		assertEquals("PartOfDay is not equal to the set value.",partOfDay,context.getPartOfDay());
		
		Context testContext = new Context();
		try {
			
			testContext.setDateTime(dateTime);
			
		} catch (UserPreferenceException e) {
			
			fail("Unable to set datetime for new context."+e.getMessage());
		}
		testContext.setLocation(location);
		assertEquals("Similar contexts have different string representation.",testContext.toString(),context.toString());
		
		testDateTime();
	}

	private void testDateTime() {
		
		Location location = new Location("FLOOR1/ROOM1/LAB");
		String dateTime ="2007-04 12.30-02:001+0001";
		boolean isValidDateTime= true;
		Context context;
		
		try {
			
			context = new Context(location,dateTime);
			
		} catch (UserPreferenceException e) {

			isValidDateTime= false;
			
		}
		
		assertFalse("Invalid date time format is allowed in context",isValidDateTime);
		
		dateTime = "2007-04-05 12.30-02:001+000";
		isValidDateTime=true;
		context = new Context();
		
		try {
			
			context.setDateTime(dateTime);
			
		} catch (UserPreferenceException e) {

			isValidDateTime= false;
			
		}
		
		assertFalse("Invalid date time format is allowed in context",isValidDateTime);
		
		dateTime = "2007-04-05";
		isValidDateTime=true;
		try {
			
			context.setDateTime(dateTime);
			
		} catch (UserPreferenceException e) {

			isValidDateTime= false;
			
		}
		
		assertFalse("Invalid date time format is allowed in context",isValidDateTime);
		
		dateTime = "2007-04-05 12";
		isValidDateTime=true;
		try {
			
			context.setDateTime(dateTime);
			
		} catch (UserPreferenceException e) {

			isValidDateTime= false;
			
		}
		
		assertFalse("Invalid date time format is allowed in context",isValidDateTime);
		
	}

}
