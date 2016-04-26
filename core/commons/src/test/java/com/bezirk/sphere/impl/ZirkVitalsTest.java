package com.bezirk.sphere.impl;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This testcase verifies the ServiceVitals by retrieving the set values for different fields.
 *
 * @author AJC6KOR
 */
public class ZirkVitalsTest {

    @Test
    public void test() {

        String serviceName = "MockServiceA";
        String ownerDeviceID = "DeviceA";
        ServiceVitals serviceVitals = new ServiceVitals(serviceName, ownerDeviceID);

        assertEquals("ServiceName is not equal to the set value.", serviceName, serviceVitals.getServiceName());
        assertEquals("OwnerDeviceID is not equal to the set value.", ownerDeviceID, serviceVitals.getOwnerDeviceID());


        assertTrue("Same serviceVitals is considered as not equal.", serviceVitals.equals(serviceVitals));
        ServiceVitals serviceVitalsTemp = new ServiceVitals(serviceName, ownerDeviceID);

        assertEquals("Similar serviceVitals have different hashcode.", serviceVitals.hashCode(), serviceVitalsTemp.hashCode());
        assertTrue("Similar serviceVitals is considered as not equal.", serviceVitals.equals(serviceVitalsTemp));

        serviceVitalsTemp = new ServiceVitals(null, null);

        assertNotEquals("Different serviceVitals have same hashcode.", serviceVitals.hashCode(), serviceVitalsTemp.hashCode());
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitals.equals(serviceVitalsTemp));
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitalsTemp.equals(serviceVitals));

        serviceVitalsTemp = new ServiceVitals(null, ownerDeviceID);
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitals.equals(serviceVitalsTemp));
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitalsTemp.equals(serviceVitals));

        serviceVitalsTemp = new ServiceVitals("ServiceB", ownerDeviceID);
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitals.equals(serviceVitalsTemp));

        serviceVitalsTemp = new ServiceVitals(serviceName, "DeviceB");
        assertFalse("Different serviceVitals are considered as  equal.", serviceVitals.equals(serviceVitalsTemp));

        assertFalse("ServiceVitals is considered equal to null.", serviceVitals.equals(null));

        serviceVitals = new ServiceVitals(serviceName, null);
        serviceVitalsTemp = new ServiceVitals(serviceName, null);
        assertTrue("Similar serviceVitals is considered as not equal.", serviceVitals.equals(serviceVitalsTemp));

        serviceVitals = new ServiceVitals(null, ownerDeviceID);
        serviceVitalsTemp = new ServiceVitals(null, ownerDeviceID);
        assertTrue("Similar serviceVitals is considered as not equal.", serviceVitals.equals(serviceVitalsTemp));

        assertFalse("ServiceVitals is considered as equal to servicename.", serviceVitals.equals(serviceName));

    }

}
