/**
 * 
 */
package com.bezirk.sphere.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;

import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author Rishabh Gulati
 * 
 */
public final class MemberSphere extends Sphere implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4338578155881822594L;
    /**
     * Used to identify if the sphere was a sharable sphere of another device
     * Sphere used for exploring services in that device[catch]
     * 
     * Can be used for future explorations or can also be used for deleting
     * sphere information after the temporary transaction is complete
     */
    private final boolean temporarySphere;

    /**
     * @param temporarySphere
     *            Set to true if the sphere is used temporarily in case of
     *            catching the services other wise set to false
     * 
     *            Makes sure a sphere with this field as true is not exposed to
     *            the UI and can also be used to evaporate the sphere once its
     *            use is completed
     */
    public MemberSphere(String sphereName, String sphereType, HashSet<String> ownerDevices,
            LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices, boolean temporarySphere) {
        super();
        this.temporarySphere = temporarySphere;
        createSphere(sphereName, sphereType, ownerDevices, deviceServices);
    }

    /**
     * Creates the sphere with passed sphereName
     * 
     * @param sphereName
     */
    private void createSphere(String sphereName, String sphereType, HashSet<String> ownerDevices,
            LinkedHashMap<String, ArrayList<UhuServiceId>> deviceServices) {
        this.sphereName = sphereName;
        this.sphereType = sphereType;
        this.ownerDevices = ownerDevices;
        this.deviceServices = deviceServices;
    }

    /**
     * @return the temporarySphere
     */
    public final boolean isTemporarySphere() {
        return temporarySphere;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (temporarySphere ? 1231 : 1237);
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
        MemberSphere other = (MemberSphere) obj;
        if (temporarySphere != other.temporarySphere)
            return false;
        return true;
    }

}
