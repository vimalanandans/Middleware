package com.bezirk.middleware.core.pubsubbroker;

import com.bezirk.middleware.core.actions.SendFileStreamAction;
import com.bezirk.middleware.core.actions.SendMulticastEventAction;
import com.bezirk.middleware.core.actions.UnicastEventAction;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

/**
 * Exposes the internal version of the Zirk API as implemented by the PubSubBroker for use
 * by API proxies.
 */
public interface PubSubBrokerZirkServicer {
    boolean registerZirk(final ZirkId zirkId, final String zirkName);

    boolean unregisterZirk(final ZirkId zirkId);

    boolean subscribe(final ZirkId zirkId, final MessageSet messageSet);

    boolean unsubscribe(final ZirkId zirkId, final MessageSet messageSet);

    boolean setLocation(final ZirkId zirkId, final Location location);

    boolean sendMulticastEvent(SendMulticastEventAction multicastEventAction);

    boolean sendUnicastEvent(UnicastEventAction unicastEventAction);

    short sendStream(SendFileStreamAction streamAction);
}
