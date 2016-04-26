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
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezirk.proxy.api.impl.UhuZirkId;

import java.io.File;


public interface UhuProxyForServiceAPI {
    public void registerService(UhuZirkId serviceId, String serviceName);

    public void subscribeService(UhuZirkId serviceId, SubscribedRole pRole);

    public void sendUnicastEvent(UhuZirkId serviceId, UhuZirkEndPoint recipient, String serializedEventMsg);

    public void sendMulticastEvent(UhuZirkId serviceId, Address address, String serializedEventMsg);

    public void discover(UhuZirkId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);

    public short sendStream(UhuZirkId sender, UhuZirkEndPoint receiver, String serialsedString, File file, short streamId);

    public short sendStream(UhuZirkId sender, UhuZirkEndPoint receiver, String serialsedString, short streamId);

    public void setLocation(UhuZirkId serviceId, Location location);

    public void unsubscribe(UhuZirkId serviceId, SubscribedRole role);

    public void unregister(UhuZirkId serviceId);

}
