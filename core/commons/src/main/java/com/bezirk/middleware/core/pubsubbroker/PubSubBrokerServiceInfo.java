package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.proxy.api.impl.ZirkId;

import java.util.Set;


/**
 * Platform independent API's used by PubSubBroker for different Map look-up and validating the requests.
 */
public interface PubSubBrokerServiceInfo {
    /**
     * Checks if the zirk is registered with PubSubBroker.
     *
     * @param zirkId ZirkId of the Zirk that has to be checked
     * @return true if successful, false otherwise
     */
    Boolean isZirkRegistered(final ZirkId zirkId);

    /**
     * Returns the Set of Registered Services.
     *
     * @return set of registered Services.
     */
    Set<ZirkId> getRegisteredZirks();
}
