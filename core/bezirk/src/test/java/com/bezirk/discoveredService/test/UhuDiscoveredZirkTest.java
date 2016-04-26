package com.bezirk.discoveredService.test;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.UhuDiscoveredZirk;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * This testcase verifies the UhuDiscoveredZirk by setting the properties and retrieving them after deserialization.
 */
public class UhuDiscoveredZirkTest {
    private UhuZirkId sid1;
    private UhuZirkId sid2;
    private UhuZirkEndPoint sed1;
    private UhuZirkEndPoint sed2;
    private HashSet<UhuDiscoveredZirk> list = new HashSet<UhuDiscoveredZirk>();


    @Before
    public void beforeTest() {
        sid1 = new UhuZirkId("Ys1NcReyox:AreYouHotonAndroid");
        sid2 = new UhuZirkId("Ys1NcReyox:AreYouHotonAndroid");
        sed1 = new UhuZirkEndPoint(new String("192.168.160.65"), sid1);
        sed2 = new UhuZirkEndPoint(new String("192.168.160.65"), sid2);

    }

    @Test
    public void test() {
        UhuDiscoveredZirk ds1 = new UhuDiscoveredZirk(sed1, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        UhuDiscoveredZirk ds2 = new UhuDiscoveredZirk(sed2, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        assertEquals(ds1, ds2);
        if (!list.contains(ds1)) {
            list.add(ds1);
        }
        assertTrue(list.contains(ds2)); //Since ds1 and ds2 are equal

        UhuZirkId serviceId = new UhuZirkId("Service25");
        UhuZirkEndPoint serviceEndPoint = new UhuZirkEndPoint(serviceId);
        serviceEndPoint.device = "DeviceA";
        String serviceName = "ServiceA";
        String pRole = "TestProtocol";
        Location location = new Location("OFFICE1/BLOCK1/FLOOR1");
        UhuDiscoveredZirk uhuDiscoveredService = new UhuDiscoveredZirk(serviceEndPoint, serviceName, pRole, location);

        assertEquals("Location is not equal to the set value.", location, uhuDiscoveredService.getLocation());
        assertEquals("Protocol is not equal to the set value.", pRole, uhuDiscoveredService.getProtocol());
        assertEquals("ZirkEndPoint is not equal to the set value.", serviceEndPoint, uhuDiscoveredService.getZirkEndPoint());
        assertEquals("ServiceName is not equal to the set value.", serviceName, uhuDiscoveredService.getZirkName());

        UhuDiscoveredZirk uhuDiscoveredServiceTemp = null;
        assertFalse("UhuDiscoveredZirk is considered as equal to null.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));
        assertTrue("UhuDiscoveredZirk is not considered equal to itself.", uhuDiscoveredService.equals(uhuDiscoveredService));
        assertFalse("UhuDiscoveredZirk is considered as equal to serviceEndPoint.", uhuDiscoveredService.equals(serviceEndPoint));


        uhuDiscoveredServiceTemp = new UhuDiscoveredZirk(serviceEndPoint, serviceName, pRole, location);
        assertTrue("Similar UhuDiscoveredServices are not considered equal.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));
        serviceId = new UhuZirkId("ServiceB");
        uhuDiscoveredServiceTemp.service = new UhuZirkEndPoint(serviceId);
        uhuDiscoveredServiceTemp.service.device = "DeviceA";
        assertFalse("Different UhuDiscoveredServices are considered equal.", uhuDiscoveredService.equals(uhuDiscoveredServiceTemp));

    }

}
