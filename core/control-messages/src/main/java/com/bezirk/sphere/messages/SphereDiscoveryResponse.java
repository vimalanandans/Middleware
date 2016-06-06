package com.bezirk.sphere.messages;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;
import com.bezirk.proxy.api.impl.ZirkId;

import java.util.List;


/**
 * Control message for SphereCatchDiscoveryResponse to send the list of messages
 * FixMe: remove the multicast. create unicast message since we know the device detail
 */
public class SphereDiscoveryResponse extends MulticastControlMessage {
    private final static Discriminator discriminator = ControlMessage.Discriminator.SphereDiscoveryResponse;
    private List<ZirkId> services;

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
