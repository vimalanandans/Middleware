package com.bezirk.discovery;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bezirk.api.objects.UhuSphereInfo;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;
import com.bezirk.sphere.api.UhuSphereType;

/**
 * @author ajc6kor
 *
 */
public class SphereDiscoveryRecordTest {

	@Test
	public void test() {

		String sphereId="TestSphere";
		long timeout=60000;
		int max = 23;
		SphereDiscoveryRecord sphereDiscoveryRecord = new SphereDiscoveryRecord(sphereId, timeout, max );
	
		assertNotNull("CreationTime is null.",sphereDiscoveryRecord.getCreationTime());
		assertEquals("DiscoveredSetSize is not 0 at start",0,sphereDiscoveryRecord.getDiscoveredSetSize());
		assertEquals("Max is not equal to the set value.",max,sphereDiscoveryRecord.getMax());
		assertEquals("Timeout is not equal to the set value.",timeout,sphereDiscoveryRecord.getTimeout());
		assertEquals("SphereId is not equal to the set value.",sphereId,sphereDiscoveryRecord.getSphereId());
		
		assertNotNull("SphereServices is null.",sphereDiscoveryRecord.getSphereServices());
		
		UhuServiceEndPoint uhuServiceEndPoint = new UhuServiceEndPoint(new UhuServiceId("ServiceA"));
		uhuServiceEndPoint.device ="DeviceA";
		UhuSphereInfo uhuSphereInfo = new UhuSphereInfo("CarSphere12", "CarSphere", UhuSphereType.UHU_SPHERE_TYPE_CAR, null, null);
		sphereDiscoveryRecord.updateSet(uhuSphereInfo, uhuServiceEndPoint);
		
		
		assertEquals("DiscoveredSetSize is not 1 after updation.",1,sphereDiscoveryRecord.getDiscoveredSetSize());

	}

}
