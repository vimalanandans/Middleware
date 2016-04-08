/**
 * Copyright (C) 2014 Robert Bosch, LLC. All Rights Reserved.
 *
 * Authors: Joao de Sousa, 2014
 *          Mansimar Aneja, 2014
 *          Vijet Badigannavar, 2014
 *          Samarjit Das, 2014
 *          Cory Henson, 2014
 *          Sunil Kumar Meena, 2014
 *          Adam Wynne, 2014
 *          Jan Zibuschka, 2014
 */
package com.bezirk.api.messages;

import com.bezirk.api.addressing.ServiceEndPoint;

/**
 */
public class UnicastStream extends Stream {

    private final ServiceEndPoint recipient;

    public UnicastStream(Stripe stripe, String topic, ServiceEndPoint recipient) {
        super(stripe, topic);
        this.recipient = recipient;
    }

    public ServiceEndPoint getRecipient() {
        return recipient;
    }
}
