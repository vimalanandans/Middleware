package com.bezirk.discoveredzirk.test;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * This testcase verifies the BezirkDiscoveredZirk by setting the properties and retrieving them after deserialization.
 */
public class BezirkDiscoveredZirkTest {
    private BezirkZirkId sid1;
    private BezirkZirkId sid2;
    private BezirkZirkEndPoint sed1;
    private BezirkZirkEndPoint sed2;
    private HashSet<BezirkDiscoveredZirk> list = new HashSet<BezirkDiscoveredZirk>();


    @Before
    public void beforeTest() {
        sid1 = new BezirkZirkId("Ys1NcReyox:AreYouHotonAndroid");
        sid2 = new BezirkZirkId("Ys1NcReyox:AreYouHotonAndroid");
        sed1 = new BezirkZirkEndPoint(new String("192.168.160.65"), sid1);
        sed2 = new BezirkZirkEndPoint(new String("192.168.160.65"), sid2);

    }

    @Test
    public void test() {
        BezirkDiscoveredZirk ds1 = new BezirkDiscoveredZirk(sed1, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        BezirkDiscoveredZirk ds2 = new BezirkDiscoveredZirk(sed2, new String("AreYouHotonAndroid"), new String("UserInput"), new Location(null));
        assertEquals(ds1, ds2);
        if (!list.contains(ds1)) {
            list.add(ds1);
        }
        assertTrue(list.contains(ds2)); //Since ds1 and ds2 are equal

        BezirkZirkId zirkId = new BezirkZirkId("Zirk25");
        BezirkZirkEndPoint zirkEndPoint = new BezirkZirkEndPoint(zirkId);
        zirkEndPoint.device = "DeviceA";
        String zirkName = "ZirkA";
        String pRole = "TestProtocol";
        Location location = new Location("OFFICE1/BLOCK1/FLOOR1");
        BezirkDiscoveredZirk bezirkDiscoveredZirk = new BezirkDiscoveredZirk(zirkEndPoint, zirkName, pRole, location);

        assertEquals("Location is not equal to the set value.", location, bezirkDiscoveredZirk.getLocation());
        assertEquals("Protocol is not equal to the set value.", pRole, bezirkDiscoveredZirk.getProtocol());
        assertEquals("ZirkEndPoint is not equal to the set value.", zirkEndPoint, bezirkDiscoveredZirk.getZirkEndPoint());
        assertEquals("ZirkName is not equal to the set value.", zirkName, bezirkDiscoveredZirk.getZirkName());

        BezirkDiscoveredZirk bezirkDiscoveredZirkTemp = null;
        assertFalse("BezirkDiscoveredZirk is considered as equal to null.", bezirkDiscoveredZirk.equals(bezirkDiscoveredZirkTemp));
        assertTrue("BezirkDiscoveredZirk is not considered equal to itself.", bezirkDiscoveredZirk.equals(bezirkDiscoveredZirk));
        assertFalse("BezirkDiscoveredZirk is considered as equal to zirkEndPoint.", bezirkDiscoveredZirk.equals(zirkEndPoint));


        bezirkDiscoveredZirkTemp = new BezirkDiscoveredZirk(zirkEndPoint, zirkName, pRole, location);
        assertTrue("Similar BezirkDiscoveredZirks are not considered equal.", bezirkDiscoveredZirk.equals(bezirkDiscoveredZirkTemp));
        zirkId = new BezirkZirkId("ZirkB");
        bezirkDiscoveredZirkTemp.zirk = new BezirkZirkEndPoint(zirkId);
        bezirkDiscoveredZirkTemp.zirk.device = "DeviceA";
        assertFalse("Different BezirkDiscoveredZirks are considered equal.", bezirkDiscoveredZirk.equals(bezirkDiscoveredZirkTemp));

    }

}
