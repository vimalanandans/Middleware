/*
 * @description: This is the core interface for Bezirk Proxy - sending side.
 */
package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import java.io.File;


public interface BezirkProxyForServiceAPI {
    void registerService(ZirkId serviceId, String serviceName);

    void subscribeService(ZirkId serviceId, SubscribedRole pRole);

    void sendUnicastEvent(ZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg);

    void sendMulticastEvent(ZirkId serviceId, RecipientSelector recipientSelector, String serializedEventMsg);

    void discover(ZirkId service, RecipientSelector scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    short sendStream(ZirkId sender, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId);

    short sendStream(ZirkId sender, BezirkZirkEndPoint receiver, String serializedString, short streamId);

    void setLocation(ZirkId serviceId, Location location);

    boolean unsubscribe(ZirkId serviceId, SubscribedRole role);

    void unregister(ZirkId serviceId);
}
