package com.bezirk.discovery;


import com.bezirk.proxy.api.impl.UhuServiceEndPoint;

public class DiscoveryLabel {
    private final UhuServiceEndPoint requestor;
    private final int discoveryId;
    private final boolean isSphereDiscovery;


    public DiscoveryLabel(UhuServiceEndPoint req, int discoveryId) {
        this.requestor = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = false;

    }

    public DiscoveryLabel(UhuServiceEndPoint req, int discoveryId, boolean isSphereDiscovery) {
        this.requestor = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = isSphereDiscovery;
    }

    public boolean isSphereDiscovery() {
        return isSphereDiscovery;
    }

    public UhuServiceEndPoint getRequester() {
        return requestor;
    }

    public int getDiscoveryId() {
        return discoveryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiscoveryLabel) {

            DiscoveryLabel curLbl = (DiscoveryLabel) obj;

            return this.requestor.equals(curLbl.requestor) && this.discoveryId == curLbl.discoveryId;

        }

        return false;

    }

    @Override
    public int hashCode() {
        return 1;
    }

}
