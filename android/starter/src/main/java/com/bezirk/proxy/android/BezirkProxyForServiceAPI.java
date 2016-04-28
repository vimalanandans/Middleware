/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Bezirk Proxy - sending side.
 * 
 */
package com.bezirk.proxy.android;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import java.io.File;


public interface BezirkProxyForServiceAPI {
    public void registerService(BezirkZirkId serviceId, String serviceName);

    public void subscribeService(BezirkZirkId serviceId, SubscribedRole pRole);

    public void sendUnicastEvent(BezirkZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg);

    public void sendMulticastEvent(BezirkZirkId serviceId, Address address, String serializedEventMsg);

    public void discover(BezirkZirkId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    public short sendStream(BezirkZirkId sender, BezirkZirkEndPoint receiver, String serialsedString, File file, short streamId);

    public short sendStream(BezirkZirkId sender, BezirkZirkEndPoint receiver, String serialsedString, short streamId);

    public void setLocation(BezirkZirkId serviceId, Location location);

    public void unsubscribe(BezirkZirkId serviceId, SubscribedRole role);

    public void unregister(BezirkZirkId serviceId);

}
