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

import com.bezirk.middleware.addressing.RecipientSelector;

/**
 * Base class for streams with multiple recipients. Extend {@link UnicastStream} for streams that
 * have only one recipient.
 *
 * @see Stream
 * @see UnicastStream
 * @see RecipientSelector
 * @see com.bezirk.middleware.addressing.Location
 */
public abstract class MulticastStream extends Stream {
    private final RecipientSelector recipientSelector;

    /**
     * Create a <code>Stream</code> with more than one recipient, where the set of recipients can
     * be different from the set of Zirks subscribed to this <code>topic</code> in the sender's
     * sphere(s). If <code>recipientSelector</code> does not specify a
     * {@link com.bezirk.middleware.addressing.Location semantic recipientSelector} that narrows the
     * recipient set, this stream is broadcast to every Zirk subscribed to the <code>topic</code>
     * in the sender's sphere(s). Otherwise, the stream is broadcast to the set of Zirks specified
     * by the semantic recipientSelector. If the <code>recipientSelector</code> specifies a
     * {@link com.bezirk.middleware.addressing.Pipe}, the stream will also be sent outside of a
     * sphere.
     *
     * @param flag              flag to mark the intent of this stream
     * @param topic             the pub-sub topic for this stream
     * @param recipientSelector a specification of the Zirks subscribed to <code>topic</code> that
     *                          should receive this stream
     */
    public MulticastStream(Flag flag, String topic, RecipientSelector recipientSelector) {
        super(flag, topic);
        this.recipientSelector = recipientSelector;
    }

    /**
     * Returns the <code>recipientSelector</code> associated with this stream. This recipientSelector specifies which
     * Zirks in the sender's spheres, subscribed to the <code>topic</code> specified in the
     * constructor, should receive this stream. The recipientSelector may also specify a pipe to send the
     * stream outside of a sphere.
     *
     * @return the <code>recipientSelector</code> associated with this stream
     */
    public RecipientSelector getRecipientSelector() {
        return recipientSelector;
    }
}
