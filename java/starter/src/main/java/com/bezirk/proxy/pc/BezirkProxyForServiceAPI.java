/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Bezirk Proxy - sending side.
 * 
 */
package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkZirkId;
import com.bezirk.proxy.api.impl.SubscribedRole;

import java.io.File;


public interface BezirkProxyForServiceAPI {
    void registerService(BezirkZirkId serviceId, String serviceName);

    void subscribeService(BezirkZirkId serviceId, SubscribedRole pRole);

    void sendUnicastEvent(BezirkZirkId serviceId, BezirkZirkEndPoint recipient, String serializedEventMsg);

    void sendMulticastEvent(BezirkZirkId serviceId, Address address, String serializedEventMsg);

    void discover(BezirkZirkId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    short sendStream(BezirkZirkId sender, BezirkZirkEndPoint receiver, String serialsedString, File file, short streamId);

    short sendStream(BezirkZirkId sender, BezirkZirkEndPoint receiver, String serialsedString, short streamId);

    void setLocation(BezirkZirkId serviceId, Location location);

    boolean unsubscribe(BezirkZirkId serviceId, SubscribedRole role);

    void unregister(BezirkZirkId serviceId);
}
