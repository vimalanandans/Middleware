/*
 * @author: Joao Sousa (CR/RTC3-NA)
 * 
 * @description: This is the core interface for Uhu Proxy - sending side.
 * 
 */
package com.bosch.upa.uhu.proxy.pc;

import com.bezirk.api.addressing.Address;
import com.bezirk.api.addressing.Location;
import com.bosch.upa.uhu.proxy.api.impl.SubscribedRole;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceEndPoint;
import com.bosch.upa.uhu.proxy.api.impl.UhuServiceId;


public interface UhuProxyForServiceAPI {
    void registerService(UhuServiceId serviceId, String serviceName);
    void subscribeService(UhuServiceId serviceId, SubscribedRole pRole);
    void sendUnicastEvent(UhuServiceId serviceId, UhuServiceEndPoint recipient,String serializedEventMsg);
    void sendMulticastEvent(UhuServiceId serviceId,Address address, String serializedEventMsg);
    void discover(UhuServiceId service, Address scope, SubscribedRole pRole, int discoveryId, long timeout, int maxDiscovered);
    short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, String filePath, short streamId);
    short sendStream(UhuServiceId sender, UhuServiceEndPoint receiver, String serialsedString, short streamId);
    void setLocation (UhuServiceId serviceId, Location location);
    void unsubscribe (UhuServiceId serviceId, SubscribedRole role);
    void unregister (UhuServiceId serviceId);
}
