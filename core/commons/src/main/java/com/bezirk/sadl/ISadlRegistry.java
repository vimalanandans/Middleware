package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkId;

/**
 * Platform Independent API's for SADL that are used to manipulate the SADL Maps.
 */
public interface ISadlRegistry {
    /**
     * Registers a UPA Zirk with BezirkZirkId in SADL.
     *
     * @param serviceId BezirkZirkId of the registering Zirk.
     * @return true is successful, false otherwise.
     */
    public Boolean registerService(final BezirkZirkId serviceId);

    /**
     * Subscribes the UhuService to SADL.
     *
     * @param serviceId BezirkZirkId of the subscribing UPAService
     * @param pRole     SubscribedRole of the subscribing Zirk
     * @return true if successful, false otherwise.
     */
    public Boolean subscribeService(final BezirkZirkId serviceId, final ProtocolRole pRole);

    /**
     * Unsubscribes the Zirk from the SADL.
     *
     * @param serviceId BezirkZirkId of the Unsubscribing Zirk
     * @param role      SubscribedRole of the Zirk
     * @return true if successful, false otherwise
     */
    public Boolean unsubscribe(final BezirkZirkId serviceId, final ProtocolRole role);

    /**
     * Un-Registers the Zirk from SADL.
     *
     * @param serviceId BezirkZirkId of the UnRegistering Zirk
     * @return true if successful, false otherwise
     */
    public Boolean unregisterService(final BezirkZirkId serviceId);

    /**
     * Update the location of the UPA Zirk
     *
     * @param serviceId BezirkZirkId of the unregistering zirk
     * @param location  Location of the UhuService
     * @return true if successful, false otherwise
     */
    public Boolean setLocation(final BezirkZirkId serviceId, final Location location);
}
