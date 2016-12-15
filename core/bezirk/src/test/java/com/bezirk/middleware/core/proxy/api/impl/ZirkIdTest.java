package com.bezirk.middleware.core.proxy.api.impl;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the equals and hashcode apis of ZirkId.
 */
public class ZirkIdTest {

    @Test
    public void test() {

        String zirkId = "Zirk25";
        ZirkId bezirkZirkId = new ZirkId(zirkId);

        assertEquals("ZirkId is not equal to the set value.", zirkId, bezirkZirkId.getZirkId());

        ZirkId zirkIdTemp = null;
        assertFalse("bezirkZirkID is considered equal to null.", bezirkZirkId.equals(zirkIdTemp));

        assertFalse("bezirkZirkID is considered equal to zirkId.", bezirkZirkId.equals(zirkId));


        zirkIdTemp = new ZirkId(null);
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", bezirkZirkId.equals(zirkIdTemp));
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", zirkIdTemp.equals(bezirkZirkId));
        assertNotEquals("bezirkZirkID with different zirkIds have same hashcode.", bezirkZirkId.hashCode(), zirkIdTemp.hashCode());

        zirkIdTemp = new ZirkId(zirkId);
        assertTrue("bezirkZirkID with same zirkIds are considered unequal.", bezirkZirkId.equals(zirkIdTemp));
        assertEquals("bezirkZirkID with same zirkIds have different hashcode.", bezirkZirkId.hashCode(), zirkIdTemp.hashCode());

        zirkId = "Zirk26";
        zirkIdTemp = new ZirkId(zirkId);
        assertFalse("bezirkZirkID with different zirkIds are considered equal.", bezirkZirkId.equals(zirkIdTemp));
        assertNotEquals("bezirkZirkID with different zirkIds have same hashcode.", bezirkZirkId.hashCode(), zirkIdTemp.hashCode());


    }

}
