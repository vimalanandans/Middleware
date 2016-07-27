package com.bezirk.pubsubbroker;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

/**
 * Platform Independent API's for Proxy server to control PubSubBroker regarding zirk related actions.
 * Any better name for PubSubBrokerServiceTrigger ?
 */
public interface PubSubBrokerServiceTrigger {
    /**
     * Registers a Zirk with ZirkId in PubSubBroker.
     *
     * @param zirkId ZirkId of the registering Zirk.
     * @return true is successful, false otherwise.
     */
    Boolean registerService(final ZirkId zirkId, final String zirkName);

    /**
     * Subscribes the BezirkService to PubSubBroker.
     *
     * @param zirkId     ZirkId of the subscribing UPAService
     * @param messageSet MessageSet of the subscribing Zirk
     * @return true if successful, false otherwise.
     */
    Boolean subscribeService(final ZirkId zirkId, final MessageSet messageSet);

    /**
     * Unsubscribes the Zirk from the PubSubBroker.
     *
     * @param zirkId     ZirkId of the unsubscribing Zirk
     * @param messageSet SubscribedRole of the Zirk
     * @return true if successful, false otherwise
     */
    Boolean unsubscribe(final ZirkId zirkId, final MessageSet messageSet);

    /**
     * Un-Registers the Zirk from PubSubBroker.
     *
     * @param zirkId ZirkId of the UnRegistering Zirk
     * @return true if successful, false otherwise
     */

    Boolean unregisterService(final ZirkId zirkId);

    /**
     * Update the location of the UPA Zirk
     *
     * @param zirkId   ZirkId of the unregistering zirk
     * @param location Location of the BezirkService
     * @return true if successful, false otherwise
     */
    Boolean setLocation(final ZirkId zirkId, final Location location);

    /**
     * Send multicast event
     */
    boolean sendMulticastEvent(ZirkId zirkId, RecipientSelector recipientSelector, String serializedEventMsg);

    boolean sendUnicastEvent(ZirkId zirkId, BezirkZirkEndPoint recipient, String serializedEventMsg);

    short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId);
}
