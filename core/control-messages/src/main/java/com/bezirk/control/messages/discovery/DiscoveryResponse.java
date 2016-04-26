package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.UnicastControlMessage;
import com.bezirk.proxy.api.impl.UhuDiscoveredZirk;
import com.bezirk.proxy.api.impl.UhuZirkEndPoint;
import com.bezrik.network.UhuNetworkUtilities;

import java.util.ArrayList;
import java.util.List;


public class DiscoveryResponse extends UnicastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.DiscoveryResponse;
    //ZirkEndPoint for this response only contains deviceID
    private static final UhuZirkEndPoint sender = UhuNetworkUtilities.getServiceEndPoint(null);
    private final List<UhuDiscoveredZirk> serviceList;
    private final Integer reqDiscoveryId;


    public DiscoveryResponse(UhuZirkEndPoint recipient, String sphereId, String reqKey, int discId) {
        super(sender, recipient, sphereId, discriminator, false, reqKey);
        //TODO: Investigate if this is really needed
        this.reqDiscoveryId = discId;
        this.serviceList = new ArrayList<UhuDiscoveredZirk>();
    }

    public List<UhuDiscoveredZirk> getServiceList() {
        return serviceList;
    }

    public Integer getReqDiscoveryId() {
        return reqDiscoveryId;
    }

}
