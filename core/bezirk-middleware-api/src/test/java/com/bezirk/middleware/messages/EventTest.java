package com.bezirk.middleware.messages;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventTest {


    @Test
    public void test() {

        String topic = "TestEvent";
        Message.Flag flag = Message.Flag.NOTICE;
        com.bezirk.middleware.messages.Event event = new Event(flag, topic);

        String serializedEvent = event.toJson();

        Event deserializedEvent = Event.fromJson(serializedEvent, Event.class);

        assertEquals("Flag is not equal to the set value.", flag, deserializedEvent.flag);
        assertEquals("Topic is not equal to the set value.", topic, deserializedEvent.topic);
    }

}
