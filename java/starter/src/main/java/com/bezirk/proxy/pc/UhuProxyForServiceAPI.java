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
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import java.io.File;


public interface UhuProxyForServiceAPI {
    void registerService(UhuZirkId serviceId, String serviceName);

    void subscribeService(UhuZirkId serviceId, SubscribedRole pRole);

    void sendUnicastEvent(UhuZirkId serviceId, UhuZirkEndPoint recipient, String serializedEventMsg);

    void sendMulticastEvent(UhuZirkId serviceId, Address address, String serializedEventMsg);

    void discover(UhuZirkId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    short sendStream(UhuZirkId sender, UhuZirkEndPoint receiver, String serialsedString, File file, short streamId);

    short sendStream(UhuZirkId sender, UhuZirkEndPoint receiver, String serialsedString, short streamId);

    void setLocation(UhuZirkId serviceId, Location location);

    void unsubscribe(UhuZirkId serviceId, SubscribedRole role);

    void unregister(UhuZirkId serviceId);
}
