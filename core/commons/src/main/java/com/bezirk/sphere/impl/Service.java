package com.bezirk.sphere.impl;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rishabh Gulati on 6/19/2014.
 */
public abstract class Service implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2130386016234942372L;
    // set of sphereID's the service is a part of
    private HashSet<String> sphereSet;
    private String serviceName;
    private String ownerDeviceId;

    public Service(String serviceName, String ownerDeviceId, HashSet<String> sphereSet) {
        this.serviceName = serviceName;
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
     * @param sphereSet
     *            the sphereSet to set
     */
    public void setSphereSet(HashSet<String> sphereSet) {
        this.sphereSet = sphereSet;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName
     *            the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the ownerDeviceId
     */
    public String getOwnerDeviceId() {
        return ownerDeviceId;
    }

    /**
     * @param ownerDeviceId
     *            the ownerDeviceId to set
     */
    public void setOwnerDeviceId(String ownerDeviceId) {
        this.ownerDeviceId = ownerDeviceId;
    }

    /**
     * Provides the information regarding a service which can be shared Ex. in
     * control messages
     * 
     * @return
     */
    public ServiceVitals getServiceVitals() {
        return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ownerDeviceId == null) ? 0 : ownerDeviceId.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
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
        Service other = (Service) obj;
        if (ownerDeviceId == null) {
            if (other.ownerDeviceId != null)
                return false;
        } else if (!ownerDeviceId.equals(other.ownerDeviceId))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        if (sphereSet == null) {
            if (other.sphereSet != null)
                return false;
        } else if (!sphereSet.equals(other.sphereSet))
            return false;
        return true;
    }

}
