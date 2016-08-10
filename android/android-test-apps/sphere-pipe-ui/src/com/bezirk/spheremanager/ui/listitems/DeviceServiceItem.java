package com.bezirk.spheremanager.ui.listitems;

public class DeviceServiceItem {
    private String serviceName;
    private boolean isActive;

    public DeviceServiceItem(String serviceName, boolean isActive) {
        this.serviceName = serviceName;
        this.isActive = isActive;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

}
