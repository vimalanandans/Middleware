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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.Address;

/**
 * Base class for streams with multiple recipients. Extend {@link UnicastStream} for streams that
 * have only one recipient.
 *
 * @see Stream
 * @see UnicastStream
 * @see com.bezirk.middleware.addressing.Address
 * @see com.bezirk.middleware.addressing.Location
 */
public abstract class MulticastStream extends Stream {
    private final Address address;

    /**
     * Create a <code>Stream</code> with more than one recipient, where the set of recipients is
     * potentially more narrow or broader than simply every component subscribed to a
     * <code>topic</code> in a sphere. If <code>address</code> does not specify a
     * {@link com.bezirk.middleware.addressing.Location semantic address} that narrows the
     * recipient set, this stream is broadcast to every component subscribed to <code>topic</code>
     * in the sender's sphere(s). Otherwise, the stream is broadcast to those components in the
     * set described in the previous sentence that are specified by <code>address</code>. If the
     * <code>address</code> specifies a {@link com.bezirk.middleware.addressing.Pipe}, the stream
     * will also be sent outside of a sphere.
     *
     * @param flag    flag to mark the intent of this stream
     * @param topic   the pub-sub topic for this stream
     * @param address a specification of the components subscribed to <code>topic</code> that
     *                should receive this stream
     */
    public MulticastStream(Flag flag, String topic, Address address) {
        super(flag, topic);
        this.address = address;
    }

    /**
     * Returns the <code>address</code> associated with this stream. This address specifies which
     * components in the sender's spheres, subscribed to the <code>topic</code> specified in the
     * constructor, should receive this stream. The address may also specify a pipe to send the
     * stream outside of a sphere.
     *
     * @return the <code>address</code> associated with this stream
     */
    public Address getAddress() {
        return address;
    }
}
