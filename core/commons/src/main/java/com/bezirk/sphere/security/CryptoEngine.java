package com.bezirk.sphere.security;

import com.bezirk.commons.BezirkId;
import com.bezirk.middleware.objects.SphereVitals;
import com.bezirk.persistence.SphereRegistry;
import com.bezirk.sphere.api.CryptoInternals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoEngine implements CryptoInternals {
    private static final Logger logger = LoggerFactory.getLogger(CryptoEngine.class);

    private static final String KEY_FACTORY_ALGORITHM = "DSA";
    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String KEY_FACTORY_ALGORITHM_PASS = "PBKDF2WithHmacSHA1";
    private static final String REGISTRY_ERROR = "registry is not initialized";
    // Encryption Zirk
    private final UPABlockCipherService sphereCipherService = new UPABlockCipherService();
    SphereRegistry registry = null;

    /* initialize the registry reference */
    public CryptoEngine(SphereRegistry registry) {
        if (registry == null) {
            logger.error(REGISTRY_ERROR);
        } else {
            this.registry = registry;
        }
    }

    /**
     * This method generates the keys for a sphere if the passed sphereId does not already have
     * assigned keys.
     *
     * @param sphereId sphereId for which keys need to be generated
     * @return <code>true</code> if the keys were generated and stored successfully
     */
    public final boolean generateKeys(String sphereId) {

        boolean success = false;

        if (registry == null) {
            logger.error(REGISTRY_ERROR);
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
                logger.debug("sphere keys successfully added for sphereId : " + sphereId);
                success = true;
            } catch (NoSuchAlgorithmException e) {
                logger.error("Problem adding sphere keys for sphereId : " + sphereId, e);
            }
        }
        return success;
    }

    /**
     * create secret key basedon passcode
     */
    public byte[] generateKey(String code) {

        try {
            // create salt string
            // note salt is generated from algorithm string.
            // if algorithm changes old passwords won't work
            String saltString = new BezirkId().getShortIdByName(KEY_FACTORY_ALGORITHM_PASS);
            byte[] salt = saltString.getBytes();

            PBEKeySpec password = new PBEKeySpec(code.toCharArray(), salt, 1000, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM_PASS);

            PBEKey key = (PBEKey) factory.generateSecret(password);
            // create key based on code
            SecretKey encKey = new SecretKeySpec(key.getEncoded(), "AES");
            return encKey.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Error generating key", e);
        }
        return null;
    }

    /**
     * generate key pair
     */
    private KeyPair getKeyPair() {
        KeyPair pair = null;
        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(KEY_FACTORY_ALGORITHM);

            SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);

            keyGen.initialize(1024, random);

            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Problem generating key pair", e);
        }
        return pair;
    }

    /**
     * This method generates the keys for a sphere if the passed sphereId does not already have
     * keys assigned. For the symmetric key the code is generated from short id of sphere id
     *
     * @param sphereId sphereId for which keys need to be generated
     * @return <code>true</code> if the keys were generated and stored successfully
     */
    public final boolean generateKeys(String sphereId, boolean fromSphereId) {

        boolean success = false;

        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return success;
        }

        if (sphereId != null && !registry.isKeymapExist(sphereId)) {
            final byte[] sphereKey;

            if (fromSphereId) { // create from sphere id
                // get short id from sphere id
                String hashId = new BezirkId().getShortIdByHash(sphereId);

                // create sphere key from pass code
                sphereKey = generateKey(hashId);
                // store it against hash Id so that we can check it when we
                // receive the message
                registry.putSphereKey(hashId, sphereKey, sphereId);
            } else { // or create random
                sphereKey = new UPABlockCipherService().generateNewKey(128).getEncoded();
            }
            KeyPair pair = getKeyPair();

            SphereKeys sKeys = new SphereKeys(sphereKey, pair);

            registry.putSphereKeys(sphereId, sKeys);

            logger.debug("sphere keys successfully added for sphereId : " + sphereId);

            success = true;

        }
        return success;
    }

    public final void addMemberKeys(String sphereId, SphereKeys sphereKeys) {
        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return;
        }
        logger.debug("CryptoEngine, Member keys being added for:" + sphereId);
        registry.putSphereKeys(sphereId, sphereKeys);
    }

    /**
     * Encrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     *
     * @param sphereId          sphereId of the sphere for which serializedContent needs to be
     *                          encrypted
     * @param serializedContent content to be encrypted
     * @return encrypted byte array if 1. <code>sphereId</code> is not <code>null</code> and has a
     * sphereKey associated with it and 2. <ccde>serializedContent</code> is not <code>null</code>,
     * returns <code>null</code> otherwise
     */
    public final byte[] encryptSphereContent(String sphereId, String serializedContent) {

        logger.debug("sphereId for encryption:" + sphereId + " content:" + serializedContent);

        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return null;
        }

        byte[] encryptedContent = null;

        if (validateSphere(sphereId) && validateSphereKey(sphereId) && serializedContent != null) {
            try {
                encryptedContent = sphereCipherService
                        .encrypt(serializedContent.getBytes(), registry.getSphereKeys(sphereId).getSphereKey())
                        .getBytes();
            } catch (Exception e) {
                logger.error("Error while encrypting the content", e);
            }
        } else if (validateHashKey(sphereId) && serializedContent != null) {
            try {
                encryptedContent = sphereCipherService
                        .encrypt(serializedContent.getBytes(), registry.getSphereHashKeys(sphereId).getHashKey())
                        .getBytes();
            } catch (Exception e) {
                logger.error("Error while encrypting the content", e);
            }
        }
        return encryptedContent;
    }

    /**
     * Decrypts the serializedContent with sphereKey associated with the
     * sphereId passed
     *
     * @param sphereId          sphereId of the sphere for which serializedContent needs to be
     *                          decrypted
     * @param serializedContent content to be decrypted
     * @return decrypted serialized content String if 1. <code>sphereId</code> is not
     * <code>null</code> and has a sphereKey associated with it and 2.
     * <code>serializedContent</code> is not <code>null</code>, <code>null</code> otherwise
     */
    public final String decryptSphereContent(String sphereId, byte[] serializedContent) {

        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return null;
        }

        String decryptedString = null;

        if (validateSphere(sphereId) && serializedContent != null) {
            if (validateSphereKey(sphereId)) {
                try {
                    decryptedString = new String(sphereCipherService
                            .decrypt(serializedContent, registry.getSphereKeys(sphereId).getSphereKey()).getBytes());
                } catch (Exception e) {
                    logger.error("Error while decrypting the content", e);
                }
            } else if (validateHashKey(sphereId) && serializedContent != null) {
                logger.info("hash id. processing decrypt for " + sphereId);
                try {
                    decryptedString = new String(sphereCipherService
                            .decrypt(serializedContent, registry.getSphereHashKeys(sphereId).getHashKey()).getBytes());
                } catch (Exception e) {
                    logger.error("Error while decrypting the content with hash id " + sphereId, e);
                }
            }
        }
        return decryptedString;
    }

    /**
     * Encrypts a stream into another stream. This method does NOT flush or close either stream
     * prior to returning - the caller must do so when they are finished with the streams.
     * For example:
     * <br>
     * <pre>
     *     {@code try {
     *         InputStream in = ...
     *         OutputStream out = ...
     *         bezirkSphere.encryptSphereContent(in, out, sphereId);
     *     } finally {
     *         if (in != null) {
     *             try {
     *                 in.close();
     *             } catch (IOException ioe1) { ... logger, trigger event, etc }
     *         }
     *         if (out != null) {
     *             try {
     *                 out.close();
     *             } catch (IOException ioe2) { ... logger, trigger event, etc }
     *         }
     *     }}
     * </pre>
     *
     * @param in       Input stream for incoming un-encrypted information
     * @param out      Output stream for outgoing encrypted information
     * @param sphereId sphereId of the sphere for which input stream needs to be
     *                 encrypted
     */
    public void encryptSphereContent(InputStream in, OutputStream out, String sphereId) {

        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return;
        }

        if (validateSphere(sphereId) && validateSphereKey(sphereId) && in != null && out != null) {
            try {
                sphereCipherService.encrypt(in, out, registry.getSphereKeys(sphereId).getSphereKey());
            } catch (Exception e) {
                logger.error("Error while encrypting the content", e);
            }
        }
    }

    /**
     * Decrypts a stream into another stream.  This method does NOT flush or close either stream
     * prior to returning - the caller must do so when they are finished with the streams.
     * For example:
     * <br>
     * <pre>
     *     {@code try {
     *         InputStream in = ...
     *         OutputStream out = ...
     *         bezirkSphere.decryptSphereContent(in, out, sphereId);
     *     } finally {
     *         if (in != null) {
     *             try {
     *                 in.close();
     *             } catch (IOException ioe1) { ... logger, trigger event, etc }
     *         }
     *         if (out != null) {
     *             try {
     *                 out.close();
     *             } catch (IOException ioe2) { ... logger, trigger event, etc }
     *         }
     *     }}
     * </pre>
     *
     * @param in       Input stream for incoming encrypted information
     * @param out      Output stream for outgoing decrypted information
     * @param sphereId sphereId of the sphere for which input stream needs to be
     *                 decrypted
     */
    public void decryptSphereContent(InputStream in, OutputStream out, String sphereId) {
        if (registry == null) {
            logger.error(REGISTRY_ERROR);
            return;
        }
        if (validateSphere(sphereId) && validateSphereKey(sphereId) && in != null && out != null) {
            try {
                sphereCipherService.decrypt(in, out, registry.getSphereKeys(sphereId).getSphereKey());
            } catch (Exception e) {
                logger.error("Error while decrypting the content", e);
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
            logger.error(REGISTRY_ERROR);
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
     * @param sphereId sphereId which needs to be validated
     * @return true: if the passed sphereId is not null, sphereId exists in the
     * sphereKeyMap and has a not-null sphere key for encryption
     */
    private boolean validateSphere(String sphereId) {
        if (sphereId != null && registry.isKeymapExist(sphereId)) {
            logger.debug("CryptoEngine, sphere validated:" + sphereId);
            return true;
        }
        return false;

    }

    /**
     * validates the sphere key associated with the sphere Id passed Requires
     * the sphereId passed to be validated
     *
     * @param sphereId sphereId for which sphere key needs to be validated
     * @return true: if the sphere key for the passed sphere id is valid false:
     * otherwise
     */
    private boolean validateSphereKey(String sphereId) {
        final SphereKeys sphereKeys = registry.getSphereKeys(sphereId);

        if (sphereKeys != null && sphereKeys.getSphereKey() != null && sphereKeys.getSphereKey().length != 0) {
            logger.debug("CryptoEngine, sphere key validated:" + sphereId);
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
        // logger.error("invalid hash key > " + sphereId);

        return false;
    }

}
