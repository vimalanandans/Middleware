package com.bezirk.middleware.core.datastorage;

import com.bezirk.middleware.core.pubsubbroker.PubSubBrokerRegistry;

/**
 * Interface for the PubSubBroker to load and persist the registry
 */
public interface PubSubBrokerStorage {
    /**
     * Persist the PubSubBrokerRegistry
     *
     * @throws Exception if persisting is not possible
     */
    void persistPubSubBrokerRegistry() throws Exception;

    /**
     * Loads the PubSubBroker Registry
     *
     * @return PubSubBroker Registry
     * @throws Exception if loading is not possible
     */
    PubSubBrokerRegistry loadPubSubBrokerRegistry() throws Exception;
}
