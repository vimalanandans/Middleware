package com.bezirk.middleware.addressing;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LocationTest {

    @Test
    public void test() {

        String parentLocation = "OFFICE1/BLOCK1";
        com.bezirk.middleware.addressing.Location location = new Location(parentLocation);

        Location tempLoc = new Location("OFFICE1/BLOCK1/FLOOR1");
        assertTrue("Location doesn't subsume the temp location. ", location.subsumes(tempLoc));

        tempLoc = new Location("OFFICE1/BLOCK2/FLOOR1");
        assertFalse("Location subsumes the invalid temp location. ", location.subsumes(tempLoc));

        assertEquals("Area is not equal to the set value", "BLOCK2", tempLoc.getIntermediateScope());
        assertEquals("Region is not equal to the set value.", "OFFICE1", tempLoc.getWideScope());
        assertEquals("Landmark is not equal to the set value.", "FLOOR1", tempLoc.getNarrowScope());

        Location testLoc = new Location(null, null, null);
        assertFalse("Location subsumes the invalid temp location. ", tempLoc.subsumes(testLoc));

        Location regionAreaLoc = new Location("OFFICE1/BLOCk1");
        assertNull("Landmark is found for location with only region and area", regionAreaLoc.getNarrowScope());

        testLoc = new Location("OFFICE1/BLOCK2/FLOOR1");
        assertEquals("Similar locations have different hashcode.", testLoc.hashCode(), tempLoc.hashCode());
        assertTrue("Similar locations are considered unequal.", tempLoc.equals(testLoc));

        assertFalse("Location is considered equal to test string.", tempLoc.equals("Region"));
        assertNotEquals("Location is considered equal to null.", null, tempLoc);

        testLoc = new Location("OFFICE1", null, "FLOOR");
        assertFalse("Differet locations are considered equal.", tempLoc.equals(testLoc));
        assertFalse("Differet locations are considered equal.", testLoc.equals(tempLoc));
        assertNotEquals("Different locations have same hashcode.", testLoc.hashCode(), tempLoc.hashCode());

        tempLoc = new Location("OFFICE1", null, "FLOOR");
        testLoc = new Location("OFFICE1", null, null);
        assertFalse("Differet locations are considered equal.", tempLoc.equals(testLoc));
        assertFalse("Differet locations are considered equal.", testLoc.equals(tempLoc));
        assertNotEquals("Different locations have same hashcode.", testLoc.hashCode(), tempLoc.hashCode());

        tempLoc = new Location("OFFICE1", null, null);
        testLoc = new Location(null, null, null);
        assertFalse("Differet locations are considered equal.", tempLoc.equals(testLoc));
        assertFalse("Differet locations are considered equal.", testLoc.equals(tempLoc));
        assertNotEquals("Different locations have same hashcode.", testLoc.hashCode(), tempLoc.hashCode());

        tempLoc = new Location(null, null, null);
        assertTrue("Similar locations are not considered equal.", tempLoc.equals(testLoc));
        assertTrue("Similar locations are not considered equal.", testLoc.equals(tempLoc));
        assertEquals("Similar locations have different hashcode.", testLoc.hashCode(), tempLoc.hashCode());


    }

}
