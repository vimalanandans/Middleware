package com.bezirk.middleware.addressing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * This testcase verifies the RecipientSelector by setting the properties and retrieving them.
 */
public class RecipientSelectorTest {

    @Test
    public void test() {


        Location loc = new Location("OFFICE1/BLOCK1/FLOOR1");
        RecipientSelector recipientSelector = new RecipientSelector(loc);

        assertEquals("Location is not equal to the set value.", loc, recipientSelector.getLocation());

        loc = new Location("OFFICE1/BLOCK1/FLOOR2");

        assertNotEquals("Location in recipientSelector is equal to another invalid location.", loc, recipientSelector.getLocation());
    }
}
