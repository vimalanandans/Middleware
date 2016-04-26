package com.bezirk.discovery;


import com.bezirk.proxy.api.impl.UhuZirkEndPoint;

public class DiscoveryLabel {
    private final UhuZirkEndPoint requestor;
    private final int discoveryId;
    private final boolean isSphereDiscovery;


    public DiscoveryLabel(UhuZirkEndPoint req, int discoveryId) {
        this.requestor = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = false;

    }

    public DiscoveryLabel(UhuZirkEndPoint req, int discoveryId, boolean isSphereDiscovery) {
        this.requestor = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = isSphereDiscovery;
    }

    public boolean isSphereDiscovery() {
        return isSphereDiscovery;
    }

    public UhuZirkEndPoint getRequester() {
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
