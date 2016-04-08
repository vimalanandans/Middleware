package com.bosch.upa.uhu.api.objects;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *	 This testcase verifies the SphereVitals by setting the properties and retrieving them.
 * 
 * @author AJC6KOR
 *
 */
public class SphereVitalsTest {

	@Test
	public void test() {

		byte[] sphereKey="tdgdiknvx".getBytes();
		byte[] publicKey="oihyurgbd".getBytes();
		SphereVitals sphereVitals = new SphereVitals(sphereKey, publicKey);
		
		assertArrayEquals("SphereKey is not equal to the set value.",sphereKey,sphereVitals.getSphereKey());
		assertArrayEquals("PublicKey is not equal to the set value.",publicKey,sphereVitals.getPublicKey());
		
	}

}
