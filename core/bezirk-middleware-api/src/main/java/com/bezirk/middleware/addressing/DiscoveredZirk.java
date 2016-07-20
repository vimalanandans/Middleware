package com.bezirk.middleware.addressing;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.ProtocolRole;

import java.util.Set;

/**
 * Metadata for a Zirk subscribed to a particular
 * {@link com.bezirk.middleware.messages.ProtocolRole}. A Zirk may use
 * {@link com.bezirk.middleware.Bezirk#discover(RecipientSelector, ProtocolRole, long, int, BezirkListener)}
 * to request the set of Zirks in its sphere(s) subscribed to a particular role. Implementations of
 * this interface are delivered to {@link com.bezirk.middleware.BezirkListener#discovered(Set)} in
 * reply the discovery request.
 */
public interface DiscoveredZirk {
    public boolean equals(Object obj);

    /**
     * An endpoint that can be used to unicast to a Zirk discovered by
     * {@link com.bezirk.middleware.Bezirk#discover(RecipientSelector, ProtocolRole, long, int, BezirkListener)}.
     *
     * @return the endpoint of the subscribing Zirk
     */
    public ZirkEndPoint getZirkEndPoint();

    /**
     * The name of the Zirk subscribed to the targeted role.
     *
     * @return the name of the subscribing Zirk
     */
    public String getZirkName();

    /**
     * Get the <code>ProtocolRole</code> that was searched for using the
     * {@link com.bezirk.middleware.Bezirk#discover(RecipientSelector, ProtocolRole, long, int, BezirkListener)
     * Discovery API} to discover this Zirk.
     *
     * @return the <code>ProtocolRole</code> that was searched for to discover the Zirk
     */
    public ProtocolRole getProtocolRole();

    /**
     * The semantic address of the Zirk subscribed to the targeted role
     *
     * @return the semantic address of the subscribing Zirk
     */
    public Location getLocation();
}
