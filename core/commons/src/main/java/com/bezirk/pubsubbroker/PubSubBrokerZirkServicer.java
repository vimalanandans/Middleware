package com.bezirk.pubsubbroker;

import com.bezirk.actions.SendFileStreamAction;
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

    boolean sendMulticastEvent(ZirkId zirkId, RecipientSelector recipientSelector, String serializedEventMsg, String eventName);

    boolean sendUnicastEvent(ZirkId zirkId, BezirkZirkEndPoint recipient, String serializedEventMsg, String eventName);

    short sendStream(SendFileStreamAction streamAction);
}
