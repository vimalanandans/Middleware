package com.bezirk.middleware.core.sphere.control.Objects;

import java.util.HashSet;

public class ServiceInformation {

    //set of sphereID's the zirk is a part of
    private HashSet<String> sphereSet;
    private ServiceVitals serviceVitals;

    public HashSet<String> getSphereSet() {
        return new HashSet<>(sphereSet);
    }

    public void setSphereSet(HashSet<String> sphereSet) {
        this.sphereSet = new HashSet<>(sphereSet);
    }

    public ServiceVitals getServiceVitals() {
        return serviceVitals;
    }

    public void setServiceVitals(ServiceVitals serviceVitals) {
        this.serviceVitals = serviceVitals;
    }
}
