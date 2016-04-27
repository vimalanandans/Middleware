/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
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
