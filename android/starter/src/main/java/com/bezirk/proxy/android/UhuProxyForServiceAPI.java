/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Bezirk Proxy - sending side.
 * 
 */
package com.bezirk.proxy.android;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.proxy.api.impl.SubscribedRole;
import com.bezirk.proxy.api.impl.UhuServiceEndPoint;
import com.bezirk.proxy.api.impl.UhuServiceId;


public interface UhuProxyForServiceAPI {
    public void registerService(UhuServiceId serviceId, String serviceName);

    public void subscribeService(UhuServiceId serviceId, SubscribedRole pRole);

    public void sendUnicastEvent(UhuServiceId serviceId, UhuServiceEndPoint recipient, String serializedEventMsg);

    public void sendMulticastEvent(UhuServiceId serviceId, Address address, String serializedEventMsg);

    public void discover(UhuServiceId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    public short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, String filePath, short streamId);

    public short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, short streamId);

    public void setLocation(UhuServiceId serviceId, Location location);

    public void unsubscribe(UhuServiceId serviceId, SubscribedRole role);

    public void unregister(UhuServiceId serviceId);

}
