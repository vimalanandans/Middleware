package com.bezirk.sadl;

import com.bezirk.proxy.api.impl.BezirkZirkId;

import java.util.Set;


/**
 * Platform independent API's used by SADL for different Map look-up and validating the requests.
 */
public interface ISadlRegistryLookup {
    /**
     * Checks if the zirk is registered with SADL.
     *
     * @param serviceId BezirkZirkId of the Zirk that has to be checked
     * @return true if successful, false otherwise
     */
    public Boolean isServiceRegistered(final BezirkZirkId serviceId);

    /**
     * Returns the Set of Registered Services.
     *
     * @return set of registered Services.
     */
    public Set<BezirkZirkId> getRegisteredServices();
}
