package com.bezirk.pubsubbroker;

import com.bezirk.actions.SendMulticastEventAction;
import com.bezirk.actions.UnicastEventAction;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.messages.MessageSet;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.io.File;

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

    short sendStream(ZirkId senderId, BezirkZirkEndPoint receiver, String serializedString, File file);
}
