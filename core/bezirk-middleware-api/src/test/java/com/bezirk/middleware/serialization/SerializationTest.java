package com.bezirk.middleware.serialization;

import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.addressing.RecipientSelector;

import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void testAddressSerializer() throws Exception {
        Location location = new Location("home", "garage", "garagedoor");
        URI uri = new URI("http://foo/bar");
        RecipientSelector recipientSelector = new RecipientSelector(location);

        String serializedAddress = recipientSelector.toJson();
        System.out.println("serialized recipientSelector    : " + serializedAddress);

        RecipientSelector newRecipientSelector = RecipientSelector.fromJson(serializedAddress);
        String serializedNewAddress = newRecipientSelector.toJson();
        System.out.println("serialized new recipientSelector: " + serializedNewAddress);

        assertTrue(serializedAddress.equals(serializedNewAddress));
    }

}
