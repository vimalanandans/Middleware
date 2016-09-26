package com.bezirk.middleware.core.datastorage;

import java.io.Serializable;
import java.util.HashMap;

public class ProxyRegistry implements Serializable {

    private static final long serialVersionUID = -7100050208684429860L;
    private final HashMap<String, String> serviceIdsMap = new HashMap<String, String>();

    public String getBezirkServiceId(final String serviceName) {
        return serviceIdsMap.get(serviceName);
    }

    public void updateBezirkZirkId(final String serviceName, final String serviceId) {
        serviceIdsMap.put(serviceName, serviceId);
    }

    public void deleteBezirkZirkId(final String serviceName) {
        serviceIdsMap.remove(serviceName);
    }

    public void clearRegistry() {
        serviceIdsMap.clear();
    }
}
