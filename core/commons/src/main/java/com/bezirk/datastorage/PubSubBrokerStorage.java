/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.datastorage;

import com.bezirk.pubsubbroker.PubSubBrokerRegistry;


/**
 * Interface for the PubSubBroker to load and persist the registry
 */
public interface PubSubBrokerStorage {
    /**
     * Persist the PubSubBrokerRegistry
     *
     * @throws Exception if persisting is not possible
     */
    public void persistPubSubBrokerRegistry() throws Exception;

    /**
     * Loads the PubSubBroker Registry
     *
     * @return PubSubBroker Registry
     * @throws Exception if loading is not possible
     */
    public PubSubBrokerRegistry loadPubSubBrokerRegistry() throws Exception;
}
