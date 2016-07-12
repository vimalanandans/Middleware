/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Bezirk Proxy - sending side.
 * 
 */
package com.bezirk.proxy.android;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import java.io.File;


public interface BezirkProxyForServiceAPI {
    public void registerService(ZirkId serviceId, String serviceName);

    public void subscribeService(ZirkId serviceId, SubscribedRole pRole);

    public void sendUnicastEvent(ZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg, String topic);

    public void sendMulticastEvent(ZirkId serviceId, RecipientSelector recipientSelector, String serializedEventMsg, String topic);

    public void discover(ZirkId service, RecipientSelector scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    public short sendStream(ZirkId sender, BezirkZirkEndPoint receiver, String serializedString, File file, short streamId);

    public short sendStream(ZirkId sender, BezirkZirkEndPoint receiver, String serializedString, short streamId);

    public void setLocation(ZirkId serviceId, Location location);

    public void unsubscribe(ZirkId serviceId, SubscribedRole role);

    public void unregister(ZirkId serviceId);

}
