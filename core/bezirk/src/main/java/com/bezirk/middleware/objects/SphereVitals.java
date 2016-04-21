/**
 *
 */
package com.bezirk.middleware.objects;

/**
 * @author Rishabh Gulati
 */
public final class SphereVitals {

    private final byte[] sphereKey;
    private final byte[] publicKey;

    /**
     * @param sphereKey
     * @param publicKey
     */
    public SphereVitals(byte[] sphereKey, byte[] publicKey) {
        super();
        this.sphereKey = sphereKey == null ? null : sphereKey.clone();
        this.publicKey = publicKey == null ? null : publicKey.clone();
    }

    /**
     * @return the sphereKey
     */
    public final byte[] getSphereKey() {
        return sphereKey == null ? null : sphereKey.clone();
    }

    /**
     * @return the publicKey
     */
    public final byte[] getPublicKey() {
        return publicKey == null ? null : publicKey.clone();
    }


}
