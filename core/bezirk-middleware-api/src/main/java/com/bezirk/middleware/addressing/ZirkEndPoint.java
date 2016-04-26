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
 * An end point represents a Zirk that another Zirk can directly send messages to (e.g. unicast).
 * There are a couple ways to get a Zirk's <code>ZirkEndPoint</code> to directly send it messages:
 * <ul>
 * <li>Wait to receive a message from the Zirk, in which case the <code>ZirkEndPoint</code>
 * will be received by the appropriate callback in {@link com.bezirk.middleware.BezirkListener}
 * (e.g. {@link com.bezirk.middleware.BezirkListener#receiveEvent(String, String, ZirkEndPoint)}).</li>
 * <li>Use {@link com.bezirk.middleware.Bezirk#discover(ZirkId, Address, ProtocolRole, long, int, BezirkListener)}
 * to discover all Zirks that subscribe to a particular {@link ProtocolRole}. The results set
 * retrieved from {@link BezirkListener#discovered(Set)} contains metadata for each discovered
 * Zirk, including each <code>ZirkEndPoint</code></li>
 * </ul>
 * <p>
 * The Bezirk middleware implements this interface.
 * </p>
 */
public interface ZirkEndPoint {
// For now, this is a marker interface because there is nothing to offer to a Zirk API wise.
}
