package com.bezirk.proxy.messagehandler;

import com.bezirk.actions.BezirkAction;
import com.bezirk.proxy.api.impl.ZirkId;

/**
 * Pojo class for Discovery notifier containing all the fields that are necessary to give the
 * notification to the ProxyForBezirkLibrary.
 */
public final class DiscoveryIncomingMessage extends ServiceIncomingMessage {
    private final String discoveredList;
    private final int discoveryId;
    private final Boolean isSphereDiscovery;

    public DiscoveryIncomingMessage(ZirkId serviceId, String discoveredList, int discoveryId, Boolean isSphereDiscovery) {
        super(BezirkAction.ACTION_ZIRK_DISCOVER, serviceId);

        this.discoveredList = discoveredList;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = isSphereDiscovery;
    }

    public String getDiscoveredList() {
        return discoveredList;
    }

    public int getDiscoveryId() {
        return discoveryId;
    }

    public Boolean isSphereDiscovery() {
        return isSphereDiscovery;
    }
}
