package com.bosch.upa.uhu.sid.test;
import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


public class UhuServiceIdTest {
	HashSet<UhuServiceId> sidList = new HashSet<UhuServiceId>();
	HashSet<UhuServiceEndPoint> sedList = new HashSet<UhuServiceEndPoint>();

	@Test
	public void test() {
		UhuServiceId sid1 = new UhuServiceId("Ys1NcReyox:AreYouHotonAndroid");
		UhuServiceId sid2 = new UhuServiceId("Ys1NcReyox:AreYouHotonAndroid");
		assertEquals(sid1,sid2);
		UhuServiceEndPoint sed1 = new UhuServiceEndPoint(new String("192.168.160.65"),sid1);
		UhuServiceEndPoint sed2 = new UhuServiceEndPoint(new String("192.168.160.65"),sid2);
		assertEquals(sed1,sed2);

		//testing sidList
		sidList.add(sid1);
		assertTrue(sidList.contains(sid2)); //Since sid 1 and sid 2 are equal

		//testing sedList
		sedList.add(sed1);
		assertTrue(sedList.contains(sed2)); //Since sed 1 and sed 2 are equal

	}

}
