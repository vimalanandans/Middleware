/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bosch.upa.uhu.persistence;

import com.bosch.upa.uhu.sadl.SadlRegistry;



/**
 * Interface for the SADL to load and persist the registry
 */
public interface ISadlPersistence {
	/**
	 * Persist the SadlRegistry
	 * @throws Exception if persisting is not possible
	 */
	public void persistSadlRegistry() throws Exception;
	/**
	 * Loads the Sadl Registry
	 * @return Sadl Registry
	 * @throws Exception if loading is not posible
	 */
	public SadlRegistry loadSadlRegistry() throws Exception;
}
