/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bosch.upa.uhu.persistence;
/**
 * Interface for the Sphere to load and store persistence
 */
public interface ISpherePersistence {
	/**
	 * Interface to persist the Sphere Registry
	 * @throws Exception if problem in persisting the data
	 */
	public void persistSphereRegistry() throws Exception;
	/**
	 * Interface to load the registry
	 * @return SphereRegistry from the persistence
	 * @throws Exception if problem in loading the SphereRegistry from persistence
	 */
	public SphereRegistry loadSphereRegistry() throws Exception;
}
