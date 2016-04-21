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
package com.bezirk.middleware.serialization;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Pipe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AddressSerializer {

    private GsonBuilder builder = null;
    private Gson gson = null;

    public AddressSerializer() {
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Pipe.class, new InterfaceAdapter<Pipe>());
        gson = builder.create();
    }

    public String toJson(Address address) {
        return gson.toJson(address);
    }


    public Address fromJson(String serializedAddress) {
        return gson.fromJson(serializedAddress, Address.class);
    }

}
