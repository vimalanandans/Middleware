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
 */
public abstract class UnicastStream extends Stream {

    private final ServiceEndPoint recipient;

    public UnicastStream(Flag flag, String topic, ServiceEndPoint recipient) {
        super(flag, topic);
        this.recipient = recipient;
    }

    public ServiceEndPoint getRecipient() {
        return recipient;
    }
}
