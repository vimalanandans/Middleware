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
public class ServiceTest {

    @Test
    public void test() {

        String serviceName = "ServiceA";
        String ownerDeviceId = "DeviceA";
        HashSet<String> sphereSet = new HashSet<String>();
        sphereSet.add("TestSphere");

        Service service = new OwnerService(serviceName, ownerDeviceId, sphereSet);

        service.setOwnerDeviceId(ownerDeviceId);
        service.setServiceName(serviceName);
        service.setSphereSet(sphereSet);

        assertEquals("Service name is not equal to the set value.", serviceName, service.getServiceName());
        assertEquals("OwnerDeviceID is not equal to the set value.", ownerDeviceId, service.getOwnerDeviceId());
        assertEquals("Sphereset size is not equal to the set value.", sphereSet.size(), service.getSphereSet().size());

        Service testService = new OwnerService(serviceName, ownerDeviceId, sphereSet);


        testService.setOwnerDeviceId(ownerDeviceId);
        testService.setServiceName(serviceName);
        testService.setSphereSet(sphereSet);

        assertEquals("Similar services have different hashcode.", service.hashCode(), testService.hashCode());
        assertTrue("Similar services are considered unequal.", service.equals(testService));
        assertTrue("Service is not considered equal to itself.", service.equals(service));

        assertFalse("Service is considered equal to null.", service.equals(null));
        assertFalse("Service is considered equal to serviceName.", service.equals(serviceName));

        testService.setOwnerDeviceId(null);
        assertNotEquals("Different services have same hashcode.", service.hashCode(), testService.hashCode());
        assertFalse("Services with different ownerDeviceId are considered equal.", service.equals(testService));
        assertFalse("Services with different ownerDeviceId are considered equal.", testService.equals(service));

        service.setOwnerDeviceId(null);
        testService.setServiceName(null);
        assertNotEquals("Different services have same hashcode.", service.hashCode(), testService.hashCode());
        assertFalse("Services with different serviceName are considered equal.", service.equals(testService));
        assertFalse("Services with different serviceName are considered equal.", testService.equals(service));

        service.setServiceName(null);
        sphereSet = new HashSet<>();
        sphereSet.add("Sphere1");
        testService.setSphereSet(sphereSet);
        assertNotEquals("Different services have same hashcode.", service.hashCode(), testService.hashCode());
        assertFalse("Services with different sphereset are considered equal.", service.equals(testService));
        assertFalse("Services with different sphereset are considered equal.", testService.equals(service));

        testService.setSphereSet(null);
        assertNotEquals("Different services have same hashcode.", service.hashCode(), testService.hashCode());
        assertFalse("Services with different sphereset are considered equal.", service.equals(testService));
        assertFalse("Services with different sphereset are considered equal.", testService.equals(service));

        assertNull("ServiceVitals are not yet defined. Still service is returning non null service vitals.", service.getServiceVitals());

    }

}
