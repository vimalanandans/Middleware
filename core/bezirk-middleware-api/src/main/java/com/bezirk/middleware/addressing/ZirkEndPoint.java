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
 * An end point represents a Zirk that another Zirk can directly send messages to (e.g. unicast).
 * There are a couple ways to get a Zirk's <code>ZirkEndPoint</code> to directly send it messages:
 * <ul>
 * <li>Wait to receive a message from the Zirk, in which case the <code>ZirkEndPoint</code>
 * will be received by the appropriate callback in {@link com.bezirk.middleware.BezirkListener}
 * (e.g. {@link com.bezirk.middleware.BezirkListener#receiveEvent(String, String, ZirkEndPoint)}).</li>
 * <li>Use {@link com.bezirk.middleware.Bezirk#discover(ZirkId, RecipientSelector, ProtocolRole, long, int, BezirkListener)}
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
