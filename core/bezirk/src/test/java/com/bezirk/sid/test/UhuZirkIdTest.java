package com.bezirk.sid.test;

import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;


public class UhuZirkIdTest {
    HashSet<UhuZirkId> sidList = new HashSet<UhuZirkId>();
    HashSet<UhuZirkEndPoint> sedList = new HashSet<UhuZirkEndPoint>();

    @Test
    public void test() {
        UhuZirkId sid1 = new UhuZirkId("Ys1NcReyox:AreYouHotonAndroid");
        UhuZirkId sid2 = new UhuZirkId("Ys1NcReyox:AreYouHotonAndroid");
        assertEquals(sid1, sid2);
        UhuZirkEndPoint sed1 = new UhuZirkEndPoint(new String("192.168.160.65"), sid1);
        UhuZirkEndPoint sed2 = new UhuZirkEndPoint(new String("192.168.160.65"), sid2);
        assertEquals(sed1, sed2);

        //testing sidList
        sidList.add(sid1);
        assertTrue(sidList.contains(sid2)); //Since sid 1 and sid 2 are equal

        //testing sedList
        sedList.add(sed1);
        assertTrue(sedList.contains(sed2)); //Since sed 1 and sed 2 are equal

    }

}
