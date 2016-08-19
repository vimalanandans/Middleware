package com.bezirk.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.ZirkId;

/**
 * Platform independent API's used by PubSubBroker for validating the responses by the control channel.
 */
interface PubSubBrokerControlReceiver {

    /**
     * Checks if the StreamDescriptor is registered by the Zirk zirkId to streamTopic.
     *
     * @param streamName - StreamDescriptor name of the stream Descriptor
     * @param serviceId  - ZirkId of the Zirk
     * @return true if registered, false otherwise.
     */
    boolean isStreamTopicRegistered(final String streamName, final ZirkId serviceId);

    /**
     * Returns the Location of the Zirk.
     *
     * @param serviceId ZirkId of the Zirk whose location needs to be known
     * @return Location if exists, null if the zirk is not registered
     */
    Location getLocationForZirk(final ZirkId serviceId);
}
