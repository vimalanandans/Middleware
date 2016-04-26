package com.bezirk.proxy.api.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the equals and hashcode apis of BezirkZirkId.
 *
 * @author AJC6KOR
 */
public class BezirkZirkIdTest {

    @Test
    public void test() {

        String zirkId = "Zirk25";
        BezirkZirkId bezirkZirkId = new BezirkZirkId(zirkId);

        assertEquals("ZirkId is not equal to the set value.", zirkId, bezirkZirkId.getBezirkZirkId());

        BezirkZirkId bezirkZirkIdTemp = null;
        assertFalse("bezirkZirkID is considered equal to null.", bezirkZirkId.equals(bezirkZirkIdTemp));

        assertFalse("bezirkZirkID is considered equal to zirkId.", bezirkZirkId.equals(zirkId));


        bezirkZirkIdTemp = new BezirkZirkId(null);
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", bezirkZirkId.equals(bezirkZirkIdTemp));
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", bezirkZirkIdTemp.equals(bezirkZirkId));
        assertNotEquals("bezirkZirkID with different zirkIds have same hashcode.", bezirkZirkId.hashCode(), bezirkZirkIdTemp.hashCode());

        bezirkZirkIdTemp = new BezirkZirkId(zirkId);
        assertTrue("bezirkZirkID with same zirkIds are considered unequal.", bezirkZirkId.equals(bezirkZirkIdTemp));
        assertEquals("bezirkZirkID with same zirkIds have different hashcode.", bezirkZirkId.hashCode(), bezirkZirkIdTemp.hashCode());

        zirkId = "Zirk26";
        bezirkZirkIdTemp = new BezirkZirkId(zirkId);
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", bezirkZirkId.equals(bezirkZirkIdTemp));
        assertNotEquals("bezirkZirkID with different zirkIds have same hashcode.", bezirkZirkId.hashCode(), bezirkZirkIdTemp.hashCode());


    }

}
