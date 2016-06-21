package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.BezirkDiscoveredZirk;
import com.bezirk.network.BezirkNetworkUtilities;

import java.util.ArrayList;
import java.util.List;


public class DiscoveryResponse extends UnicastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.DiscoveryResponse;
    //ZirkEndPoint for this response only contains deviceID
    private static final BezirkZirkEndPoint sender = BezirkNetworkUtilities.getServiceEndPoint(null);
    private final List<BezirkDiscoveredZirk> serviceList;
    private final Integer reqDiscoveryId;


    public DiscoveryResponse(BezirkZirkEndPoint recipient, String sphereId, String reqKey, int discId) {
        super(sender, recipient, sphereId, discriminator, false, reqKey);
        //TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;
        this.serviceList = new ArrayList<BezirkDiscoveredZirk>();
    }

    public List<BezirkDiscoveredZirk> getZirkList() {
        return serviceList;
    }

    public Integer getReqDiscoveryId() {
        return reqDiscoveryId;
    }

}
