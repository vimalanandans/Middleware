/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.pubsubbroker.SadlRegistry;


/**
 * Interface for the SADL to load and persist the registry
 */
public interface SadlPersistence {
    /**
     * Persist the SadlRegistry
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
    public SadlRegistry loadSadlRegistry() throws Exception;
}
