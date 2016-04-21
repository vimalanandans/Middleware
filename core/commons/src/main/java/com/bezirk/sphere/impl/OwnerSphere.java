/**
 *
 */
package com.bezirk.sphere.impl;

import com.bezirk.proxy.api.impl.UhuServiceId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * @author Rishabh Gulati
 */
public final class OwnerSphere extends Sphere implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8307802592890417699L;

    public OwnerSphere() {
        // FOR GSON
    }

    public OwnerSphere(String sphereName, String ownerDeviceId, String sphereType) {
        super();
        createSphere(sphereName, ownerDeviceId, sphereType);
    }

    /**
     * Creates the sphere with passed sphereName
     *
     * @param sphereName
     */
    private void createSphere(String sphereName, String ownerDeviceId, String sphereType) {
        HashSet<String> ownerDevices = new HashSet<String>();
        ownerDevices.add(ownerDeviceId);
        this.sphereName = sphereName;
        this.sphereType = sphereType;
        this.ownerDevices = ownerDevices;
        this.deviceServices = new LinkedHashMap<String, ArrayList<UhuServiceId>>();
    }

}
