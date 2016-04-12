/**
 * @author Vijet Badigannavar(bvijet@in.bosch.com)
 */
package com.bezirk.persistence;

import com.bezirk.sadl.SadlRegistry;


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
