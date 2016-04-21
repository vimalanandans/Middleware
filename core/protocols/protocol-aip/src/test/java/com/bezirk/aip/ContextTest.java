package com.bezirk.aip;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the context by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */
public class ContextTest {


    @Test
    public void test() {

        String dateTime = "12-02-05 00:12:34";
        String location = "FLOOR1/ROOM1/TABLE";
        String user = "BOB";

        Context context = new Context();
        context.setDateTime(dateTime);
        context.setLocation(location);
        context.setUser(user);

        assertEquals("DateTime is not equal to set value.", dateTime, context.getDateTime());
        assertEquals("Location is not equal to set value.", location, context.getLocation());
        assertEquals("User is not equal to set value.", user, context.getUser());

    }

}
