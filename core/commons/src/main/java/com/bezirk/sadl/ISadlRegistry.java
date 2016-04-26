package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuZirkId;

/**
 * Platform Independent API's for SADL that are used to manipulate the SADL Maps.
 */
public interface ISadlRegistry {
    /**
     * Registers a UPA Service with UhuZirkId in SADL.
     *
     * @param serviceId UhuZirkId of the registering Service.
     * @return true is successful, false otherwise.
     */
    public Boolean registerService(final UhuZirkId serviceId);

    /**
     * Subscribes the UhuService to SADL.
     *
     * @param serviceId UhuZirkId of the subscribing UPAService
     * @param pRole     SubscribedRole of the subscribing Service
     * @return true if successful, false otherwise.
     */
    public Boolean subscribeService(final UhuZirkId serviceId, final ProtocolRole pRole);

    /**
     * Unsubscribes the Service from the SADL.
     *
     * @param serviceId UhuZirkId of the Unsubscribing Service
     * @param role      SubscribedRole of the Service
     * @return true if successful, false otherwise
     */
    public Boolean unsubscribe(final UhuZirkId serviceId, final ProtocolRole role);

    /**
     * Un-Registers the Service from SADL.
     *
     * @param serviceId UhuZirkId of the UnRegistering Service
     * @return true if successful, false otherwise
     */
    public Boolean unregisterService(final UhuZirkId serviceId);

    /**
     * Update the location of the UPA Service
     *
     * @param serviceId UhuZirkId of the unregistering service
     * @param location  Location of the UhuService
     * @return true if successful, false otherwise
     */
    public Boolean setLocation(final UhuZirkId serviceId, final Location location);
}
