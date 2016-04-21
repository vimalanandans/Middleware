package com.bezirk.discoveredService.test;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * This testcase verifies the UhuDiscoveredService by setting the properties and retrieving them after deserialization.
 */
public class UhuDiscoveredServiceTest {
    private UhuServiceId sid1;
    private UhuServiceId sid2;
    private UhuServiceEndPoint sed1;
    private UhuServiceEndPoint sed2;
    private HashSet<UhuDiscoveredService> list = new HashSet<UhuDiscoveredService>();


    @Before
    public void beforeTest() {
        sid1 = new UhuServiceId("Ys1NcReyox:AreYouHotonAndroid");
        sid2 = new UhuServiceId("Ys1NcReyox:AreYouHotonAndroid");
        sed1 = new UhuServiceEndPoint(new String("192.168.160.65"), sid1);
        sed2 = new UhuServiceEndPoint(new String("192.168.160.65"), sid2);

    }

    @Test
    public void test() {
        UhuDiscoveredService ds1 = new UhuDiscoveredService(sed1, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        UhuDiscoveredService ds2 = new UhuDiscoveredService(sed2, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        assertEquals(ds1, ds2);
        if (!list.contains(ds1)) {
            list.add(ds1);
        }
        assertTrue(list.contains(ds2)); //Since ds1 and ds2 are equal

        UhuServiceId serviceId = new UhuServiceId("Service25");
        UhuServiceEndPoint serviceEndPoint = new UhuServiceEndPoint(serviceId);
        serviceEndPoint.device = "DeviceA";
        String serviceName = "ServiceA";
        String pRole = "TestProtocol";
        Location location = new Location("OFFICE1/BLOCK1/FLOOR1");
        UhuDiscoveredService uhuDiscoveredService = new UhuDiscoveredService(serviceEndPoint, serviceName, pRole, location);

        assertEquals("Location is not equal to the set value.", location, uhuDiscoveredService.getLocation());
        assertEquals("Protocol is not equal to the set value.", pRole, uhuDiscoveredService.getProtocol());
        assertEquals("ServiceEndPoint is not equal to the set value.", serviceEndPoint, uhuDiscoveredService.getServiceEndPoint());
        assertEquals("ServiceName is not equal to the set value.", serviceName, uhuDiscoveredService.getServiceName());

        UhuDiscoveredService uhuDiscoveredServiceTemp = null;
        assertFalse("UhuDiscoveredService is considered as equal to null.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));
        assertTrue("UhuDiscoveredService is not considered equal to itself.", uhuDiscoveredService.equals(uhuDiscoveredService));
        assertFalse("UhuDiscoveredService is considered as equal to serviceEndPoint.", uhuDiscoveredService.equals(serviceEndPoint));


        uhuDiscoveredServiceTemp = new UhuDiscoveredService(serviceEndPoint, serviceName, pRole, location);
        assertTrue("Similar UhuDiscoveredServices are not considered equal.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));
        serviceId = new UhuServiceId("ServiceB");
        uhuDiscoveredServiceTemp.service = new UhuServiceEndPoint(serviceId);
        uhuDiscoveredServiceTemp.service.device = "DeviceA";
        assertFalse("Different UhuDiscoveredServices are considered equal.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));

    }

}
