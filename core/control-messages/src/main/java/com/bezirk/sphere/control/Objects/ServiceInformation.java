package com.bezirk.sphere.control.Objects;

import java.util.HashSet;

/**
 * @author Rishab Gulati
 */
public class ServiceInformation {

    //set of sphereID's the service is a part of
    private HashSet<String> sphereSet;
    private ServiceVitals serviceVitals;

    public HashSet<String> getSphereSet() {
        return sphereSet;
    }

    public void setSphereSet(HashSet<String> sphereSet) {
        this.sphereSet = sphereSet;
    }

    public ServiceVitals getServiceVitals() {
        return serviceVitals;
    }

    public void setServiceVitals(ServiceVitals serviceVitals) {
        this.serviceVitals = serviceVitals;
    }
}
