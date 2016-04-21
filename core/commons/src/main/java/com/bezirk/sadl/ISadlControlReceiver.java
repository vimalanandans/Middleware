package com.bezirk.sadl;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.UhuDiscoveredService;
import com.bezirk.proxy.api.impl.UhuServiceId;

import java.util.Set;

/**
 * Platform independent API's used by SADL for validating the responses by the control channel.
 */
public interface ISadlControlReceiver {

    /**
     * Checks if the Stream is registered by the Service serviceId to streamTopic.
     *
     * @param streamTopic - Stream Topic of the stream Descriptor
     * @param serviceId   - UhuServiceId of the Service
     * @return true if registered, false otherwise.
     */
    public Boolean isStreamTopicRegistered(final String streamTopic, final UhuServiceId serviceId);

    /**
     * Returns all {@link UhuDiscoveredService} that are subscribed to pRole.
     *
     * @param pRole    - Protocol Role of the Services
     * @param location - Location where the services should reside
     * @return if location is null, all the services subscibed to the pRole are returned, null is returned if no services subscribe to the role or subscribe to the role and not in the same location.
     */
    public Set<UhuDiscoveredService> discoverServices(final ProtocolRole pRole, final Location location);

    /**
     * Returns the Location of the Service.
     *
     * @param serviceId UhuServiceId of the Service whose location needs to be known
     * @return Location if exists, null if the service is not registered
     */
    public Location getLocationForService(final UhuServiceId serviceId);

}
