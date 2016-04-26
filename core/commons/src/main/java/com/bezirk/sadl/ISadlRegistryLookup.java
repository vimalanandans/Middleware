package com.bezirk.sadl;

import com.bezirk.proxy.api.impl.UhuZirkId;

import java.util.Set;


/**
 * Platform independent API's used by SADL for different Map look-up and validating the requests.
 */
public interface ISadlRegistryLookup {
    /**
     * Checks if the service is registered with SADL.
     *
     * @param serviceId UhuZirkId of the Service that has to be checked
     * @return true if successful, false otherwise
     */
    public Boolean isServiceRegisterd(final UhuZirkId serviceId);

    /**
     * Returns the Set of Registered Services.
     *
     * @return set of registered Services.
     */
    public Set<UhuZirkId> getRegisteredServices();
}
