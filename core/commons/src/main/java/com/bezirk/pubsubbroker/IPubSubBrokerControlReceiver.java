package com.bezirk.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.proxy.api.impl.ZirkId;

import java.util.Set;

/**
 * Platform independent API's used by SADL for validating the responses by the control channel.
 */
public interface IPubSubBrokerControlReceiver {

    /**
     * Checks if the Stream is registered by the Zirk zirkId to streamTopic.
     *
     * @param streamTopic - Stream Topic of the stream Descriptor
     * @param serviceId   - ZirkId of the Zirk
     * @return true if registered, false otherwise.
     */
    public Boolean isStreamTopicRegistered(final String streamTopic, final ZirkId serviceId);

    /**
     * Returns all {@link BezirkDiscoveredZirk} that are subscribed to protocolRole.
     *
     * @param pRole    - Protocol Role of the Services
     * @param location - Location where the services should reside
     * @return if location is null, all the services subscribed to the protocolRole are returned,
     * <code>null</code> is returned if no services subscribe to the role or subscribe to the role
     * and not in the same location.
     */
    public Set<BezirkDiscoveredZirk> discoverZirks(final ProtocolRole pRole, final Location location);

    /**
     * Returns the Location of the Zirk.
     *
     * @param serviceId ZirkId of the Zirk whose location needs to be known
     * @return Location if exists, null if the zirk is not registered
     */
    public Location getLocationForService(final ZirkId serviceId);

}
