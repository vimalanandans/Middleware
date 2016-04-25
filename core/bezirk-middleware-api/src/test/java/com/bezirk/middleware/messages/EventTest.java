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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This testcase verifies Event by setting the properties and retrieving them after deserialization.
 *
 * @author AJC6KOR
 */
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
