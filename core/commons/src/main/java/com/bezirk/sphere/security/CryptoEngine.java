package com.bezirk.sphere.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bezirk.commons.UhuId;
import com.bezirk.api.objects.SphereVitals;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.ICryptoInternals;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoEngine implements ICryptoInternals {

    private static final Logger LOGGER = LoggerFactory.getLogger(CryptoEngine.class);

    // Encryption Service
    private final UPABlockCipherService sphereCipherService = new UPABlockCipherService();

    SphereRegistry registry = null;

    private static final String KEY_FACTORY_ALGORITHM = "DSA";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String KEY_FACTORY_ALGORITHM_PASS = "PBKDF2WithHmacSHA1";
    private static final String REGISTRY_ERROR = "registry is not initialized";
    /* initialize the registry reference */
    public CryptoEngine(SphereRegistry registry) {
        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
        } else {
            this.registry = registry;
        }
    }

    /**
     * This method generates the keys for a sphere if the passed sphereId is not
     * already present
     * 
     * @param sphereId
     *            sphereId for which keys need to be generated
     * @return true: if the keys were generated and stored successfully
     * 
     *         false otherwise
     * 
     */
    public final boolean generateKeys(String sphereId) {

        boolean success = false;

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return success;
        }

        if (sphereId != null && !registry.isKeymapExist(sphereId)) {
            try {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);
                SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
                keyGen.initialize(1024, random);
                KeyPair pair = keyGen.generateKeyPair();
                SphereKeys sKeys = new SphereKeys(new UPABlockCipherService().generateNewKey(128).getEncoded(),
                        pair.getPrivate().getEncoded(), pair.getPublic().getEncoded());
                registry.putSphereKeys(sphereId, sKeys);
                LOGGER.debug("Sphere keys successfully added for sphereId : " + sphereId);
                success = true;
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("Problem adding sphere keys for sphereId : " + sphereId, e);
            }
        }
        return success;
    }

    /** create secret key basedon passcode */
    public byte[] generateKey(String code) {

        try {
            // create salt string
            // note salt is generated from algorithm string.
            // if algorithm changes old passwords won't work
            String saltString = new UhuId().getShortIdByName(KEY_FACTORY_ALGORITHM_PASS);
            byte[] salt = saltString.getBytes();

            PBEKeySpec password = new PBEKeySpec(code.toCharArray(), salt, 1000, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM_PASS);

            PBEKey key = (PBEKey) factory.generateSecret(password);
            // create key based on code
            SecretKey encKey = new SecretKeySpec(key.getEncoded(), "AES");
            return encKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error generating key", e);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Error generating key", e);
        }
        return null;
    }

    /** generate key pair */
    private KeyPair getKeyPair() {
        KeyPair pair = null;
        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);

            SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);

            keyGen.initialize(1024, random);

            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Problem generating key pair", e);
        }
        return pair;
    }

    /**
     * This method generates the keys for a sphere if the passed sphereId is not
     * already present
     *
     * for the symmetric key the code is generated form short id of sphere id
     *
     * @param sphereId
     *            sphereId for which keys need to be generated
     * @return true: if the keys were generated and stored successfully
     *
     *         false otherwise
     *
     */
    public final boolean generateKeys(String sphereId, boolean fromSphereId) {

        boolean success = false;

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return success;
        }

        if (sphereId != null && !registry.isKeymapExist(sphereId)) {
            byte[] sphereKey = null;

            if (fromSphereId) { // create from sphere id
                // get short id from sphere id
                String hashId = new UhuId().getShortIdByHash(sphereId);

                // create sphere key from pass code
                sphereKey = generateKey(hashId);
                // store it against hash Id so that we can check it when we
                // receive the message
                registry.putSphereKey(hashId, sphereKey, sphereId);
            } else { // or create randam
                sphereKey = new UPABlockCipherService().generateNewKey(128).getEncoded();
            }
            KeyPair pair = getKeyPair();

            SphereKeys sKeys = new SphereKeys(sphereKey, pair);

            registry.putSphereKeys(sphereId, sKeys);

            LOGGER.debug("Sphere keys successfully added for sphereId : " + sphereId);

            success = true;

        }
        return success;
    }

    public final void addMemberKeys(String sphereId, SphereKeys sphereKeys) {
        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return;
        }
        LOGGER.debug("CryptoEngine, Member keys being added for:" + sphereId);
        registry.putSphereKeys(sphereId, sphereKeys);
    }

    /**
     * Encrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     * 
     * @param sphereId
     *            sphereId of the sphere for which serializedContent needs to be
     *            encrypted
     * @param serializedContent
     *            content to be encrypted
     * @return encrypted byte array if 1. sphereId is not null & has a sphereKey
     *         associated with it 2. serializedContent is not null
     * 
     *         null otherwise
     */
    public final byte[] encryptSphereContent(String sphereId, String serializedContent) {

        LOGGER.debug("sphereId for encryption:" + sphereId + " content:" + serializedContent);

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return null;
        }

        byte[] encryptedContent = null;

        if (validateSphere(sphereId) && validateSphereKey(sphereId) && serializedContent != null) {
            try {
                encryptedContent = sphereCipherService
                        .encrypt(serializedContent.getBytes(), registry.getSphereKeys(sphereId).getSphereKey())
                        .getBytes();
            } catch (Exception e) {
                LOGGER.error("Error while encrypting the content", e);
            }
        } else if (validateHashKey(sphereId) && serializedContent != null) {
            try {
                encryptedContent = sphereCipherService
                        .encrypt(serializedContent.getBytes(), registry.getSphereHashKeys(sphereId).getHashKey())
                        .getBytes();
            } catch (Exception e) {
                LOGGER.error("Error while encrypting the content", e);
            }
        }
        return encryptedContent;
    }

    /**
     * Decrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     * 
     * @param sphereId
     *            sphereId of the sphere for which serializedContent needs to be
     *            decrypted
     * @param serializedContent
     *            content to be decrypted
     * @return Decrypted serialized content String if 1. sphereId is not null &
     *         has a sphereKey associated with it 2. serializedContent is not
     *         null
     * 
     *         null otherwise
     */
    public final String decryptSphereContent(String sphereId, byte[] serializedContent) {

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return null;
        }

        String decryptedString = null;

        if (validateSphere(sphereId) && serializedContent != null) {
            if (validateSphereKey(sphereId)) {
                try {
                    decryptedString = new String(sphereCipherService
                            .decrypt(serializedContent, registry.getSphereKeys(sphereId).getSphereKey()).getBytes());
                } catch (Exception e) {
                    LOGGER.error("Error while decrypting the content", e);
                }
            } else if (validateHashKey(sphereId) && serializedContent != null) {
                LOGGER.info("hash id. processing decrept for " + sphereId);
                try {
                    decryptedString = new String(sphereCipherService
                            .decrypt(serializedContent, registry.getSphereHashKeys(sphereId).getHashKey()).getBytes());
                } catch (Exception e) {
                    LOGGER.error("Error while decrypting the content with hash id " + sphereId, e);
                }
            }
        }
        return decryptedString;
    }

    /**
     * Encrypts a stream into another stream
     * 
     * @param in
     *            Input stream for incoming un-encrypted information
     * @param out
     *            Output stream for outgoing encrypted information
     * @param sphereId
     *            sphereId of the sphere for which input stream needs to be
     *            encrypted
     * 
     *            <pre>
     * NOTE: This method does NOT flush or close either stream prior to returning - the caller must do so when they are finished with the streams. For example:
     * {@code try {
     *       InputStream in = ...
     *       OutputStream out = ...
     *       uhuSphere.encryptSphereContent(in, out, sphereId);
     *   } finally {
     *       if (in != null) {
     *           try {
     *               in.close();
     *           } catch (IOException ioe1) { ... log, trigger event, etc }
     *       }
     *       if (out != null) {
     *           try {
     *               out.close();
     *           } catch (IOException ioe2) { ... log, trigger event, etc }
     *       }
     *   }}
     *            </pre>
     */
    public void encryptSphereContent(InputStream in, OutputStream out, String sphereId) {

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return;
        }

        if (validateSphere(sphereId) && validateSphereKey(sphereId) && in != null && out != null) {
            try {
                sphereCipherService.encrypt(in, out, registry.getSphereKeys(sphereId).getSphereKey());
            } catch (Exception e) {
                LOGGER.error("Error while encrypting the content", e);
            }
        }
    }

    /**
     * Decrypts a stream into another stream
     * 
     * @param in
     *            Input stream for incoming encrypted information
     * @param out
     *            Output stream for outgoing decrypted information
     * @param sphereId
     *            sphereId of the sphere for which input stream needs to be
     *            decrypted
     * 
     *            <pre>
     * NOTE: This method does NOT flush or close either stream prior to returning - the caller must do so when they are finished with the streams. For example:	
     * {@code try {
     *       InputStream in = ...
     *       OutputStream out = ...
     *       uhuSphere.decryptSphereContent(in, out, sphereId);
     *   } finally {
     *       if (in != null) {
     *           try {
     *               in.close();
     *           } catch (IOException ioe1) { ... log, trigger event, etc }
     *       }
     *       if (out != null) {
     *           try {
     *               out.close();
     *           } catch (IOException ioe2) { ... log, trigger event, etc }
     *       }
     *   }}
     *            </pre>
     */
    public void decryptSphereContent(InputStream in, OutputStream out, String sphereId) {
        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return;
        }
        if (validateSphere(sphereId) && validateSphereKey(sphereId) && in != null && out != null) {
            try {
                sphereCipherService.decrypt(in, out, registry.getSphereKeys(sphereId).getSphereKey());
            } catch (Exception e) {
                LOGGER.error("Error while decrypting the content", e);
            }
        }
    }

    /**
     * Provides the key details which can be used for sharing a sphere with
     * other devices Eq. using QR code
     * 
     * @return
     */
    public SphereVitals getSphereVitals(String sphereId) {

        SphereVitals vitals = null;

        if (registry == null) {
            LOGGER.error(REGISTRY_ERROR);
            return null;
        }

        if (validateSphere(sphereId)) {
            SphereKeys keys = registry.getSphereKeys(sphereId);
            // create new copies to distribute
            // can use System.arraycopy as well
            byte[] sphereKey = Arrays.copyOf(keys.getSphereKey(), keys.getSphereKey().length);
            byte[] spherePublicKey = Arrays.copyOf(keys.getOwnerPublicKeyBytes(), keys.getOwnerPublicKeyBytes().length);
            vitals = new SphereVitals(sphereKey, spherePublicKey);
        }
        return vitals;
    }

    /**
     * validates the passed sphereId
     * 
     * @param sphereId
     *            sphereId which needs to be validated
     * @return true: if the passed sphereId is not null, sphereId exists in the
     *         sphereKeyMap and has a not-null sphere key for encryption
     */
    private boolean validateSphere(String sphereId) {
        if (sphereId != null && registry.isKeymapExist(sphereId)) {
            LOGGER.debug("CryptoEngine, Sphere validated:" + sphereId);
            return true;
        }
        return false;

    }

    /**
     * validates the sphere key associated with the sphere Id passed Requires
     * the sphereId passed to be validated
     * 
     * @param sphereId
     *            sphereId for which sphere key needs to be validated
     * @return true: if the sphere key for the passed sphere id is valid false:
     *         otherwise
     */
    private boolean validateSphereKey(String sphereId) {
        final SphereKeys sphereKeys = registry.getSphereKeys(sphereId);

        if (sphereKeys != null && sphereKeys.getSphereKey() != null && sphereKeys.getSphereKey().length != 0) {
            LOGGER.debug("CryptoEngine, Sphere key validated:" + sphereId);
            return true;
        }

        return false;
    }

    /**
     * check is it valid hash key
     */
    private boolean validateHashKey(String hashId) {
        SphereRegistry.HashKey key = registry.getSphereHashKeys(hashId);

        if (key != null && key.getHashKey() != null && key.getHashKey().length != 0) {
            return true;
        }
        // log.error("invalid hash key > " + sphereId);

        return false;
    }

}
