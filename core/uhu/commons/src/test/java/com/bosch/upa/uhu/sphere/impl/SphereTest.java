package com.bosch.upa.uhu.sphere.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;
import com.bosch.upa.uhu.sphere.api.UhuSphereType;

public class SphereTest {
	

	@Test
	public void testSphere() {

		
		testSphereEquality();

		

	}

	private void testSphereEquality() {
		Sphere sphere = new Sphere();
		String deviceID = "DeviceA";
		String sphereName="HomeSphere";
		
		ArrayList<UhuServiceId> serviceIDList = new ArrayList<>();
		serviceIDList.add(new UhuServiceId("ServiceA"));
		serviceIDList.add(new UhuServiceId("ServiceB"));
		serviceIDList.add(new UhuServiceId("ServiceC"));

		LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices = new LinkedHashMap<String,ArrayList<UhuServiceId>>();
		deviceServices.put(deviceID, serviceIDList );
		
		
		HashSet<String> ownerDevices = new HashSet<>();
		ownerDevices.add("DeviceA");
		
		Sphere sphereTemp = new Sphere();

		assertTrue("Same Spheres are considered not equal.",sphere.equals(sphereTemp));
		
		sphere.setSphereType(UhuSphereType.UHU_SPHERE_TYPE_CAR);
		assertFalse("Different Spheres are considered equal.",sphere.equals(sphereTemp));
		assertFalse("Different Spheres are considered equal.",sphereTemp.equals(sphere));

		sphereTemp.setSphereType(UhuSphereType.UHU_SPHERE_TYPE_CAR);
		sphere.setSphereName(sphereName);
		assertFalse("Different Spheres are considered equal.",sphere.equals(sphereTemp));
		assertFalse("Different Spheres are considered equal.",sphereTemp.equals(sphere));

		sphereTemp.setSphereName(sphereName);
		sphere.ownerDevices = ownerDevices;
		assertFalse("Different Spheres are considered equal.",sphere.equals(sphereTemp));		
		assertFalse("Different Spheres are considered equal.",sphereTemp.equals(sphere));

		
		sphereTemp.ownerDevices =ownerDevices;
		sphere.deviceServices = deviceServices;
		assertFalse("Different Spheres are considered equal.",sphere.equals(sphereTemp));
		assertFalse("Different Spheres are considered equal.",sphereTemp.equals(sphere));
		assertNotEquals("Different Spheres have same hashcode.",sphere.hashCode(),sphereTemp.hashCode());

		sphereTemp.deviceServices=deviceServices;
		assertTrue("Similar Spheres are considered not equal.",sphere.equals(sphereTemp));
		assertEquals("Same Spheres have different hashcode.",sphere.hashCode(),sphereTemp.hashCode());
		
		UhuServiceId serviceId = new UhuServiceId("TestService");
		assertFalse("Remove service is processed.",sphere.removeService(serviceId ));
		
		assertTrue("Same Sphere is not considered equal to itself.",sphere.equals(sphere));
		
		assertFalse("Sphere is considered equal to null.",sphere.equals(null));
		
		assertFalse("Sphere is considered equal to serviceID.",sphere.equals(serviceId));
		
		sphereTemp.ownerDevices =null;
		sphereTemp.deviceServices =null;
		sphereTemp.sphereName=null;
		sphereTemp.sphereType=null;
		assertNotEquals("Different Spheres have same hashcode.",sphere.hashCode(),sphereTemp.hashCode());

		
		
				



	}

}
