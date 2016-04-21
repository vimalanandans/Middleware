/**
 *
 */
package com.bezirk.middleware.objects;

import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * @author Rishabh Gulati
 */
public class UhuServiceInfo {

    private final String serviceId;
    private final String serviceName;
    private final String serviceType;
    private final boolean visible; // true if service is visible
    private boolean active;

    /**
     * @param serviceId
     * @param serviceType
     * @param active
     * @param visible
     */
    @Deprecated
    public UhuServiceInfo(UhuServiceId serviceId, String serviceName, String serviceType, boolean active,
                          boolean visible) {
        this.serviceId = serviceId.getUhuServiceId();
        this.serviceType = serviceType;
        this.active = active;
        this.visible = visible;
        this.serviceName = serviceName;
    }

    public UhuServiceInfo(String serviceId, String serviceName, String serviceType, boolean active,
                          boolean visible) {
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.active = active;
        this.visible = visible;
        this.serviceName = serviceName;
    }

    /**
     * @return the serviceId
     */
    @Deprecated
    public final UhuServiceId getUhuServiceId() {
        return new UhuServiceId(serviceId);
    }

    public String getServiceId() {
        return serviceId;
    }

    public final String getServiceName() {
        return serviceName;
    }

    /**
     * @return the serviceType
     */
    public final String getServiceType() {
        return serviceType;
    }

    /**
     * @return the active
     */
    public final boolean isActive() {
        return active;
    }

    public void setActive(boolean status) {
        active = status;
    }

    /**
     * @return the visible
     */
    public final boolean isVisible() {
        return visible;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UhuServiceInfo [serviceId=" + serviceId + ",\nserviceName="
                + serviceName + ",\nserviceType=" + serviceType + ",\nactive="
                + active + ",\nvisible=" + visible + "]";
    }


}
