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

import com.bezirk.api.addressing.Address;

/**
 * Created by sme6kor on 7/4/2014.
 */
public class MulticastStream extends Stream{

    private final Address address;

    public MulticastStream(Stripe stripe, String topic, Address address) {
        super(stripe, topic);
        this.address = address;
    }

    public Address getAddress() {
        return address;
    }
}
