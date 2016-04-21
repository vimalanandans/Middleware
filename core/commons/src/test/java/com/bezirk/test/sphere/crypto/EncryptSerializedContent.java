/**
 *
 */
package com.bezirk.test.sphere.crypto;

import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.security.CryptoEngine;
import com.bezirk.sphere.security.SphereKeys;
import com.bezirk.sphere.security.UPABlockCipherService;
import com.bezirk.test.sphere.testUtilities.MockSetUpUtility;
import com.bezirk.util.TextCompressor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Rishabh Gulati
 */
public class EncryptSerializedContent {
    private static final Logger log = LoggerFactory
            .getLogger(EncryptSerializedContent.class);
    private static final MockSetUpUtility mockSetUp = new MockSetUpUtility();
    private static SphereRegistry registry;
    private static CryptoEngine cryptoEngine;
    private final UPABlockCipherService cipherService = new UPABlockCipherService();
    private final String KEY_FACTORY_ALGORITHM = "DSA";
    private final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private final String content = "Testing serialized content encryption!!!";
    private SphereKeys sKeys;
    private String sphereId;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        log.info("***** Setting up EncryptSerializedContent TestCase *****");
        mockSetUp.setUPTestEnv();
        registry = mockSetUp.registry;
        cryptoEngine = mockSetUp.cryptoEngine;
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        log.info("***** Shutting down EncryptSerializedContent TestCase *****");
        mockSetUp.destroyTestSetUp();
    }

    /**
     * decompress the byte[] to the String format.
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String decompress(byte[] str) {
        if (str == null || str.length == 0) {
            return str.toString();
        }
        String outStr = "";
        try {
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str));
            BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            String line;
            while ((line = bf.readLine()) != null) {
                outStr += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outStr;
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        sphereId = UUID.randomUUID().toString();
        KeyPairGenerator keyGen;
        SecureRandom random;
        try {
            keyGen = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
            random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
            keyGen.initialize(1024, random);
            KeyPair pair = keyGen.generateKeyPair();
            sKeys = new SphereKeys(new UPABlockCipherService().generateNewKey(
                    128).getEncoded(), pair.getPrivate().getEncoded(), pair
                    .getPublic().getEncoded());
            registry.putSphereKeys(sphereId, sKeys);
            //sphereKeyMap.put(sphereId, sKeys);

        } catch (NoSuchAlgorithmException e) {
            log.error("Exception while setting up the test case");
        }
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * This wil compress the msg data
     *
     * @param data
     * @return
     */
    private byte[] compressMsg(final String data) {
        byte[] temp = null;
        byte[] wireData = null;

        temp = data.getBytes();
        log.info("Before Compression Msg byte length : " + temp.length);
        long compStartTime = System.currentTimeMillis();
        wireData = TextCompressor.compress(temp);
        long compEndTime = System.currentTimeMillis();
        log.info("Compression Took " + (compEndTime - compStartTime) + " mili seconds");
        //After Compression Byte Length is
        log.info("After Compression Msg byte length : " + wireData.length);
        return wireData;
    }

    /**
     * Test whether the byte[] provided by
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * is valid by decrypting it with another instance of
     * {@link UPABlockCipherService}
     */
    @Test
    public final void testEncryptSphereContent() {
        byte[] actualEncryptedContent = cryptoEngine.encryptSphereContent(
                sphereId, content);
        String decryptedContent;
        try {
            decryptedContent = new String(cipherService.decrypt(
                    actualEncryptedContent, sKeys.getSphereKey()).getBytes());
            assertTrue(content.equals(decryptedContent));
        } catch (Exception e) {
            log.error("Error while encrypting the content" + e.getMessage());
        }

    }

    /**
     * Test whether the byte[] provided by
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * is valid by decrypting it with another instance of
     * {@link UPABlockCipherService}
     */
    //@Test
    public final void testEncryptSphereContentAsBytes() {
        final Logger log = LoggerFactory.getLogger(EncryptSerializedContent.class);


        byte[] compressedContent = compressMsg(content);
        String decompressedContent;

        log.info("compressed content >> " + compressedContent);

        byte[] actualEncryptedContent = cryptoEngine.encryptSphereContent(
                sphereId, new String(compressedContent));

        log.info("Encrypted content >> " + compressedContent);

        byte[] decryptedContent;
        try {

            decryptedContent = cipherService.decrypt(
                    actualEncryptedContent, sKeys.getSphereKey()).getBytes();

            decompressedContent = decompress(decryptedContent);

            log.info("Decrypted content >> " + decryptedContent);
            log.info("DeCompressed content >> " + decompressedContent);
            assertTrue(content.equals(decompressedContent));
            //assertFalse(content.equals(decompressedContent));
        } catch (Exception e) {
            log.error("Error while encrypting the content" + e.getMessage());
        }

    }

    /**
     * Test operation of
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * with null sphereId
     */
    @Test
    public final void testEncryptSphereContentSphereIdNull() {
        assertEquals(null, cryptoEngine.encryptSphereContent(null, content));
    }

    /**
     * Test operation of
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * with null serialized content
     */
    @Test
    public final void testEncryptSphereContentSerializedContentNull() {
        assertEquals(null, cryptoEngine.encryptSphereContent(sphereId, null));
    }

    /**
     * Test operation of
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * with null serialized content and sphereId
     */
    @Test
    public final void testEncryptSphereContentBothNull() {
        assertEquals(null, cryptoEngine.encryptSphereContent(null, null));
    }

    /**
     * Test operation of
     * {@link CryptoEngine#encryptSphereContent(java.lang.String, java.lang.String)}
     * with an entry of passed sphereId absent
     */
    @Test
    public final void testEncryptSphereContentAbsentSphereId() {
        String newSphereId = UUID.randomUUID().toString();
        assertEquals(null,
                cryptoEngine.encryptSphereContent(newSphereId, content));
    }

}
