/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 * <p>
 * Authors: Joao de Sousa, 2014
 * Mansimar Aneja, 2014
 * Vijet Badigannavar, 2014
 * Samarjit Das, 2014
 * Cory Henson, 2014
 * Sunil Kumar Meena, 2014
 * Adam Wynne, 2014
 * Jan Zibuschka, 2014
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
