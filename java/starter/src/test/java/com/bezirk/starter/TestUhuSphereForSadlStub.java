package com.bezirk.starter;

import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestUhuSphereForSadlStub {
    com.bezirk.starter.UhuSphereForSadlStub uhuSphereForSadlStub = new com.bezirk.starter.UhuSphereForSadlStub();
    String testStr = "TestString";
    BezirkZirkId uhuServiceId = new BezirkZirkId("123");

    @Test
    public void testEncryptSphereContent() {
        assertNull("Encrypted value is non null when content is null.", uhuSphereForSadlStub.encryptSphereContent("123", null));
        assertNotNull("Encrypted value is null when content is non null.", uhuSphereForSadlStub.encryptSphereContent("123", "testSerializedStringContent"));

        assertTrue("Encrypted value not equal to string byte array. ", Arrays.equals(testStr.getBytes(), uhuSphereForSadlStub.encryptSphereContent("123", testStr)));
    }

    @Test
    public void testDecryptSphereContent() {
        byte[] encryptedContent = uhuSphereForSadlStub.encryptSphereContent("123", testStr);
        assertEquals("Decryption is not working fine", testStr, uhuSphereForSadlStub.decryptSphereContent("123", encryptedContent));
    }

    @Test
    public void testGetSphereMembership() {

        Iterable<String> spheres = uhuSphereForSadlStub.getSphereMembership(uhuServiceId);
        assertEquals("sphere name is not equal to default sphere.", "default sphere", spheres.iterator().next());
    }

    //This is for the unimplemented methods.
    @Test
    public void testEncryptDecryptSphereContent_unimplemented() {
        uhuSphereForSadlStub.encryptSphereContent(null, null, null);
        uhuSphereForSadlStub.decryptSphereContent(null, null, null);
        uhuSphereForSadlStub.processSphereDiscoveryRequest(null);
        assertNull("ServiceName is non null.", uhuSphereForSadlStub.getZirkName(uhuServiceId));
        assertNull("Device name is non null.", uhuSphereForSadlStub.getDeviceNameFromSphere("testDevId"));

    }

    @Test
    public void testIsServiceInSphere() {
        assertTrue("Zirk is not identified as a member zirk in sphere.", uhuSphereForSadlStub.isZirkInSphere(uhuServiceId, "123"));
    }

}
