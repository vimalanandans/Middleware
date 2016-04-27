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
package com.bezirk.middleware.serialziation;

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.Pipe;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void testAddressSerializer() throws Exception {
        Location location = new Location("home", "garage", "garagedoor");
        URI uri = new URI("http://foo/bar");
        Address address = new Address(location);

        String serializedAddress = address.toJson();
        System.out.println("serialized address    : " + serializedAddress);

        Address newAddress = Address.fromJson(serializedAddress);
        String serializedNewAddress = newAddress.toJson();
        System.out.println("serialized new address: " + serializedNewAddress);

        assertTrue(serializedAddress.equals(serializedNewAddress));
    }

}
