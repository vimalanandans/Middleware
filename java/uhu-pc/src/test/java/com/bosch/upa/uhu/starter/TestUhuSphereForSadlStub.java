package com.bosch.upa.uhu.starter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;

public class TestUhuSphereForSadlStub {
	UhuSphereForSadlStub uhuSphereForSadlStub = new UhuSphereForSadlStub();
	String testStr = "TestString";
	UhuServiceId uhuServiceId = new UhuServiceId("123");
	@Test
	public void testEncryptSphereContent() {
		assertNull("Encrypted value is non null when content is null.",uhuSphereForSadlStub.encryptSphereContent("123", null));
		assertNotNull("Encrypted value is null when content is non null.",uhuSphereForSadlStub.encryptSphereContent("123", "testSerializedStringContent"));
		
		assertTrue("Encrypted value not equal to string byte array. ",Arrays.equals(testStr .getBytes(), uhuSphereForSadlStub.encryptSphereContent("123", testStr)));
	}
	@Test
	public void testDecryptSphereContent() {
		byte[] encryptedContent = uhuSphereForSadlStub.encryptSphereContent("123", testStr);
		assertEquals("Decryption is not working fine", testStr, uhuSphereForSadlStub.decryptSphereContent("123", encryptedContent));
	}
	
	@Test
	public void testGetSphereMembership(){
		
		Iterable<String> spheres = uhuSphereForSadlStub.getSphereMembership(uhuServiceId);
		assertEquals("Sphere name is not equal to default sphere.","default sphere", spheres.iterator().next());
	}
	
	//This is for the unimplemented methods.
	@Test
	public void testEncryptDecryptSphereContent_unimplemented(){
		uhuSphereForSadlStub.encryptSphereContent(null, null, null);
		uhuSphereForSadlStub.decryptSphereContent(null, null, null);
		uhuSphereForSadlStub.processSphereDiscoveryRequest(null);
		assertNull("ServiceName is non null.",uhuSphereForSadlStub.getServiceName(uhuServiceId));
		assertNull("Device name is non null.",uhuSphereForSadlStub.getDeviceNameFromSphere("testDevId"));
	
	}
	@Test
	public void testIsServiceInSphere(){
		assertTrue("Service is not identified as a member service in sphere.",uhuSphereForSadlStub.isServiceInSphere(uhuServiceId, "123"));
	}

}
