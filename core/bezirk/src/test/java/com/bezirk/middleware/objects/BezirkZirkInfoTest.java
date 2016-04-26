package com.bezirk.middleware.objects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This testcase verifies the BezirkZirkInfo by setting the properties and retrieving them.
 *
 * @author AJC6KOR
 */

public class BezirkZirkInfoTest {

    @Test
    public void test() {
        String zirkName = "ZirkA";
        String zirkId = "Zirk123";
        String zirkType = "MemberZirk";
        boolean active = true;
        boolean visible = true;
        BezirkZirkInfo bezirkZirkInfo = new BezirkZirkInfo(zirkId, zirkName, zirkType, active, visible);

        assertEquals("ZirkId is not equal to the set value.", zirkId, bezirkZirkInfo.getZirkId());
        assertEquals("ZirkName is not equal to the set value.", zirkName, bezirkZirkInfo.getZirkName());
        assertEquals("ZirkType is not equal to the set value.", zirkType, bezirkZirkInfo.getZirkType());
        assertTrue("Zirk is considered inactive.", bezirkZirkInfo.isActive());
        assertTrue("Zirk is not visible.", bezirkZirkInfo.isVisible());

        bezirkZirkInfo.setActive(false);
        assertFalse("Inactive zirk is considered active.", bezirkZirkInfo.isActive());

        zirkName = "ZirkB";
        BezirkZirkInfo bezirkZirkInfoTemp = new BezirkZirkInfo(zirkId, zirkName, zirkType, active, visible);
        assertFalse("Different bezirkZirkInfo has same string representation.", bezirkZirkInfo.toString().equalsIgnoreCase(bezirkZirkInfoTemp.toString()));
    }
}
