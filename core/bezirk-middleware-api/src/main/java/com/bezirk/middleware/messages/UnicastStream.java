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
package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.ServiceEndPoint;

/**
 * Base class for streams with one recipient. Extend {@link MulticastStream} for streams that
 * have more than one recipient.
 *
 * @see Stream
 * @see MulticastStream
 * @see com.bezirk.middleware.addressing.ServiceEndPoint
 */
public abstract class UnicastStream extends Stream {
    private final ServiceEndPoint recipient;

    /**
     * Create a <code>Stream</code> with one recipient where the recipient is in one of the sender's
     * spheres.
     *
     * @param flag      flag to mark the intent of this stream
     * @param topic     the pub-sub topic for this stream
     * @param recipient the specific component that should receive this stream
     */
    public UnicastStream(Flag flag, String topic, ServiceEndPoint recipient) {
        super(flag, topic);
        this.recipient = recipient;
    }

    /**
     * Returns the endpoint that is the recipient of this stream.
     *
     * @return the endpoint that is the recipient of this stream
     */
    public ServiceEndPoint getRecipient() {
        return recipient;
    }
}
