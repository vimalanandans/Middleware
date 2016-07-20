package com.bezirk.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

/**
 * Platform Independent API's for Proxy server to control PubSubBroker regarding service related actions.
 *  Any better name for PubSubBrokerServiceTrigger ?
 */
public interface PubSubBrokerServiceTrigger {
    /**
     * Registers a Zirk with ZirkId in PubSubBroker.
     *
     * @param serviceId ZirkId of the registering Zirk.
     * @return true is successful, false otherwise.
     */
    Boolean registerService(final ZirkId serviceId, final String serviceName);

    /**
     * Subscribes the BezirkService to PubSubBroker.
     *
     * @param serviceId ZirkId of the subscribing UPAService
     * @param pRole     SubscribedRole of the subscribing Zirk
     * @return true if successful, false otherwise.
     */
    Boolean subscribeService(final ZirkId serviceId, final ProtocolRole pRole);

    /**
     * Unsubscribes the Zirk from the PubSubBroker.
     *
     * @param serviceId ZirkId of the unsubscribing Zirk
     * @param role      SubscribedRole of the Zirk
     * @return true if successful, false otherwise
     */
    Boolean unsubscribe(final ZirkId serviceId, final ProtocolRole role);

    /**
     * Un-Registers the Zirk from PubSubBroker.
     *
     * @param serviceId ZirkId of the UnRegistering Zirk
     * @return true if successful, false otherwise
     */

    Boolean unregisterService(final ZirkId serviceId);

    /**
     * Update the location of the UPA Zirk
     *
     * @param serviceId ZirkId of the unregistering zirk
     * @param location  Location of the BezirkService
     * @return true if successful, false otherwise
     */
    Boolean setLocation(final ZirkId serviceId, final Location location);

    /**
     * Send multicast event
     */
    boolean sendMulticastEvent(ZirkId serviceId, RecipientSelector recipientSelector, String serializedEventMsg, String topic);

    boolean sendUnicastEvent(ZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg, String topic);

    short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId);
}
