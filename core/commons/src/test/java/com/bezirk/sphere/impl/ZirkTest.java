package com.bezirk.sphere.impl;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author ajc6kor
 */
public class ZirkTest {

    @Test
    public void test() {

        String serviceName = "ServiceA";
        String ownerDeviceId = "DeviceA";
        HashSet<String> sphereSet = new HashSet<String>();
        sphereSet.add("TestSphere");

        Zirk zirk = new OwnerZirk(serviceName, ownerDeviceId, sphereSet);

        zirk.setOwnerDeviceId(ownerDeviceId);
        zirk.setZirkName(serviceName);
        zirk.setSphereSet(sphereSet);

        assertEquals("Zirk name is not equal to the set value.", serviceName, zirk.getZirkName());
        assertEquals("OwnerDeviceID is not equal to the set value.", ownerDeviceId, zirk.getOwnerDeviceId());
        assertEquals("Sphereset size is not equal to the set value.", sphereSet.size(), zirk.getSphereSet().size());

        Zirk testZirk = new OwnerZirk(serviceName, ownerDeviceId, sphereSet);


        testZirk.setOwnerDeviceId(ownerDeviceId);
        testZirk.setZirkName(serviceName);
        testZirk.setSphereSet(sphereSet);

        assertEquals("Similar services have different hashcode.", zirk.hashCode(), testZirk.hashCode());
        assertTrue("Similar services are considered unequal.", zirk.equals(testZirk));
        assertTrue("Zirk is not considered equal to itself.", zirk.equals(zirk));

        assertNotEquals("Zirk is considered equal to null.", null, zirk);
        assertFalse("Zirk is considered equal to serviceName.", zirk.equals(serviceName));

        testZirk.setOwnerDeviceId(null);
        assertNotEquals("Different services have same hashcode.", zirk.hashCode(), testZirk.hashCode());
        assertFalse("Services with different ownerDeviceId are considered equal.", zirk.equals(testZirk));
        assertFalse("Services with different ownerDeviceId are considered equal.", testZirk.equals(zirk));

        zirk.setOwnerDeviceId(null);
        testZirk.setZirkName(null);
        assertNotEquals("Different services have same hashcode.", zirk.hashCode(), testZirk.hashCode());
        assertFalse("Services with different serviceName are considered equal.", zirk.equals(testZirk));
        assertFalse("Services with different serviceName are considered equal.", testZirk.equals(zirk));

        zirk.setZirkName(null);
        sphereSet = new HashSet<>();
        sphereSet.add("Sphere1");
        testZirk.setSphereSet(sphereSet);
        assertNotEquals("Different services have same hashcode.", zirk.hashCode(), testZirk.hashCode());
        assertFalse("Services with different sphereset are considered equal.", zirk.equals(testZirk));
        assertFalse("Services with different sphereset are considered equal.", testZirk.equals(zirk));

        testZirk.setSphereSet(null);
        assertNotEquals("Different services have same hashcode.", zirk.hashCode(), testZirk.hashCode());
        assertFalse("Services with different sphereset are considered equal.", zirk.equals(testZirk));
        assertFalse("Services with different sphereset are considered equal.", testZirk.equals(zirk));

        assertNull("ServiceVitals are not yet defined. Still zirk is returning non null zirk vitals.", zirk.getServiceVitals());

    }

}
