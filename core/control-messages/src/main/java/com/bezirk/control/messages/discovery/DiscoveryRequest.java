package com.bezirk.control.messages.discovery;

import com.bezirk.control.messages.ControlMessage;
import com.bezirk.control.messages.MulticastControlMessage;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.ProtocolRole;
import com.bezirk.proxy.api.impl.BezirkZirkEndPoint;

public class DiscoveryRequest extends MulticastControlMessage {
    private final static Discriminator discriminator = ControlMessage.Discriminator.DiscoveryRequest;
    private final int discoveryId;
    private long timeout = 0;
    private int maxDiscovered = 0;
    private Location location;
    private ProtocolRole protocol;

    public DiscoveryRequest(String sphereId, BezirkZirkEndPoint sender, Location location, ProtocolRole protocol, int discoveryId, long timeout, int maxDiscovered) {
        super(sender, sphereId, discriminator);
        this.location = location;
        this.protocol = protocol;
        this.discoveryId = discoveryId;
        this.timeout = timeout;
        this.maxDiscovered = maxDiscovered;
    }

    public long getTimeout() {
        return timeout;
    }


    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }


    public int getMaxDiscovered() {
        return maxDiscovered;
    }


    public void setMaxDiscovered(int maxDiscovered) {
        this.maxDiscovered = maxDiscovered;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ProtocolRole getProtocol() {
        return protocol;
    }

    public void setProtocol(ProtocolRole protocol) {
        this.protocol = protocol;
    }

    public int getDiscoveryId() {
        return discoveryId;
    }


}
