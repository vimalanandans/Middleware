package com.bezirk.starter;

import com.bezirk.proxy.api.impl.BezirkZirkId;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestBezirkSphereForSadlStub {
    BezirkSphereForSadlStub bezirkSphereForSadlStub = new BezirkSphereForSadlStub();
    String testStr = "TestString";
    BezirkZirkId uhuServiceId = new BezirkZirkId("123");

    @Test
    public void testEncryptSphereContent() {
        assertNull("Encrypted value is non null when content is null.", bezirkSphereForSadlStub.encryptSphereContent("123", null));
        assertNotNull("Encrypted value is null when content is non null.", bezirkSphereForSadlStub.encryptSphereContent("123", "testSerializedStringContent"));

        assertTrue("Encrypted value not equal to string byte array. ", Arrays.equals(testStr.getBytes(), bezirkSphereForSadlStub.encryptSphereContent("123", testStr)));
    }

    @Test
    public void testDecryptSphereContent() {
        byte[] encryptedContent = bezirkSphereForSadlStub.encryptSphereContent("123", testStr);
        assertEquals("Decryption is not working fine", testStr, bezirkSphereForSadlStub.decryptSphereContent("123", encryptedContent));
    }

    @Test
    public void testGetSphereMembership() {

        Iterable<String> spheres = bezirkSphereForSadlStub.getSphereMembership(uhuServiceId);
        assertEquals("sphere name is not equal to default sphere.", "default sphere", spheres.iterator().next());
    }

    //This is for the unimplemented methods.
    @Test
    public void testEncryptDecryptSphereContent_unimplemented() {
        bezirkSphereForSadlStub.encryptSphereContent(null, null, null);
        bezirkSphereForSadlStub.decryptSphereContent(null, null, null);
        bezirkSphereForSadlStub.processSphereDiscoveryRequest(null);
        assertNull("ServiceName is non null.", bezirkSphereForSadlStub.getZirkName(uhuServiceId));
        assertNull("Device name is non null.", bezirkSphereForSadlStub.getDeviceNameFromSphere("testDevId"));

    }

    @Test
    public void testIsServiceInSphere() {
        assertTrue("Zirk is not identified as a member zirk in sphere.", bezirkSphereForSadlStub.isZirkInSphere(uhuServiceId, "123"));
    }

}
