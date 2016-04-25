package com.bezirk.sphere.impl;

import java.io.Serializable;

/**
 * @author Rishab Gulati
 */
public class ServiceVitals implements Serializable {
    private static final long serialVersionUID = -1298410041179461450L;
    private final String serviceName;
    private final String ownerDeviceID;

    /**
     * @param serviceName
     * @param ownerDeviceID
     */
    public ServiceVitals(String serviceName, String ownerDeviceID) {
        super();
        this.serviceName = serviceName;
        this.ownerDeviceID = ownerDeviceID;
    }

    /**
     * @return the serviceName
     */
    public final String getServiceName() {
        return serviceName;
    }

    /**
     * @return the ownerDeviceID
     */
    public final String getOwnerDeviceID() {
        return ownerDeviceID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ownerDeviceID == null) ? 0 : ownerDeviceID.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
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
        ServiceVitals other = (ServiceVitals) obj;
        if (ownerDeviceID == null) {
            if (other.ownerDeviceID != null)
                return false;
        } else if (!ownerDeviceID.equals(other.ownerDeviceID))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        return true;
    }

}
