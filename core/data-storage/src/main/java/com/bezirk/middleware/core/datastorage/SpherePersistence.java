/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.middleware.core.datastorage;

/**
 * Interface for the sphere to load and store persistence
 */
public interface SpherePersistence {
    /**
     * Interface to persist the sphere Registry
     *
     * @throws Exception if problem in persisting the data
     */
    void persistSphereRegistry() throws Exception;

    /**
     * Interface to load the registry
     *
     * @return SphereRegistry from the persistence
     * @throws Exception if problem in loading the SphereRegistry from persistence
     */
    SphereRegistry loadSphereRegistry() throws Exception;
}
