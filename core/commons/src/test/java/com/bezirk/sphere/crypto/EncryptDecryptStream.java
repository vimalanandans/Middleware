/**
 *
 */
package com.bezirk.sphere.crypto;

import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.sphere.testUtilities.SphereTestUtility;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author rishabh
 */
public class EncryptDecryptStream {
    private static final Logger log = LoggerFactory.getLogger(EncryptDecryptStream.class);
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static CryptoEngine cryptoEngine;
    private static SphereTestUtility sphereTestUtility;
    private static String sphereId;
    private static String content = "Content to be encrypted";
    private String decryptedContent;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up EncryptDecryptStream TestCase *****");
        mockSetUp.setUPTestEnv();
        cryptoEngine = mockSetUp.cryptoEngine;
        sphereTestUtility = new SphereTestUtility(mockSetUp.sphereRegistryWrapper, mockSetUp.upaDevice);
        sphereId = sphereTestUtility.generateOwnerCombo();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        mockSetUp.destroyTestSetUp();
        log.info("***** Shutting down EncryptDecryptStream TestCase *****");

    }

    @Test
    public void testEncryptDecryptStream() {
        ByteArrayInputStream contentInStream = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream contentOutStream = new ByteArrayOutputStream();
        cryptoEngine.encryptSphereContent(contentInStream, contentOutStream, sphereId);
        try {
            contentInStream = new ByteArrayInputStream(contentOutStream.toByteArray());
            contentOutStream = new ByteArrayOutputStream();
            cryptoEngine.decryptSphereContent(contentInStream, contentOutStream, sphereId);

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            outStream.write(contentOutStream.toByteArray());
            decryptedContent = outStream.toString();
            assertEquals("Decrypted content not equal to the original message.", content, decryptedContent);
        } catch (IOException e) {
            fail("Error in checking encrypt/decrypt. " + e.getLocalizedMessage());
        }
    }

    @Test
    public void testDecryptStreamWithInvalidSphereId() {
        ByteArrayInputStream contentInStream = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream contentOutStream = new ByteArrayOutputStream();
        cryptoEngine.decryptSphereContent(contentInStream, contentOutStream, "Test");
        assertEquals("Bezirk decrypted contentStream with invalid spehreId", 0, contentOutStream.size());
    }

    @Test
    public void testDecryptStreamWithNullSphereId() {
        ByteArrayInputStream contentInStream = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream contentOutStream = new ByteArrayOutputStream();
        cryptoEngine.decryptSphereContent(contentInStream, contentOutStream, null);
        assertEquals("Bezirk decrypted contentStream without sphereId", 0, contentOutStream.size());
    }

    @Test
    public void testEncryptStreamWithInvalidSphereId() {
        ByteArrayInputStream contentInStream = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream contentOutStream = new ByteArrayOutputStream();
        cryptoEngine.encryptSphereContent(contentInStream, contentOutStream, "Test");
        assertEquals("Bezirk encrypted contentStream without sphereId", 0, contentOutStream.size());
    }

    @Test
    public void testEncryptStreamWithNullSphereId() {
        ByteArrayInputStream contentInStream = new ByteArrayInputStream(content.getBytes());
        ByteArrayOutputStream contentOutStream = new ByteArrayOutputStream();
        cryptoEngine.encryptSphereContent(contentInStream, contentOutStream, null);
        assertEquals("Bezirk encrypted contentStream without sphereId", 0, contentOutStream.size());
    }

}
