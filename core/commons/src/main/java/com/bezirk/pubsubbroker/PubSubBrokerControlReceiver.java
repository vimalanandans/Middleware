package com.bezirk.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.ZirkId;

import java.util.Set;

/**
 * Platform independent API's used by SADL for validating the responses by the control channel.
 */
public interface PubSubBrokerControlReceiver {

    /**
     * Checks if the StreamDescriptor is registered by the Zirk zirkId to streamTopic.
     *
     * @param streamTopic - StreamDescriptor Topic of the stream Descriptor
     * @param serviceId   - ZirkId of the Zirk
     * @return true if registered, false otherwise.
     */
    public Boolean isStreamTopicRegistered(final String streamTopic, final ZirkId serviceId);

    /**
     * Returns the Location of the Zirk.
     *
     * @param serviceId ZirkId of the Zirk whose location needs to be known
     * @return Location if exists, null if the zirk is not registered
     */
    public Location getLocationForService(final ZirkId serviceId);
}
