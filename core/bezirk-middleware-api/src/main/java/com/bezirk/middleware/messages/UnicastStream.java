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

import com.bezirk.middleware.addressing.ZirkEndPoint;

/**
 * Base class for streams with one recipient. Extend {@link MulticastStream} for streams that
 * have more than one recipient.
 *
 * @see Stream
 * @see MulticastStream
 * @see ZirkEndPoint
 */
public abstract class UnicastStream extends Stream {
    private final ZirkEndPoint recipient;

    /**
     * Create a <code>Stream</code> with one recipient where the recipient is in one of the sender's
     * spheres.
     *
     * @param flag      flag to mark the intent of this stream
     * @param topic     the pub-sub topic for this stream
     * @param recipient the specific component that should receive this stream
     */
    public UnicastStream(Flag flag, String topic, ZirkEndPoint recipient) {
        super(flag, topic);
        this.recipient = recipient;
    }

    /**
     * Returns the endpoint that is the recipient of this stream.
     *
     * @return the endpoint that is the recipient of this stream
     */
    public ZirkEndPoint getRecipient() {
        return recipient;
    }
}
