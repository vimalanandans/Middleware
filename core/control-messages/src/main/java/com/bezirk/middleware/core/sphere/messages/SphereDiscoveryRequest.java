package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;

/**
 * Control message for SPHERE_DISCOVERY_REQUEST
 * FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryRequest extends MulticastControlMessage {
    private final static Discriminator discriminator = ControlMessage.Discriminator.SPHERE_DISCOVERY_REQUEST;

    public SphereDiscoveryRequest(String scanSphereId,
                                  BezirkZirkEndPoint sender) {
        super(sender, scanSphereId, discriminator);
    }
}
