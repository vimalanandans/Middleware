package com.bezirk.messagehandler;

import com.bezirk.proxy.api.impl.UhuZirkId;

/**
 * Pojo class for Discovery notifier containing all the fields that are necessary to give the notification to the ProxyForUhuLibrary.
 */
public final class DiscoveryIncomingMessage extends ServiceIncomingMessage {
    /**
     * List of discoveredServices serialized as String
     */
    public String discoveredList;
    /**
     * Unique discovered id.
     */
    public int discoveryId;

    public Boolean isSphereDiscovery;

    public DiscoveryIncomingMessage() {
        callbackDiscriminator = "DISCOVERY";
    }

    public DiscoveryIncomingMessage(UhuZirkId serviceId, String discoveredList, int discoveryId, Boolean isSphereDiscovery) {
        super();
        callbackDiscriminator = "DISCOVERY";
        recipient = serviceId;
        this.discoveredList = discoveredList;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = isSphereDiscovery;
    }


}
