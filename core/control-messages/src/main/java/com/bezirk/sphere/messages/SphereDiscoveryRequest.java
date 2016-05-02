package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

/**
 * Control message for SphereDiscoveryRequest
 * FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryRequest extends MulticastControlMessage {
    private final static Discriminator discriminator = ControlMessage.Discriminator.SphereDiscoveryRequest;

    public SphereDiscoveryRequest(String scanSphereId,
                                  BezirkZirkEndPoint sender) {
        super(sender, scanSphereId, discriminator);
    }
}
