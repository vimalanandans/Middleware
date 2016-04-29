package com.bezirk.discovery;


import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

public class DiscoveryLabel {
    private final BezirkZirkEndPoint requester;
    private final int discoveryId;
    private final boolean isSphereDiscovery;


    public DiscoveryLabel(BezirkZirkEndPoint req, int discoveryId) {
        this.requester = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = false;

    }

    public DiscoveryLabel(BezirkZirkEndPoint req, int discoveryId, boolean isSphereDiscovery) {
        this.requester = req;
        this.discoveryId = discoveryId;
        this.isSphereDiscovery = isSphereDiscovery;
    }

    public boolean isSphereDiscovery() {
        return isSphereDiscovery;
    }

    public BezirkZirkEndPoint getRequester() {
        return requester;
    }

    public int getDiscoveryId() {
        return discoveryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DiscoveryLabel) {

            DiscoveryLabel curLbl = (DiscoveryLabel) obj;

            return this.requester.equals(curLbl.requester) && this.discoveryId == curLbl.discoveryId;

        }

        return false;

    }

    @Override
    public int hashCode() {
        return 1;
    }

}
