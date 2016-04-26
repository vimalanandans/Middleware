package com.bezirk.sid.test;

import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;


public class BezirkZirkIdTest {
    HashSet<BezirkZirkId> sidList = new HashSet<BezirkZirkId>();
    HashSet<BezirkZirkEndPoint> sedList = new HashSet<BezirkZirkEndPoint>();

    @Test
    public void test() {
        BezirkZirkId sid1 = new BezirkZirkId("Ys1NcReyox:AreYouHotonAndroid");
        BezirkZirkId sid2 = new BezirkZirkId("Ys1NcReyox:AreYouHotonAndroid");
        assertEquals(sid1, sid2);
        BezirkZirkEndPoint sed1 = new BezirkZirkEndPoint(new String("192.168.160.65"), sid1);
        BezirkZirkEndPoint sed2 = new BezirkZirkEndPoint(new String("192.168.160.65"), sid2);
        assertEquals(sed1, sed2);

        //testing sidList
        sidList.add(sid1);
        assertTrue(sidList.contains(sid2)); //Since sid 1 and sid 2 are equal

        //testing sedList
        sedList.add(sed1);
        assertTrue(sedList.contains(sed2)); //Since sed 1 and sed 2 are equal

    }

}
