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
import com.bezirk.middleware.addressing.CloudPipe;
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
        Pipe pipe = new CloudPipe("boschPipe", uri);
        Address address = new Address(location, pipe);

        String serializedAddress = address.toJson();
        System.out.println("serialized address    : " + serializedAddress);

        Address newAddress = Address.fromJson(serializedAddress);
        String serializedNewAddress = newAddress.toJson();
        System.out.println("serialized new address: " + serializedNewAddress);

        assertTrue(serializedAddress.equals(serializedNewAddress));
    }

    @Test
    public void testPipeSerialization() throws Exception {
        URI uri = new URI("http://foo/bar");
        Pipe pipe = new CloudPipe("boschPipe", uri);

        String serializedPipe = pipe.serialize();
        System.out.println("Serialized pipe    : " + serializedPipe);

        Pipe newPipe = Pipe.deserialize(serializedPipe, CloudPipe.class);
        String serializedNewPipe = newPipe.serialize();
        System.out.println("Serialized new pipe: " + serializedNewPipe);

        assertTrue(serializedPipe.equals(serializedNewPipe));
    }

}
