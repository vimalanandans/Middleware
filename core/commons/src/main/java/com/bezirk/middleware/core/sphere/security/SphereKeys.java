package com.bezirk.middleware.core.sphere.security;

import java.io.Serializable;
import java.security.KeyPair;
import java.util.Arrays;

public final class SphereKeys implements Serializable {

    /**
     * The key used for encrypting all the sphere information
     */
    private final byte[] sphereKey;

    /**
     * The private key of sphere owner. Only possessed by sphere owner. Null for
     * sphere members.
     */
    private final byte[] ownerPrivateKeyBytes;

    /**
     * The public key of a sphere. Used for authenticating the owner of a
     * sphere.
     */
    private final byte[] ownerPublicKeyBytes;

    public SphereKeys() {
        // For Gson
        this.sphereKey = null;
        this.ownerPrivateKeyBytes = null;
        this.ownerPublicKeyBytes = null;
    }

    /**
     * Initializes the Object with all the keys passed
     *
     * @param sphereKey
     * @param ownerPrivateKeyBytes
     * @param ownerPublicKeyBytes
     */
    public SphereKeys(byte[] sphereKey, byte[] ownerPrivateKeyBytes, byte[] ownerPublicKeyBytes) {
        this.sphereKey = sphereKey == null ? null : sphereKey.clone();
        this.ownerPrivateKeyBytes = ownerPrivateKeyBytes == null ? null : ownerPrivateKeyBytes.clone();
        this.ownerPublicKeyBytes = ownerPublicKeyBytes == null ? null : ownerPublicKeyBytes.clone();
    }

    public SphereKeys(byte[] sphereKey, KeyPair pair) {
        this.sphereKey = sphereKey == null ? null : sphereKey.clone();
        this.ownerPrivateKeyBytes = pair.getPrivate().getEncoded();
        this.ownerPublicKeyBytes = pair.getPublic().getEncoded();
    }

    /**
     * Initializes the Object with all the keys passed [for member spheres, no
     * private key]
     *
     * @param sphereKey
     * @param ownerPublicKeyBytes
     */
    public SphereKeys(byte[] sphereKey, byte[] ownerPublicKeyBytes) {
        this.sphereKey = sphereKey == null ? null : sphereKey.clone();
        this.ownerPrivateKeyBytes = null;
        this.ownerPublicKeyBytes = ownerPublicKeyBytes == null ? null : ownerPublicKeyBytes.clone();
    }

    /**
     * @return the sphereKey
     */
    public final byte[] getSphereKey() {
        return sphereKey == null ? null : sphereKey.clone();
    }

    /**
     * @return the ownerPrivateKeyBytes
     */
    public final byte[] getOwnerPrivateKeyBytes() {
        return ownerPrivateKeyBytes == null ? null : ownerPrivateKeyBytes.clone();
    }

    /**
     * @return the ownerPublicKeyBytes
     */
    public final byte[] getOwnerPublicKeyBytes() {
        return ownerPublicKeyBytes == null ? null : ownerPublicKeyBytes.clone();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(ownerPrivateKeyBytes);
        result = prime * result + Arrays.hashCode(ownerPublicKeyBytes);
        result = prime * result + Arrays.hashCode(sphereKey);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SphereKeys other = (SphereKeys) obj;
        if (!Arrays.equals(ownerPrivateKeyBytes, other.ownerPrivateKeyBytes))
            return false;
        return Arrays.equals(ownerPublicKeyBytes, other.ownerPublicKeyBytes) && Arrays.equals(sphereKey, other.sphereKey);
    }

}
