package com.bosch.upa.uhu.sphere.control.Objects;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rishabh Gulati on 6/19/2014.
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
