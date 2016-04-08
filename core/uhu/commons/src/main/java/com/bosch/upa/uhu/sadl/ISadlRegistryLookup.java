package com.bosch.upa.uhu.sadl;

import java.util.Set;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


/**
 * Platform independent API's used by SADL for different Map look-up and validating the requests.
 */
public interface ISadlRegistryLookup {
	/**
	 * Checks if the service is registered with SADL.
	 * @param serviceId UhuServiceId of the Service that has to be checked
	 * @return true if successful, false otherwise
	 */
	public Boolean isServiceRegisterd(final UhuServiceId serviceId);
	
	/**
	 * Returns the Set of Registered Services.
	 * @return set of registered Services.
	 */
	public Set<UhuServiceId> getRegisteredServices();
}
