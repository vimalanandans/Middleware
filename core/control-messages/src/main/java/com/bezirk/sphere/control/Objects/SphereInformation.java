package com.bezirk.sphere.control.Objects;

import java.util.Arrays;

/**
 * Created by GUR1PI on 7/15/2014.
 */
public class SphereInformation {
    private String sphereName;
    private byte[] sphereKey;
    //private String creatorDeviceName;
    private String creatorDeviceId;

    /**
     * Used to identify if the sphere was a sharable sphere of another device
     * Sphere used for exploring services in that device
     * Can be used for future explorations or can also be used for deleting sphere information after the temporary transaction is complete
     */
    private boolean temporarySphere;

    /*
    * Currently only a single owner maintained
    * Can be extended to a list of owners
    * */
    //private transient PrivateKey ownerPrivateKey;
    //private transient PublicKey ownerPublicKey;

    private byte[] ownerPrivateKeyBytes;
    private byte[] ownerPublicKeyBytes;

    /*
    * true : owner of the sphere
    * false : member of the sphere
    * */
    private boolean owner;

//    public String getCreatorDeviceName() {
//        return creatorDeviceName;
//    }
//
//    public void setCreatorDeviceName(String creatorDeviceName) {
//        this.creatorDeviceName = creatorDeviceName;
//    }

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
