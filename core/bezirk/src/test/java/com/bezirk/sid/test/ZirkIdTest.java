package com.bezirk.sid.test;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;


public class ZirkIdTest {
    HashSet<ZirkId> sidList = new HashSet<ZirkId>();
    HashSet<BezirkZirkEndPoint> sedList = new HashSet<BezirkZirkEndPoint>();

    @Test
    public void test() {
        ZirkId sid1 = new ZirkId("Ys1NcReyox:AreYouHotonAndroid");
        ZirkId sid2 = new ZirkId("Ys1NcReyox:AreYouHotonAndroid");
        assertEquals(sid1, sid2);
        BezirkZirkEndPoint sed1 = new BezirkZirkEndPoint("192.168.160.65", sid1);
        BezirkZirkEndPoint sed2 = new BezirkZirkEndPoint("192.168.160.65", sid2);
        assertEquals(sed1, sed2);

        //testing sidList
        sidList.add(sid1);
        assertTrue(sidList.contains(sid2)); //Since sid 1 and sid 2 are equal

        //testing sedList
        sedList.add(sed1);
        assertTrue(sedList.contains(sed2)); //Since sed 1 and sed 2 are equal

    }

}
