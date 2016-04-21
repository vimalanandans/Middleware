package com.bezirk.protocols.penguin.v01;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the Preference by setting the properties and retrieving them after deserialization.
 *
 * @author RHR8KOR
 */
public class PreferencesTest {

    @Test
    public void test() {

        Preference pref = new Preference();


        String user = "BOB";
        String value = "Friend";
        Preference prefN = new Preference(user, "Public", value, "HASH TAG", 100.00d);


        assertEquals(user, prefN.getUser());
        assertEquals("Public", prefN.getType());
        assertEquals(value, prefN.getValue());
        assertEquals(100.00, prefN.getConfidence(), 0.05);


        /**
         *  First We will invoke all the setters and then
         *  we can call all the Getters for our JUNIT
         * 	testing.
         */

        String dateTime = "12-06-2015";
        String format = "BIG INDIAN";
        String location = "OFFICE";
        String partOfDay = "Saturday";
        String source = "Home Party";
        String type = "Private";
        pref.setConfidence(10.00);
        pref.setDateTime(dateTime);
        pref.setFormat(format);
        pref.setLocation(location);
        pref.setPartOfDay(partOfDay);
        pref.setSource(source);
        pref.setType(type);
        pref.setUser(user);
        pref.setValue(value);


        assertEquals(10.00, pref.getConfidence(), 0.05);
        assertEquals(dateTime, pref.getDateTime());
        assertEquals(format, pref.getFormat());
        assertEquals(location, pref.getLocation());
        assertEquals(partOfDay, pref.getPartOfDay());
        assertEquals(source, pref.getSource());
        assertEquals(type, pref.getType());
        assertEquals(user, pref.getUser());
        assertEquals(value, pref.getValue());

    }

}
