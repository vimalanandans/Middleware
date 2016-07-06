package com.bezirk.sphere.impl;

import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.sphere.api.BezirkSphereType;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class SphereTest {


    @Test
    public void testSphere() {


        testSphereEquality();


    }

    private void testSphereEquality() {
        Sphere sphere = new Sphere();
        String deviceID = "DeviceA";
        String sphereName = "HomeSphere";

        ArrayList<ZirkId> serviceIDList = new ArrayList<>();
        serviceIDList.add(new ZirkId("ServiceA"));
        serviceIDList.add(new ZirkId("ServiceB"));
        serviceIDList.add(new ZirkId("ServiceC"));

        LinkedHashMap<String, ArrayList<ZirkId>> deviceServices = new LinkedHashMap<String, ArrayList<ZirkId>>();
        deviceServices.put(deviceID, serviceIDList);


        HashSet<String> ownerDevices = new HashSet<>();
        ownerDevices.add("DeviceA");

        Sphere sphereTemp = new Sphere();

        assertTrue("Same Spheres are considered not equal.", sphere.equals(sphereTemp));

        sphere.setSphereType(BezirkSphereType.BEZIRK_SPHERE_TYPE_CAR);
        assertFalse("Different Spheres are considered equal.", sphere.equals(sphereTemp));
        assertFalse("Different Spheres are considered equal.", sphereTemp.equals(sphere));

        sphereTemp.setSphereType(BezirkSphereType.BEZIRK_SPHERE_TYPE_CAR);
        sphere.setSphereName(sphereName);
        assertFalse("Different Spheres are considered equal.", sphere.equals(sphereTemp));
        assertFalse("Different Spheres are considered equal.", sphereTemp.equals(sphere));

        sphereTemp.setSphereName(sphereName);
        sphere.ownerDevices = ownerDevices;
        assertFalse("Different Spheres are considered equal.", sphere.equals(sphereTemp));
        assertFalse("Different Spheres are considered equal.", sphereTemp.equals(sphere));


        sphereTemp.ownerDevices = ownerDevices;
        sphere.deviceServices = deviceServices;
        assertFalse("Different Spheres are considered equal.", sphere.equals(sphereTemp));
        assertFalse("Different Spheres are considered equal.", sphereTemp.equals(sphere));
        assertNotEquals("Different Spheres have same hashcode.", sphere.hashCode(), sphereTemp.hashCode());

        sphereTemp.deviceServices = deviceServices;
        assertTrue("Similar Spheres are considered not equal.", sphere.equals(sphereTemp));
        assertEquals("Same Spheres have different hashcode.", sphere.hashCode(), sphereTemp.hashCode());

        ZirkId serviceId = new ZirkId("TestService");
        assertFalse("Remove zirk is processed.", sphere.removeService(serviceId));

        assertTrue("Same sphere is not considered equal to itself.", sphere.equals(sphere));

        assertNotEquals("sphere is considered equal to null.", null, sphere);

        assertFalse("sphere is considered equal to serviceID.", sphere.equals(serviceId));

        sphereTemp.ownerDevices = null;
        sphereTemp.deviceServices = null;
        sphereTemp.sphereName = null;
        sphereTemp.sphereType = null;
        assertNotEquals("Different Spheres have same hashcode.", sphere.hashCode(), sphereTemp.hashCode());


    }

}
