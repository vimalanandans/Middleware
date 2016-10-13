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
package com.bezirk.middleware.core.sphere.control.Objects;

import java.util.Arrays;

public class SphereInformation {
    private String sphereName;
    private byte[] sphereKey;
    private String creatorDeviceId;

    /**
     * Used to identify if the sphere was a sharable sphere of another device
     * sphere used for exploring services in that device
     * Can be used for future explorations or can also be used for deleting sphere information after
     * the temporary transaction is complete
     */
    private boolean temporarySphere;

    private byte[] ownerPrivateKeyBytes;
    private byte[] ownerPublicKeyBytes;

    /*
    * true : owner of the sphere
    * false : member of the sphere
    * */
    private boolean owner;

    public boolean isTemporarySphere() {
        return temporarySphere;
    }

    public void setTemporarySphere(boolean temporarySphere) {
        this.temporarySphere = temporarySphere;
    }

    public String getSphereName() {
        return sphereName;
    }

    public void setSphereName(String sphereName) {
        this.sphereName = sphereName;
    }

    public byte[] getSphereKey() {
        return sphereKey == null ? null : sphereKey.clone();
    }

    public void setSphereKey(byte[] sphereKey) {
        if (sphereKey != null) {
            this.sphereKey = Arrays.copyOf(sphereKey, sphereKey.length);
        }
    }

    public byte[] getOwnerPrivateKeyBytes() {
        return ownerPrivateKeyBytes == null ? null : ownerPrivateKeyBytes.clone();
    }

    public void setOwnerPrivateKeyBytes(byte[] ownerPrivateKeyBytes) {
        if (ownerPrivateKeyBytes != null) {
            this.ownerPrivateKeyBytes = Arrays.copyOf(ownerPrivateKeyBytes, ownerPrivateKeyBytes.length);
        }
    }

    public byte[] getOwnerPublicKeyBytes() {
        return ownerPublicKeyBytes == null ? null : ownerPublicKeyBytes.clone();
    }

    public void setOwnerPublicKeyBytes(byte[] ownerPublicKeyBytes) {
        if (ownerPublicKeyBytes != null) {
            this.ownerPublicKeyBytes = Arrays.copyOf(ownerPublicKeyBytes, ownerPublicKeyBytes.length);
        }
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public String getCreatorDeviceId() {
        return creatorDeviceId;
    }

    public void setCreatorDeviceId(String creatorDeviceId) {
        this.creatorDeviceId = creatorDeviceId;
    }
}
