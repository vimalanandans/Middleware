/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Uhu Proxy - sending side.
 * 
 */
package com.bezirk.proxy.pc;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;

import java.io.File;


public interface UhuProxyForServiceAPI {
    void registerService(UhuServiceId serviceId, String serviceName);

    void subscribeService(UhuServiceId serviceId, SubscribedRole pRole);

    void sendUnicastEvent(UhuServiceId serviceId, UhuServiceEndPoint recipient, String serializedEventMsg);

    void sendMulticastEvent(UhuServiceId serviceId, Address address, String serializedEventMsg);

    void discover(UhuServiceId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, File file, short streamId);

    short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, short streamId);

    void setLocation(UhuServiceId serviceId, Location location);

    void unsubscribe(UhuServiceId serviceId, SubscribedRole role);

    void unregister(UhuServiceId serviceId);
}
