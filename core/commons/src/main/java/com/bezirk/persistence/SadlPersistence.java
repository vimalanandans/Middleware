/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.pubsubbroker.PubSubBrokerRegistry;


/**
 * Interface for the SADL to load and persist the registry
 */
public interface SadlPersistence {
    /**
     * Persist the PubSubBrokerRegistry
     *
     * @throws Exception if persisting is not possible
     */
    public void persistSadlRegistry() throws Exception;

    /**
     * Loads the Sadl Registry
     *
     * @return Sadl Registry
     * @throws Exception if loading is not possible
     */
    public PubSubBrokerRegistry loadSadlRegistry() throws Exception;
}
