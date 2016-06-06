package com.bezirk.middleware.messages;

import com.bezirk.middleware.addressing.RecipientSelector;
import com.bezirk.middleware.addressing.Location;
import com.bezirk.middleware.messages.Message.Flag;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MulticastStreamTest {

    @Test
    public void test() {

        Flag flag = Flag.NOTICE;
        String topic = "TestTopic";
        Location loc = new Location("OFFICE1/BLOCk1/FLOOR1");
        RecipientSelector recipientSelector = new RecipientSelector(loc);
        com.bezirk.middleware.messages.MulticastStream multicastStream = new MockMulticastStream(flag, topic, recipientSelector);

        String serializedMulticastStream = multicastStream.toJson();

        MulticastStream deserializedMulticastStream = MulticastStream.fromJson(serializedMulticastStream, MockMulticastStream.class);

        assertEquals("RecipientSelector is not equal to the set value. ", recipientSelector.getLocation(), deserializedMulticastStream.getRecipientSelector().getLocation());
        assertEquals("Flag is not equal to the set value. ", flag, deserializedMulticastStream.flag);
        assertEquals("Topic is not equal to the set value. ", topic, deserializedMulticastStream.topic);
    }

    private class MockMulticastStream extends MulticastStream {
        MockMulticastStream(Flag flag, String topic, RecipientSelector recipientSelector) {
            super(flag, topic, recipientSelector);
        }
    }
}
