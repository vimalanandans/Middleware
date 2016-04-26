package com.bezirk.proxy.api.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the equals and hashcode apis of UhuZirkId.
 *
 * @author AJC6KOR
 */
public class UhuZirkIdTest {

    @Test
    public void test() {

        String serviceId = "Service25";
        UhuZirkId uhuServiceId = new UhuZirkId(serviceId);

        assertEquals("ZirkId is not equal to the set value.", serviceId, uhuServiceId.getUhuServiceId());

        UhuZirkId uhuServiceIdTemp = null;
        assertFalse("UhuserviceID is considered equal to null.", uhuServiceId.equals(uhuServiceIdTemp));

        assertFalse("UhuserviceID is considered equal to serviceId.", uhuServiceId.equals(serviceId));


        uhuServiceIdTemp = new UhuZirkId(null);
        assertFalse("UhuserviceIDs with different serviceIds are considered equal.", uhuServiceId.equals(uhuServiceIdTemp));
        assertFalse("UhuserviceIDs with different serviceIds are considered equal.", uhuServiceIdTemp.equals(uhuServiceId));
        assertNotEquals("UhuserviceIDs with different serviceIds have same hashcode.", uhuServiceId.hashCode(), uhuServiceIdTemp.hashCode());

        uhuServiceIdTemp = new UhuZirkId(serviceId);
        assertTrue("UhuserviceIDs with same serviceIds are considered unequal.", uhuServiceId.equals(uhuServiceIdTemp));
        assertEquals("UhuserviceIDs with same serviceIds have different hashcode.", uhuServiceId.hashCode(), uhuServiceIdTemp.hashCode());

        serviceId = "Service26";
        uhuServiceIdTemp = new UhuZirkId(serviceId);
        assertFalse("UhuserviceIDs with different serviceIds are considered equal.", uhuServiceId.equals(uhuServiceIdTemp));
        assertNotEquals("UhuserviceIDs with different serviceIds have same hashcode.", uhuServiceId.hashCode(), uhuServiceIdTemp.hashCode());


    }

}
