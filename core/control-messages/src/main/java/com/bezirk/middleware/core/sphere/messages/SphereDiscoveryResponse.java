package com.bezirk.middleware.core.sphere.messages;

import com.bezirk.middleware.core.control.messages.ControlMessage;
import com.bezirk.middleware.core.control.messages.MulticastControlMessage;
import com.bezirk.middleware.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.middleware.proxy.api.impl.ZirkId;

import java.util.List;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of messages
 * FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryResponse extends MulticastControlMessage {
    private static final Discriminator discriminator = ControlMessage.Discriminator.SPHERE_DISCOVERY_RESPONSE;
    private final List<ZirkId> services;

    /**
     * Constructor
     *
     * @param scannedSphereId sphere to be created at the recipient device
     * @param services        Services that need to join the target sphere
     */

    public SphereDiscoveryResponse(String scannedSphereId,
                                   List<ZirkId> services, BezirkZirkEndPoint sender) {
        super(sender, scannedSphereId, discriminator);
        this.services = services;

    }

    /**
     * @return the services
     */
    public final List<ZirkId> getServices() {
        return services;
    }


}
