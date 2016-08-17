package com.bezirk.sphere.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Rishabh Gulati
 */
public abstract class Zirk implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2130386016234942372L;
    // set of sphereID's the zirk is a part of
    private HashSet<String> sphereSet;
    private String zirkName;
    private String ownerDeviceId;

    public Zirk(String zirkName, String ownerDeviceId, HashSet<String> sphereSet) {
        this.zirkName = zirkName;
        this.ownerDeviceId = ownerDeviceId;
        this.sphereSet = sphereSet;
    }

    /**
     * @return the sphereSet
     */
    public Set<String> getSphereSet() {
        return sphereSet;
    }

    /**
     * @param sphereSet the sphereSet to set
     */
    public void setSphereSet(HashSet<String> sphereSet) {
        this.sphereSet = sphereSet;
    }

    /**
     * @return the zirkName
     */
    public String getZirkName() {
        return zirkName;
    }

    /**
     * @param zirkName the zirkName to set
     */
    public void setZirkName(String zirkName) {
        this.zirkName = zirkName;
    }

    /**
     * @return the ownerDeviceId
     */
    public String getOwnerDeviceId() {
        return ownerDeviceId;
    }

    /**
     * @param ownerDeviceId the ownerDeviceId to set
     */
    public void setOwnerDeviceId(String ownerDeviceId) {
        this.ownerDeviceId = ownerDeviceId;
    }

    /**
     * Provides the information regarding a zirk which can be shared Ex. in
     * control messages
     */
    public ServiceVitals getServiceVitals() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ownerDeviceId == null) ? 0 : ownerDeviceId.hashCode());
        result = prime * result + (( zirkName == null) ? 0 :  zirkName.hashCode());
        result = prime * result + ((sphereSet == null) ? 0 : sphereSet.hashCode());
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
        Zirk other = (Zirk) obj;
        if (ownerDeviceId == null) {
            if (other.ownerDeviceId != null)
                return false;
        } else if (!ownerDeviceId.equals(other.ownerDeviceId))
            return false;
        if ( zirkName == null) {
            if (other.zirkName != null)
                return false;
        } else if (! zirkName.equals(other.zirkName))
            return false;
        if (sphereSet == null) {
            if (other.sphereSet != null)
                return false;
        } else if (!sphereSet.equals(other.sphereSet))
            return false;
        return true;
    }

}
