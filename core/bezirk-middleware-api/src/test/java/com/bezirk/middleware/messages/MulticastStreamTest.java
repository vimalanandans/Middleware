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

import com.bezirk.middleware.addressing.Address;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies the MulticastStream by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
public class MulticastStreamTest {

    @Test
    public void test() {

        Flag flag = Flag.NOTICE;
        String topic = "TestTopic";
        Location loc = new Location("OFFICE1/BLOCk1/FLOOR1");
        Address address = new Address(loc);
        com.bezirk.middleware.messages.MulticastStream multicastStream = new MockMulticastStream(flag, topic, address);

        String serializedMulticastStream = multicastStream.serialize();

        MulticastStream deserializedMulticastStream = MulticastStream.deserialize(serializedMulticastStream, MockMulticastStream.class);

        assertEquals("Address is not equal to the set value. ", address.getLocation(), deserializedMulticastStream.getAddress().getLocation());
        assertEquals("Flag is not equal to the set value. ", flag, deserializedMulticastStream.flag);
        assertEquals("Topic is not equal to the set value. ", topic, deserializedMulticastStream.topic);
    }

    private class MockMulticastStream extends MulticastStream {
        MockMulticastStream(Flag flag, String topic, Address address) {
            super(flag, topic, address);
        }
    }
}
