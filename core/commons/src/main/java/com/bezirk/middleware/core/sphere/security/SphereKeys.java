/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
     */
    public SphereKeys(byte[] sphereKey, byte[] ownerPublicKeyBytes) {
        this.sphereKey = sphereKey == null ? null : sphereKey.clone();
        this.ownerPrivateKeyBytes = null;
        this.ownerPublicKeyBytes = ownerPublicKeyBytes == null ? null : ownerPublicKeyBytes.clone();
    }

    /**
     * @return the sphereKey
     */
    public byte[] getSphereKey() {
        return sphereKey == null ? null : sphereKey.clone();
    }

    /**
     * @return the ownerPrivateKeyBytes
     */
    public byte[] getOwnerPrivateKeyBytes() {
        return ownerPrivateKeyBytes == null ? null : ownerPrivateKeyBytes.clone();
    }

    /**
     * @return the ownerPublicKeyBytes
     */
    public byte[] getOwnerPublicKeyBytes() {
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
