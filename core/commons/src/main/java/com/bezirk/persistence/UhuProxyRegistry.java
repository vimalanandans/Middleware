package com.bezirk.persistence;

import java.io.Serializable;
import java.util.HashMap;

public class UhuProxyRegistry implements Serializable {

    private final HashMap<String, String> serviceIdsMap = new HashMap<String, String>();

    public String getUhuServiceId(final String serviceName) {
        return serviceIdsMap.get(serviceName);
    }

    public void updateUhuServiceId(final String serviceName, final String serviceId) {
        serviceIdsMap.put(serviceName, serviceId);
    }

    public void deleteUhuServiceId(final String serviceName) {
        serviceIdsMap.remove(serviceName);
    }

    public void clearRegistry() {
        serviceIdsMap.clear();
    }
}
