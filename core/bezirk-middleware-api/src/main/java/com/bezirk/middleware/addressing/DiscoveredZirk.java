/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p/>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
 */
package com.bezirk.middleware.addressing;

import com.bezirk.middleware.BezirkListener;
import com.bezirk.middleware.messages.ProtocolRole;

import java.util.Set;

/**
 * Metadata for a Zirk subscribed to a particular
 * {@link com.bezirk.middleware.messages.ProtocolRole}. A Zirk may use
 * {@link com.bezirk.middleware.Bezirk#discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
 * to request the set of Zirks in its sphere(s) subscribed to a particular role. Implementations of
 * this interface are delivered to {@link com.bezirk.middleware.BezirkListener#discovered(Set)} in
 * reply the discovery request.
 */
public interface DiscoveredZirk {
    public boolean equals(Object obj);

    /**
     * An endpoint that can be used to unicast to a Zirk discovered by
     * {@link com.bezirk.middleware.Bezirk#discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
     * as subscribing to a particular role.
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

    public String getProtocol();

    /**
     * The semantic address of the Zirk subscribed to the targeted role
     *
     * @return the semantic address of the subscribing Zirk
     */
    public Location getLocation();
}
