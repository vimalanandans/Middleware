package com.bezirk.sadl;

import com.bezirk.api.addressing.Location;
import com.bezirk.api.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuServiceId;

/**
 * Platform Independent API's for SADL that are used to manipulate the SADL Maps.
 */
public interface ISadlRegistry {
	/**
	 * Registers a UPA Service with UhuServiceId in SADL.
	 * @param serviceId UhuServiceId of the registering Service.
	 * @return true is successful, false otherwise.
	 */
	public Boolean registerService(final UhuServiceId serviceId);
	/**
	 * Subscribes the UhuService to SADL.
	 * @param serviceId UhuServiceId of the subscribing UPAService
	 * @param pRole SubscribedRole of the subscribing Service
	 * @return true if successful, false otherwise.
	 */
	public Boolean subscribeService(final UhuServiceId serviceId, final ProtocolRole pRole);
	/**
	 * Unsubscribes the Service from the SADL.
	 * @param serviceId UhuServiceId of the Unsubscribing Service
	 * @param role SubscribedRole of the Service
	 * @return true if successful, false otherwise
	 */
	public Boolean unsubscribe(final UhuServiceId serviceId, final ProtocolRole role);
	/**
	 * Un-Registers the Service from SADL.
	 * @param serviceId UhuServiceId of the UnRegistering Service
	 * @return true if successful, false otherwise
	 */
	public Boolean unregisterService(final UhuServiceId serviceId);
	/**
	 * Update the location of the UPA Service 
	 * @param serviceId UhuServiceId of the unregistering service
	 * @param location Location of the UhuService
	 * @return true if successful, false otherwise
	 */
	public Boolean setLocation(final UhuServiceId serviceId, final Location location);
}
